package http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerEpicsTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

    static class EpicsListTypeToken extends TypeToken<List<Epic>> {
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
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём эпик
        Epic epic = new Epic("Test 1",
                "Testing epic 1");
        // конвертируем её в JSON
        String taskJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> epicsFromManager = manager.getAllEpicsList();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test 1", epicsFromManager.getFirst().getName(), "Некорректное имя эпика");
        assertTrue(Epic.epicsNameAndDescriptionFieldsEquals(epic, epicsFromManager.getFirst()), "Некорректные поля эпика");
    }


    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        // создаём эпики
        Epic epic1 = new Epic("Test 1",
                "Testing epic 1");

        Epic epic2 = new Epic("Test 2",
                "Testing epic 2");

        Epic epic3 = new Epic("Test 3",
                "Testing epic 3");

        manager.createEpic(epic1);
        manager.createEpic(epic2);
        manager.createEpic(epic3);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpicsList();
        List<Epic> epicsFromResponse = gson.fromJson(response.body(), new EpicsListTypeToken().getType());

        assertTrue(Epic.epicsNameAndDescriptionFieldsEquals(epicsFromManager.get(0), epicsFromResponse.get(0)), "Поля эпиков не совпадают");
        assertEquals(epicsFromManager.get(0), epicsFromResponse.get(0), "Эпики не совпадают");
        assertTrue(Epic.epicsNameAndDescriptionFieldsEquals(epicsFromManager.get(1), epicsFromResponse.get(1)), "Поля эпиков не совпадают");
        assertEquals(epicsFromManager.get(1), epicsFromResponse.get(1), "Эпики не совпадают");
        assertTrue(Epic.epicsNameAndDescriptionFieldsEquals(epicsFromManager.get(2), epicsFromResponse.get(2)), "Поля эпиков не совпадают");
        assertEquals(epicsFromManager.get(2), epicsFromResponse.get(2), "Эпики не совпадают");
    }


    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        // создаём эпики
        Epic epic1 = new Epic("Test 1",
                "Testing epic 1");

        Epic epic2 = new Epic("Test 2",
                "Testing epic 2");

        manager.createEpic(epic1);
        int epic2id = manager.createEpic(epic2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic2id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);

        assertTrue(Epic.epicsNameAndDescriptionFieldsEquals(manager.getEpicById(epic2id), epicFromResponse), "Поля эпиков не совпадают");
        assertEquals(manager.getEpicById(epic2id), epicFromResponse, "Эпики не совпадают");
    }


    @Test
    public void shouldGet404WhenEpicDoesNotExist() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }


    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        // создаём эпики

        Epic epic1 = new Epic("Test 1",
                "Testing epic 1");
        int epic1id = manager.createEpic(epic1);

        Epic epic2 = new Epic("Test 1 updated",
                "Testing epic 1 updated");
        epic2.setId(epic1id);

        // конвертируем её в JSON

        String taskJson = gson.toJson(epic2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        assertEquals("Test 1 updated",
                manager.getEpicById(epic1id).getName(),
                "Имя эпика не равно ожидаемому после обновления");
        assertTrue(Epic.epicsNameAndDescriptionFieldsEquals(epic2, manager.getEpicById(epic1id)), "Поля эпиков не совпадают");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        // создаём эпики
        Epic epic1 = new Epic("Test 1",
                "Testing epic 1");

        Epic epic2 = new Epic("Test 2",
                "Testing epic 2");

        Epic epic3 = new Epic("Test 3",
                "Testing epic 3");

        manager.createEpic(epic1);
        int epic2id = manager.createEpic(epic2);
        manager.createEpic(epic3);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic2id);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());
        assertFalse(manager.getAllEpicsList().contains(epic2), "Задача не удалена");
    }

}