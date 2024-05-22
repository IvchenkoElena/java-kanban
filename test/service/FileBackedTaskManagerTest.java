import model.Epic;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;
import service.ManagerSaveException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File saveFile;

    @BeforeEach
    public void setUp() throws IOException {
        saveFile = File.createTempFile("java-kanban-save-test", ".csv");
        taskManager = new FileBackedTaskManager(saveFile);
    }

    @Test
    void loadAndSaveTest() {

        // Проверка загрузки пустого файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(saveFile);
        assertEquals(taskManager.getAllTasksList().size(), loadedManager.getAllTasksList().size());
        assertEquals(taskManager.getAllSubtasksList().size(), loadedManager.getAllSubtasksList().size());
        assertEquals(taskManager.getAllEpicsList().size(), loadedManager.getAllEpicsList().size());

        assertEquals(0, loadedManager.getAllTasksList().size());
        assertEquals(0, loadedManager.getAllSubtasksList().size());
        assertEquals(0, loadedManager.getAllEpicsList().size());

        // Создание нескольких задач для сохранения и загрузки
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        Task task3 = new Task("Задача 3", "Описание задачи 3");
        Epic epic = new Epic("Эпик", "Описание эпика");

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createEpic(epic);

        assertEquals(3, taskManager.getAllTasksList().size());
        assertEquals(0, taskManager.getAllSubtasksList().size());
        assertEquals(1, taskManager.getAllEpicsList().size());

        assertEquals(0, loadedManager.getAllTasksList().size());
        assertEquals(0, loadedManager.getAllSubtasksList().size());
        assertEquals(0, loadedManager.getAllEpicsList().size());

        // Проверка сохранения и загрузки нескольких задач
        loadedManager = FileBackedTaskManager.loadFromFile(saveFile);

        assertEquals(taskManager.getAllTasksList().size(), loadedManager.getAllTasksList().size());
        assertEquals(taskManager.getAllSubtasksList().size(), loadedManager.getAllSubtasksList().size());
        assertEquals(taskManager.getAllEpicsList().size(), loadedManager.getAllEpicsList().size());

        assertEquals(3, loadedManager.getAllTasksList().size());
        assertEquals(0, loadedManager.getAllSubtasksList().size());
        assertEquals(1, loadedManager.getAllEpicsList().size());

        // Проверка соответствия загруженных задач созданным
        List<Task> originalTasks = new ArrayList<>(taskManager.getAllTasksList());
        List<Task> loadedTasks = new ArrayList<>(loadedManager.getAllTasksList());
        for (int i = 0; i < originalTasks.size(); i++) {
            Task originalTask = originalTasks.get(i);
            Task loadedTask = loadedTasks.get(i);
            assertEquals(originalTask.getId(), loadedTask.getId());
            assertEquals(originalTask.getName(), loadedTask.getName());
            assertEquals(originalTask.getStatus(), loadedTask.getStatus());
        }

        //обновление задач
        Task taskNew1 = new Task("Задача new1", "Описание задачи new1");
        taskNew1.setId(1);
        taskNew1.setStatus(Status.IN_PROGRESS);
        Task taskNew2 = new Task("Задача new2", "Описание задачи new2");
        taskNew2.setId(2);
        taskNew2.setStatus(Status.DONE);
        Task taskNew3 = new Task("Задача new3", "Описание задачи new");
        taskNew3.setId(3);
        taskNew3.setStatus(Status.DONE);
        Epic epicNew = new Epic("Эпик new", "Описание эпика new");
        epicNew.setId(4);

        taskManager.updateTask(taskNew1);
        taskManager.updateTask(taskNew2);
        taskManager.updateTask(taskNew3);
        taskManager.updateEpic(epicNew);

        loadedManager = FileBackedTaskManager.loadFromFile(saveFile);

        // Проверка соответствия обновленных задач созданным
        List<Task> originalTasks2 = new ArrayList<>(taskManager.getAllTasksList());
        List<Task> loadedTasks2 = new ArrayList<>(loadedManager.getAllTasksList());
        for (int i = 0; i < originalTasks2.size(); i++) {
            Task originalTask = originalTasks2.get(i);
            Task loadedTask = loadedTasks2.get(i);
            assertEquals(originalTask.getId(), loadedTask.getId());
            assertEquals(originalTask.getName(), loadedTask.getName());
            assertEquals(originalTask.getStatus(), loadedTask.getStatus());
        }
    }

    @Test
    public void shouldThrowManagerSaveExceptionSaveTest() {
        // Создаем менеджер задач и передаем ему файл по несуществующему пути
        File wrongFile = new File("notExistedPath/wrong.csv");
        taskManager = new FileBackedTaskManager(wrongFile);
        Task task = new Task("Задача 1", "Описание задачи 1");
        assertThrows(ManagerSaveException.class, () -> taskManager.createTask(task));
    }

//    @Test
//    public void shouldNotThrowManagerSaveException() { //эти методы DoesNotThrow так и не запускаются
//        Task task = new Task("Задача 1", "Описание задачи 1");
//        Assertions.assertDoesNotThrow(ManagerSaveException.class, () -> {
//            taskManager.createTask(task);
//        });
//    }

    @Test
    public void shouldThrowManagerSaveExceptionLoadTest() { //Чтобы этот тест работал,
        // удалила проверку на существование файла в методе loadFromFile,
        // не знаю, как правильнее? наверное исключение более информативно

        // Создаем файл с несуществующим путем
        File wrongFile = new File("notExistedPath/wrong.csv");
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(wrongFile));
        // передаем несуществующий файл
        File notExistedFile = Path.of("notExistedFile", "csv").toFile();
        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(notExistedFile));
    }

//    @Test
//    public void shouldNotThrowManagerSaveExceptionLoadTest() { //эти методы DoesNotThrow так и не запускаются
//        Assertions.assertDoesNotThrow(ManagerSaveException.class, () -> {
//            FileBackedTaskManager.loadFromFile(saveFile);
//        });
//    }

}