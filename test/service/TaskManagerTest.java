import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {
    T taskManager;

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
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasksList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void deleteAllTasks() {
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
        Task task = new Task("Test Task", "Test Task description");
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

        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        taskManager.createEpic(epic2);
        int epic2Id = epic2.getId();
        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи3", epic2Id);
        taskManager.createSubtask(subtask3);
        Subtask subtask4 = new Subtask("Подзадача4", "Описание подзадачи4", epic2Id);
        taskManager.createSubtask(subtask4);

        final List<Epic> epicsList = taskManager.getAllEpicsList();

        assertNotNull(epicsList, "Задачи не возвращаются.");
        assertEquals(2, epicsList.size(), "Неверное количество задач.");
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
        int epic2Id = epic2.getId();
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
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic = taskManager.getEpicById(epic1Id);
        assertNotNull(taskManager.getEpicById(epic1Id), "Задача должна быть.");
        assertEquals(epic1, savedEpic, "Задачи не совпадают.");

        taskManager.deleteEpicById(epic1Id);

        assertNull(taskManager.getEpicById(epic1Id), "Задачи недолжно быть");
    }

    @Test
    void createSubtask() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", epic1Id);
        final int subtaskId = taskManager.createSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");
    }

    @Test
    void forSubtaskEpicExist(){
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description", epic1Id);
        Epic savedEpic = taskManager.getEpicById(epic1Id);

        assertNotNull(savedEpic);
        assertEquals(savedEpic, taskManager.getEpicById(subtask.getMyEpicId()));
    }

    @Test
    void noNotActualSubtasksInEpicSubtasksList() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", epic1Id);
        final int subtask1Id = taskManager.createSubtask(subtask1);
        final Subtask savedSubtask1 = taskManager.getSubtaskById(subtask1Id);

        Subtask subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description", epic1Id);
        final int subtask2Id = taskManager.createSubtask(subtask2);
        final Subtask savedSubtask2 = taskManager.getSubtaskById(subtask2Id);

        final int mySubtaskList1Size = epic1.getMySubtasksIdList().size();
        assertEquals(2, mySubtaskList1Size, "Количество подзадач должно быть 2");

        taskManager.deleteSubtaskById(subtask1Id);

        final int mySubtaskList2Size = epic1.getMySubtasksIdList().size();
        assertEquals(mySubtaskList1Size - 1, mySubtaskList2Size, "Количество подзадач должно уменьшиться на 1.");

        assertFalse(epic1.getMySubtasksIdList().contains(savedSubtask1.getId()), "Подзадачи1 не должно быть.");
        assertEquals(1, epic1.getMySubtasksIdList().size(), "В списке должна быть одна подзадача.");
        assertEquals(savedSubtask2.getId(), epic1.getMySubtasksIdList().getFirst(), "Задачи не совпадают.");
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
        subtask1.setStartTime(LocalDateTime.of(2024,5,19,15,0));
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
        subtask1.setStartTime(LocalDateTime.of(2024,5,19,15,0));
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
        prev.setStartTime(LocalDateTime.of(2024,5,19,15,0));
        taskManager.createSubtask(prev);

        Subtask next = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description", epic1Id);
        next.setStartTime(LocalDateTime.of(2024,5,19,16,0));
        taskManager.createSubtask(next);

        assertEquals(prev.getStartTime(), savedEpic.getStartTime());
    }

    @Test
    void determineEpicEndTimeMaxSubtask() {
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        int epic1Id = taskManager.createEpic(epic1);
        Epic savedEpic = taskManager.getEpicById(epic1Id);

        Subtask prev = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", epic1Id);
        prev.setStartTime(LocalDateTime.of(2024,5,19,15,0));
        prev.setDuration(Duration.ofMinutes(10));
        taskManager.createSubtask(prev);

        Subtask next = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description", epic1Id);
        next.setStartTime(LocalDateTime.of(2024,5,19,16,0));
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
        subtask1.setStartTime(LocalDateTime.of(2024,5,19,15,0));
        subtask1.setDuration(Duration.ofMinutes(10));
        taskManager.createSubtask(subtask1);

        assertEquals(subtask1.getDuration(), savedEpic.getDuration());

        Subtask subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description", epic1Id);
        subtask2.setStartTime(LocalDateTime.of(2024,5,19,16,0));
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

        Task newTask1 = new Task("Задача 1", "Купить много продуктов");
        newTask1.setStartTime(LocalDateTime.of(2024,5,19,15,0));
        newTask1.setId(taskId);
        taskManager.updateTask(newTask1);
        final Task newSavedTask = taskManager.getTaskById(taskId);
        assertEquals(newTask1.getStartTime(), newSavedTask.getStartTime(), "Стартовое время не совпадает");
        assertEquals(LocalDateTime.of(2024,5,19,15,0), newSavedTask.getStartTime(), "Стартовое время не совпадает");
        assertNull(savedTask.getDuration(), "Продолжительность не должна быть задана");
        assertNull(savedTask.getEndTime(), "Время окончания не должно быть задано");

        Task newTask2 = new Task("Задача 1", "Купить очень много продуктов");
        newTask2.setStartTime(LocalDateTime.of(2024,5,19,16,0));
        newTask2.setDuration(Duration.ofMinutes(90));
        newTask2.setId(taskId);
        taskManager.updateTask(newTask2);
        final Task newSavedTask2 = taskManager.getTaskById(taskId);
        assertEquals(newTask2.getStartTime(), newSavedTask2.getStartTime(), "Стартовое время не совпадает");
        assertEquals(LocalDateTime.of(2024,5,19,16,0), newSavedTask2.getStartTime(), "Стартовое время не совпадает");
        assertEquals(newTask2.getDuration(), newSavedTask2.getDuration(), "Продолжительность не совпадает");
        assertEquals(Duration.ofMinutes(90), newSavedTask2.getDuration(), "Продолжительность не совпадает");
        assertEquals(newTask2.getEndTime(), newSavedTask2.getEndTime(), "Время окончания не совпадает");
    }

    @Test
    void timeCrossAndPrioritizedTasksList(){
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description");
        task1.setStartTime(LocalDateTime.of(2024,5,20,8,0));
        task1.setDuration(Duration.ofMinutes(60));
        final int task1Id = taskManager.createTask(task1);

        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description");
        task2.setStartTime(LocalDateTime.of(2024,5,20,9,0));
        task2.setDuration(Duration.ofMinutes(60));
        final int task2Id = taskManager.createTask(task2);

        Task task3 = new Task("Test addNewTask3", "Test addNewTask3 description");
        task3.setStartTime(LocalDateTime.of(2024,5,20,9,30));
        task3.setDuration(Duration.ofMinutes(10));
        final int task3Id = taskManager.createTask(task3);

        Task task4 = new Task("Test addNewTask4", "Test addNewTask4 description");
        task4.setStartTime(LocalDateTime.of(2024,5,21,8,30));
        final int task4Id = taskManager.createTask(task4);

        assertNotNull(taskManager.getTaskById(task1Id), "Первая задача должна записываться в HashMap");
        assertTrue(taskManager.getPrioritizedTasks().contains(task1), "Задача1 должна быть в сортированном списке");
        assertNotNull(taskManager.getTaskById(task2Id), "Непересекающаяся задача должна записываться в HashMap");
        assertTrue(taskManager.getPrioritizedTasks().contains(task2), "Задача2  должна быть в сортированном списке");
        assertEquals(-1, task3Id, "Пересекающейся задаче должен присваиваться id -1");
        assertNull(taskManager.getTaskById(task3Id), "Пересекающаяся задача не должна записываться в HashMap");
        assertFalse(taskManager.getPrioritizedTasks().contains(task3), "Пересекающаяся задаче не должна записываться в сортированный список");
        assertTrue(taskManager.getAllTasksList().contains(taskManager.getTaskById(task4Id)), "Задача4 без продолжительности должна записываться в HashMap");
        assertFalse(taskManager.getPrioritizedTasks().contains(task4), "Задача4 без продолжительности не должна записываться в сортированный список");

        Task updatedTask1ver1 = new Task("Test updatedTask1ver1", "Test updatedTask1ver1 description");
        updatedTask1ver1.setStartTime(LocalDateTime.of(2024,5,20,8,10));
        updatedTask1ver1.setDuration(Duration.ofMinutes(60));
        updatedTask1ver1.setId(task1Id);
        taskManager.updateTask(updatedTask1ver1);

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
