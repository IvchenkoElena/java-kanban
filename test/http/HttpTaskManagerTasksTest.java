package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Status;
import model.Task;
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


public class HttpTaskManagerTasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    static class TaskListTypeToken extends TypeToken<List<Task>> {
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
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test", "Testing task",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 2, 15, 0));
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasksList();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        assertTrue(Task.taskFieldsExceptIdEquals(task, tasksFromManager.getFirst()), "Некорректные поля задачи");
    }


    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        // создаём задач
        Task task1 = new Task("Task1 name",
                "Task1 description",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 5, 2, 15, 0));

        Task task2 = new Task("Task2 name",
                "Task2 description",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 5, 2, 16, 0));

        Task task3 = new Task("Task3 name",
                "Task3 description",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 5, 2, 17, 0));

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = manager.getAllTasksList();
        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertTrue(Task.taskFieldsExceptIdEquals(tasksFromManager.get(0), tasksFromResponse.get(0)), "Поля задач не совпадают");
        assertEquals(tasksFromManager.get(0), tasksFromResponse.get(0), "Задачи не совпадают");
        assertTrue(Task.taskFieldsExceptIdEquals(tasksFromManager.get(1), tasksFromResponse.get(1)), "Поля задач не совпадают");
        assertEquals(tasksFromManager.get(1), tasksFromResponse.get(1), "Задачи не совпадают");
        assertTrue(Task.taskFieldsExceptIdEquals(tasksFromManager.get(2), tasksFromResponse.get(2)), "Поля задач не совпадают");
        assertEquals(tasksFromManager.get(2), tasksFromResponse.get(2), "Задачи не совпадают");
    }


    @Test
    public void testGetTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task("Task1 name",
                "Task1 description",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 5, 2, 15, 0));

        Task task2 = new Task("Task2 name",
                "Task2 description",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 5, 2, 16, 0));

        manager.createTask(task1);
        int task2Id = manager.createTask(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task2Id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Task taskFromResponse = gson.fromJson(response.body(), Task.class);

        assertTrue(Task.taskFieldsExceptIdEquals(manager.getTaskById(task2Id), taskFromResponse),
                "Поля задач не совпадают");
        assertEquals(manager.getTaskById(task2Id), taskFromResponse, "Задачи не совпадают");
    }


    @Test
    public void shouldGet404WhenTaskDoesNotExist() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }


    @Test
    public void shouldGet406WhenIntersects() throws IOException, InterruptedException {

        // создаём задачу
        Task task1 = new Task("Task 1", "Desc task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        manager.createTask(task1);

        Task task2 = new Task("Task 2", "Desc task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        // конвертируем её в JSON
        String taskJson = gson.toJson(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        // создаём задачи
        Task task1 = new Task("Task1 name",
                "Task1 description",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 5, 2, 15, 0));

        int taskId = manager.createTask(task1);

        Task task2 = new Task("Task1 name updated",
                "Task1 description updated",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 5, 2, 16, 0));
        task2.setId(taskId);

        // конвертируем её в JSON

        String taskJson = gson.toJson(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        assertEquals("Task1 name updated",
                manager.getTaskById(taskId).getName(),
                "Имя задачи не равно ожидаемому после обновления");
        assertTrue(Task.taskFieldsExceptIdEquals(task2, manager.getTaskById(taskId)), "Поля задач не совпадают");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task("Task1 name",
                "Task1 description",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 5, 2, 15, 0));

        Task task2 = new Task("Task2 name",
                "Task2 description",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 5, 2, 16, 0));

        Task task3 = new Task("Task3 name",
                "Task3 description",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 5, 2, 17, 0));

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        assertFalse(manager.getAllTasksList().contains(task2), "Задача не удалена");
    }

}