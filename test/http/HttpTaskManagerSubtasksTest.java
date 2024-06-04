package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class HttpTaskManagerSubtasksTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    static class SubtaskListTypeToken extends TypeToken<List<Subtask>> {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        // создаём эпик и подзадачу
        Epic epic = new Epic("Тестовый эпик", "Описание");
        int epicId = manager.createEpic(epic);

        Subtask subtask = new Subtask("Test", "Testing task",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 1, 12, 20), epicId);
        // конвертируем её в JSON
        String taskJson = gson.toJson(subtask);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> subtasksFromManager = manager.getAllSubtasksList();

        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test", subtasksFromManager.getFirst().getName(), "Некорректное имя подзадачи");
        assertTrue(Subtask.subtaskFieldsExceptIdEquals(subtask, subtasksFromManager.getFirst()), "Некорректные поля подзадачи");
    }


    @Test
    public void testGetSubtasks() throws IOException, InterruptedException {
        // создаём эпик и подзадачи
        Epic epic = new Epic("Тестовый эпик", "Описание");
        int epicId = manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 1, 12, 20), epicId);
        Subtask subtask2 = new Subtask("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 1, 12, 30), epicId);
        Subtask subtask3 = new Subtask("Test 3", "Testing task 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 1, 12, 40), epicId);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromManager = manager.getAllSubtasksList();
        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());

        assertTrue(Subtask.subtaskFieldsExceptIdEquals(subtasksFromManager.get(0), subtasksFromResponse.get(0)), "Поля задач не совпадают");
        assertEquals(subtasksFromManager.get(0), subtasksFromResponse.get(0), "Подзадачи не совпадают");
        assertTrue(Subtask.subtaskFieldsExceptIdEquals(subtasksFromManager.get(1), subtasksFromResponse.get(1)), "Поля задач не совпадают");
        assertEquals(subtasksFromManager.get(1), subtasksFromResponse.get(1), "Подзадачи не совпадают");
        assertTrue(Subtask.subtaskFieldsExceptIdEquals(subtasksFromManager.get(2), subtasksFromResponse.get(2)), "Поля задач не совпадают");
        assertEquals(subtasksFromManager.get(2), subtasksFromResponse.get(2), "Подзадачи не совпадают");
    }


    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        // создаём эпик и подзадачи
        Epic epic = new Epic("Тестовый эпик", "Описание");
        int epicId = manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 1, 12, 20), epicId);
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 1, 12, 30), epicId);
        int subtask2Id = manager.createSubtask(subtask2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask2Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Subtask subtaskFromResponse = gson.fromJson(response.body(), Subtask.class);

        assertTrue(Subtask.subtaskFieldsExceptIdEquals(manager.getSubtaskById(subtask2Id), subtaskFromResponse), "Поля подзадач не совпадают");
        assertEquals(manager.getSubtaskById(subtask2Id), subtaskFromResponse, "Подзадачи не совпадают");
    }


    @Test
    public void shouldGet404WhenSubtaskDoesNotExist() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }


    @Test
    public void shouldGet406WhenIntersects() throws IOException, InterruptedException {
        // создаём эпик и подзадачи
        Epic epic = new Epic("Тестовый эпик", "Описание");
        int epicId = manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epicId);
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epicId);

        // конвертируем её в JSON
        String taskJson = gson.toJson(subtask2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        // создаём эпик и подзадачи
        Epic epic = new Epic("Тестовый эпик", "Описание");
        int epicId = manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 1, 12, 20), epicId);
        int subtaskId = manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test 1 updated", "Testing task 1 updated",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 1, 12, 20), epicId);
        subtask2.setId(subtaskId);

        // конвертируем её в JSON
        String taskJson = gson.toJson(subtask2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        assertEquals("Test 1 updated",
                manager.getSubtaskById(subtaskId).getName(),
                "Имя подзадачи не равно ожидаемому после обновления");
        assertTrue(Subtask.subtaskFieldsExceptIdEquals(subtask2, manager.getSubtaskById(subtaskId)), "Поля подзадач не совпадают");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        // создаём эпик и подзадачи
        Epic epic = new Epic("Тестовый эпик", "Описание");
        int epicId = manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epicId);
        Subtask subtask2 = new Subtask("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now().plusMinutes(10), epicId);
        manager.createSubtask(subtask1);
        int subtask2Id = manager.createSubtask(subtask2);


        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask2Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        assertFalse(manager.getAllSubtasksList().contains(subtask2), "Подзадача не удалена");
    }

}