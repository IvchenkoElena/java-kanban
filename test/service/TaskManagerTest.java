import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    static final TaskManager taskManager = Managers.getDefault();
    private int id = 0;

    @Test
    void generateId() {
        int id1 = taskManager.generateId();
        int id2 = taskManager.generateId();
        assertEquals(id1 + 1, id2, "ID должен увеличиться на 1");
    }

    @Test
    void createdTaskExists() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
    }

    @Test
    void updateTask() {
        Task task1 = new Task("Задача 1", "Купить продукты");
        final int taskId = taskManager.createTask(task1);
        final Task savedTask = taskManager.getTaskById(taskId);
        assertEquals(task1, savedTask, "Проверка созданной задачи не прошла");
        Task newTask1 = new Task("Задача 1", "Купить много продуктов");
        newTask1.setStatus(Status.DONE);
        newTask1.setId(taskId);
        taskManager.updateTask(newTask1);
        final Task newSavedTask = taskManager.getTaskById(taskId);
        assertEquals(newTask1, newSavedTask, "Обновленная задача не совпадает");
    }

    @Test
    void getAllTasksList() {
        taskManager.deleteAllTasks();
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void deleteAllTasks() {
        taskManager.deleteAllTasks();
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description");
        taskManager.createTask(task2);
        final List<Task> tasks = taskManager.getAllTasksList();
        assertEquals(2, tasks.size(), "Неверное количество задач.");
        taskManager.deleteAllTasks();
        final List<Task> newTasks = taskManager.getAllTasksList();

        assertEquals(0, newTasks.size(), "Неверное количество задач.");
    }

    @Test
    void getTaskById() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void deleteTaskById() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        taskManager.deleteTaskById(taskId);

        final Task newSavedTask = taskManager.getTaskById(taskId);

        assertNull(newSavedTask, "Задача не удалилась.");
    }

    @Test
    void createEpic() {
        Epic renovation = new Epic("Ремонт", "Сделать ремонт");
        taskManager.createEpic(renovation);
        int renovationId = renovation.getId();
        Subtask wall = new Subtask("Стены", "Шпаклюем и штукатурим", renovationId);
        taskManager.createSubtask(wall);
        Subtask furniture = new Subtask("Мебель", "Купить и собрать", renovationId);
        taskManager.createSubtask(furniture);
        final Epic savedEpic = taskManager.getEpicById(renovationId);

        assertEquals(renovation, savedEpic, "Проверка созданного эпика не прошла");
    }

    @Test
    void getAllEpicsList() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", epic1Id);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epic1Id);
        taskManager.createSubtask(subtask2);
        final Epic savedEpic = taskManager.getEpicById(epic1Id);

        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        taskManager.createEpic(epic2);
        int epic2Id = epic1.getId();
        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", epic2Id);
        taskManager.createSubtask(subtask3);
        Subtask subtask4 = new Subtask("Подзадача4", "Описание подзадачи4", epic2Id);
        taskManager.createSubtask(subtask4);
        final Epic savedEpic2 = taskManager.getEpicById(epic2Id);

        final List<Epic> epics = taskManager.getAllEpicsList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество задач.");
        assertEquals(savedEpic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", epic1Id);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epic1Id);
        taskManager.createSubtask(subtask2);

        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        taskManager.createEpic(epic2);
        int epic2Id = epic1.getId();
        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", epic2Id);
        taskManager.createSubtask(subtask3);
        Subtask subtask4 = new Subtask("Подзадача4", "Описание подзадачи4", epic2Id);
        taskManager.createSubtask(subtask4);

        final List<Epic> epics = taskManager.getAllEpicsList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(2, epics.size(), "Неверное количество задач.");

        taskManager.deleteAllEpics();
        final List<Epic> newEpicsList = taskManager.getAllEpicsList();

        assertEquals(0, newEpicsList.size(), "Неверное количество задач.");
    }

    @Test
    void getEpicById() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();
        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи1", epic1Id);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи2", epic1Id);
        taskManager.createSubtask(subtask2);
        final Epic savedEpic = taskManager.getEpicById(epic1Id);
        assertEquals(epic1, savedEpic, "Задачи не совпадают.");
    }

    @Test
    void deleteEpicById() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();
        assertNotNull(taskManager.getEpicById(epic1Id), "Задача должна быть.");

        taskManager.deleteEpicById(epic1Id);

        //assertEquals(epic1, savedEpic, "Задачи не совпадают.");
        assertNull(taskManager.getEpicById(epic1Id), "Задачи недолжно быть");
    }

    @Test
    void createSubtask() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        taskManager.createEpic(epic1);
        int epic1Id = epic1.getId();
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", epic1Id);
        final int subtaskId = taskManager.createSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
    }
}