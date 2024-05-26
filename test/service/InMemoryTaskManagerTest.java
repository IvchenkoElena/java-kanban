import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.IntersectionException;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void determineEpicStatusAllSubtasksNew() {

        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", epic1Id);
        subtask1.setStatus(Status.NEW);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description", epic1Id);
        subtask2.setStatus(Status.NEW);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    void determineEpicStatusNoSubtasks() {

        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic = taskManager.getEpicById(epic1Id);

        assertEquals(Status.NEW, savedEpic.getStatus());
    }

    @Test
    void determineEpicStatusAllSubtasksDone() {

        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic = taskManager.getEpicById(epic1Id);

        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", epic1Id);
        subtask1.setStatus(Status.DONE);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description", epic1Id);
        subtask2.setStatus(Status.DONE);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.DONE, savedEpic.getStatus());
    }

    @Test
    void determineEpicStatusSubtasksNewAndDone() {

        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic = taskManager.getEpicById(epic1Id);

        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", epic1Id);
        subtask1.setStatus(Status.NEW);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description", epic1Id);
        subtask2.setStatus(Status.DONE);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus());
    }

    @Test
    void determineEpicStatusSubtasksInProgress() {

        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic = taskManager.getEpicById(epic1Id);

        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", epic1Id);
        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description", epic1Id);
        subtask2.setStatus(Status.IN_PROGRESS);
        taskManager.createSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, savedEpic.getStatus());
    }

    @Test
    void determineEpicStartAndEndTimeNoSubtasks() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        taskManager.createEpic(epic1);

        assertNull(epic1.getStartTime());
        assertNull(epic1.getEndTime());
    }

    @Test
    void determineEpicStartAndEndTimeOneSubtaskNotSetted() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic = taskManager.getEpicById(epic1Id);

        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", epic1Id);
        taskManager.createSubtask(subtask1);

        assertEquals(subtask1.getStartTime(), savedEpic.getStartTime());
        assertNull(epic1.getStartTime());
        assertEquals(subtask1.getEndTime(), savedEpic.getEndTime());
        assertNull(epic1.getEndTime());
    }

    @Test
    void determineEpicStartAndEndTimeOneSubtaskNotSettedDuration() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic = taskManager.getEpicById(epic1Id);

        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", epic1Id);
        subtask1.setStartTime(LocalDateTime.of(2024, 5, 19, 15, 0));
        taskManager.createSubtask(subtask1);

        assertEquals(subtask1.getStartTime(), savedEpic.getStartTime());
        assertEquals(subtask1.getEndTime(), savedEpic.getEndTime());
        assertNull(savedEpic.getEndTime());
    }

    @Test
    void determineEpicStartAndEndTimeOneSubtaskSetted() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic = taskManager.getEpicById(epic1Id);

        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", epic1Id);
        subtask1.setStartTime(LocalDateTime.of(2024, 5, 19, 15, 0));
        subtask1.setDuration(Duration.ofMinutes(90));
        taskManager.createSubtask(subtask1);

        assertEquals(subtask1.getStartTime(), savedEpic.getStartTime());
        assertEquals(subtask1.getEndTime(), savedEpic.getEndTime());
    }

    @Test
    void determineEpicStartTimeMinSubtask() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic = taskManager.getEpicById(epic1Id);

        Subtask prev = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", epic1Id);
        prev.setStartTime(LocalDateTime.of(2024, 5, 19, 15, 0));
        taskManager.createSubtask(prev);

        Subtask next = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description", epic1Id);
        next.setStartTime(LocalDateTime.of(2024, 5, 19, 16, 0));
        taskManager.createSubtask(next);

        assertEquals(prev.getStartTime(), savedEpic.getStartTime());
    }

    @Test
    void determineEpicEndTimeMaxSubtask() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic = taskManager.getEpicById(epic1Id);

        Subtask prev = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", epic1Id);
        prev.setStartTime(LocalDateTime.of(2024, 5, 19, 15, 0));
        prev.setDuration(Duration.ofMinutes(10));
        taskManager.createSubtask(prev);

        Subtask next = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description", epic1Id);
        next.setStartTime(LocalDateTime.of(2024, 5, 19, 16, 0));
        next.setDuration(Duration.ofMinutes(10));
        taskManager.createSubtask(next);

        assertEquals(next.getEndTime(), savedEpic.getEndTime());
        assertEquals(prev.getStartTime(), savedEpic.getStartTime());
    }

    @Test
    void determineEpicDuration() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic = taskManager.getEpicById(epic1Id);

        assertNull(savedEpic.getDuration());

        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", epic1Id);
        subtask1.setStartTime(LocalDateTime.of(2024, 5, 19, 15, 0));
        subtask1.setDuration(Duration.ofMinutes(10));
        taskManager.createSubtask(subtask1);

        assertEquals(subtask1.getDuration(), savedEpic.getDuration());

        Subtask subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description", epic1Id);
        subtask2.setStartTime(LocalDateTime.of(2024, 5, 19, 16, 0));
        subtask2.setDuration(Duration.ofMinutes(20));
        taskManager.createSubtask(subtask2);

        assertEquals(subtask1.getDuration().plus(subtask2.getDuration()), savedEpic.getDuration());
    }

    @Test
    void updateTaskTimeAndDuration() {
        Task task1 = new Task("Задача 1", "Купить продукты");
        final int taskId = taskManager.createTask(task1);
        final Task savedTask = taskManager.getTaskById(taskId);
        assertNull(savedTask.getStartTime(), "Стартовое время не должно быть задано");
        assertNull(savedTask.getDuration(), "Продолжительность не должна быть задана");
        assertNull(savedTask.getEndTime(), "Время окончания не должно быть задано");

        Task newTask = new Task("Задача 1", "Купить разных продуктов");
        newTask.setId(taskId);
        taskManager.updateTask(newTask);
        final Task newSavedTask = taskManager.getTaskById(taskId);
        assertEquals(newTask.getDescription(), newSavedTask.getDescription(), "Описание не совпадает");
        assertEquals("Купить разных продуктов", newSavedTask.getDescription(), "Описание не совпадает");
        assertNull(savedTask.getDuration(), "Продолжительность не должна быть задана");
        assertNull(savedTask.getEndTime(), "Время окончания не должно быть задано");

        Task newTask1 = new Task("Задача 1", "Купить много продуктов");
        newTask1.setStartTime(LocalDateTime.of(2024, 5, 19, 15, 0));
        newTask1.setId(taskId);
        taskManager.updateTask(newTask1);
        final Task newSavedTask1 = taskManager.getTaskById(taskId);
        assertEquals(newTask1.getStartTime(), newSavedTask1.getStartTime(), "Стартовое время не совпадает");
        assertEquals(LocalDateTime.of(2024, 5, 19, 15, 0), newSavedTask1.getStartTime(), "Стартовое время не совпадает");
        assertNull(savedTask.getDuration(), "Продолжительность не должна быть задана");
        assertNull(savedTask.getEndTime(), "Время окончания не должно быть задано");

        Task newTask2 = new Task("Задача 1", "Купить очень много продуктов");
        newTask2.setStartTime(LocalDateTime.of(2024, 5, 19, 16, 0));
        newTask2.setDuration(Duration.ofMinutes(90));
        newTask2.setId(taskId);
        taskManager.updateTask(newTask2);
        final Task newSavedTask2 = taskManager.getTaskById(taskId);
        assertEquals(newTask2.getStartTime(), newSavedTask2.getStartTime(), "Стартовое время не совпадает");
        assertEquals(LocalDateTime.of(2024, 5, 19, 16, 0), newSavedTask2.getStartTime(), "Стартовое время не совпадает");
        assertEquals(newTask2.getDuration(), newSavedTask2.getDuration(), "Продолжительность не совпадает");
        assertEquals(Duration.ofMinutes(90), newSavedTask2.getDuration(), "Продолжительность не совпадает");
        assertEquals(newTask2.getEndTime(), newSavedTask2.getEndTime(), "Время окончания не совпадает");
    }

    @Test
    void timeCrossAndPrioritizedTasksList() {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description");
        task1.setStartTime(LocalDateTime.of(2024, 5, 20, 8, 0));
        task1.setDuration(Duration.ofMinutes(50));
        final int task1Id = taskManager.createTask(task1);

        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description");
        task2.setStartTime(LocalDateTime.of(2024, 5, 20, 9, 0));
        task2.setDuration(Duration.ofMinutes(60));
        final int task2Id = taskManager.createTask(task2);

        Task task3 = new Task("Test addNewTask3", "Test addNewTask3 description");
        task3.setStartTime(LocalDateTime.of(2024, 5, 20, 9, 30));
        task3.setDuration(Duration.ofMinutes(10));
        try {
            taskManager.createTask(task3);
        } catch (IntersectionException exception) {
            System.out.println("Поймано исключение пересечения: " + exception.getMessage());
        }

        Task task4 = new Task("Test addNewTask4", "Test addNewTask4 description");
        task4.setStartTime(LocalDateTime.of(2024, 5, 21, 8, 30));
        final int task4Id = taskManager.createTask(task4);

        assertNotNull(taskManager.getTaskById(task1Id), "Первая задача должна записываться в HashMap");
        assertTrue(taskManager.getPrioritizedTasks().contains(task1), "Задача1 должна быть в сортированном списке");
        assertNotNull(taskManager.getTaskById(task2Id), "Непересекающаяся задача должна записываться в HashMap");
        assertTrue(taskManager.getPrioritizedTasks().contains(task2), "Задача2  должна быть в сортированном списке");
        assertFalse(taskManager.getAllTasksList().contains(task3), "Пересекающаяся задача не должна записываться в HashMap");
        assertFalse(taskManager.getPrioritizedTasks().contains(task3), "Пересекающаяся задаче не должна записываться в сортированный список");
        assertTrue(taskManager.getAllTasksList().contains(taskManager.getTaskById(task4Id)), "Задача4 без продолжительности должна записываться в HashMap");
        assertFalse(taskManager.getPrioritizedTasks().contains(task4), "Задача4 без продолжительности не должна записываться в сортированный список");

        Task updatedTask1ver1 = new Task("Test updatedTask1ver1", "Test updatedTask1ver1 description");
        updatedTask1ver1.setStartTime(LocalDateTime.of(2024, 5, 20, 8, 10));
        updatedTask1ver1.setDuration(Duration.ofMinutes(60));
        updatedTask1ver1.setId(task1Id);
        try {
            taskManager.updateTask(updatedTask1ver1);
        } catch (IntersectionException exception) {
            System.out.println("Поймано исключение пересечения: " + exception.getMessage());
        }

        assertNotEquals(updatedTask1ver1.getStartTime(), taskManager.getTaskById(task1Id).getStartTime(), "Пересекающаяся задача не должна записываться в HashMap");
        assertEquals(task1.getStartTime(), taskManager.getTaskById(task1Id).getStartTime(), "В HashMap должна остаться старая версия задачи с ID1");
        assertNotEquals(updatedTask1ver1.getStartTime(), taskManager.getPrioritizedTasks().getFirst().getStartTime(), "Пересекающаяся задача не должна записываться в сортированный список");
        assertEquals(task1.getStartTime(), taskManager.getPrioritizedTasks().getFirst().getStartTime(), "В сортированном списке должна остаться старая версия задачи с ID1");

        Task updatedTask1ver2 = new Task("Test updatedTask1ver2", "Test updatedTask1ver2 description");
        updatedTask1ver2.setId(task1Id);
        taskManager.updateTask(updatedTask1ver2);

        assertNull(taskManager.getTaskById(task1Id).getStartTime(), "У задачи с ID1 не должно быть задано стартовое время");
        assertFalse(taskManager.getPrioritizedTasks().contains(task1), "Сортированный список больше не должен содержать задачу с ID1");
    }
}