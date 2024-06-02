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

public class HttpTaskManagerHistoryTest {
    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager); //тут подчеркивается предупреждение
    // Instantiation of utility class 'HttpTaskServer'
    // но я не знаю как по-другому можно вызвать нужный конструктор
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerHistoryTest() { //зачем мы тут во всех тестах пишем конструктор? Я взяла это из примера в ТЗ,
        // но не понимаю зачем он нужен, если он пустой. или в него надо инициализацию полей вынести?
    }

    @BeforeEach
    public void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        //taskServer.start(); //было предупреждение, поменяла на вызов через указание класса, или так не надо было?
        HttpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        //taskServer.stop();
        HttpTaskServer.stop();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        //создаем задачу, эпик, подзадачи и get их
        Task task1 = new Task("Task1 name",
                "Task1 description",
                Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 5, 2, 15, 0));
        int task1id = manager.createTask(task1);

        Epic epic = new Epic("Тестовый эпик", "Описание");
        int epicId = manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Test 1", "Testing task 1",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 1, 12, 20), epicId);
        Subtask subtask2 = new Subtask("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.of(2024, 5, 1, 12, 30), epicId);

        int subtask1Id = manager.createSubtask(subtask1);
        int subtask2Id = manager.createSubtask(subtask2);

        manager.getSubtaskById(subtask2Id);
        manager.getEpicById(epicId);
        manager.getTaskById(task1id);
        manager.getSubtaskById(subtask1Id);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        // отправляем request и получаем response
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Task> historyFromManager = manager.getHistoryManager().getHistory();
        List<Task> historyFromResponse = gson.fromJson(response.body(), new HttpTaskManagerTasksTest.TaskListTypeToken().getType());

        assertEquals(historyFromManager.size(), historyFromResponse.size(), "Длина списка не совпадает");
        assertTrue(Task.taskFieldsExceptIdEquals(historyFromManager.get(0), historyFromResponse.get(0)), "Поля не совпадают");
        assertTrue(Task.taskFieldsExceptIdEquals(historyFromManager.get(1), historyFromResponse.get(1)), "Поля не совпадают");
        assertTrue(Task.taskFieldsExceptIdEquals(historyFromManager.get(2), historyFromResponse.get(2)), "Поля не совпадают");
        assertTrue(Task.taskFieldsExceptIdEquals(historyFromManager.get(3), historyFromResponse.get(3)), "Поля не совпадают");
    }
}
