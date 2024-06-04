package http;

import com.google.gson.Gson;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskManagerPrioritizedTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();

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
    public void testGetPrioritized() throws IOException, InterruptedException {
        //создаем задачу, эпик, подзадачи
        Task task1 = new Task("Task1 name",
                "Task1 description",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 5, 2, 15, 0));
        manager.createTask(task1);

        Epic epic = new Epic("Тестовый эпик", "Описание");
        int epicId = manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 1, 12, 20), epicId);
        Subtask subtask2 = new Subtask("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 1, 12, 30), epicId);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Task> prioritizedFromManager = manager.getPrioritizedTasks();
        List<Task> prioritizedFromResponse = gson.fromJson(response.body(), new HttpTaskManagerTasksTest.TaskListTypeToken().getType());

        assertEquals(prioritizedFromManager.size(), prioritizedFromResponse.size(), "Длина списка не совпадает");
        assertTrue(Task.taskFieldsExceptIdEquals(prioritizedFromManager.get(0), prioritizedFromResponse.get(0)), "Поля не совпадают");
        assertTrue(Task.taskFieldsExceptIdEquals(prioritizedFromManager.get(1), prioritizedFromResponse.get(1)), "Поля не совпадают");
        assertTrue(Task.taskFieldsExceptIdEquals(prioritizedFromManager.get(2), prioritizedFromResponse.get(2)), "Поля не совпадают");
    }
}
