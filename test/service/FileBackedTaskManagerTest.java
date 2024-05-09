import model.Status;
import model.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


class FileBackedTaskManagerTest {
    static final File file;

    static {
        try {
            file = File.createTempFile("Test", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void loadAndSaveTest() {
        //FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        // Проверка сохранения и загрузки пустого файла
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Assertions.assertEquals(manager.getAllTasksList().size(), loadedManager.getAllTasksList().size());
        Assertions.assertEquals(manager.getAllSubtasksList().size(), loadedManager.getAllSubtasksList().size());
        Assertions.assertEquals(manager.getAllEpicsList().size(), loadedManager.getAllEpicsList().size());

        // Создание нескольких задач для сохранения и загрузки
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        task1.setId(1);
        task1.setStatus(Status.IN_PROGRESS);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        task1.setId(2);
        task1.setStatus(Status.DONE);
        Task task3 = new Task("Задача 3", "Описание задачи 3");
        task1.setId(3);
        task1.setStatus(Status.NEW);

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        // Проверка сохранения и загрузки нескольких задач
        manager.save();
        loadedManager = FileBackedTaskManager.loadFromFile(file);
        Assertions.assertEquals(manager.getAllTasksList().size(), loadedManager.getAllTasksList().size());
        Assertions.assertEquals(manager.getAllSubtasksList().size(), loadedManager.getAllSubtasksList().size());
        Assertions.assertEquals(manager.getAllEpicsList().size(), loadedManager.getAllEpicsList().size());

        // Проверка соответствия загруженных задач созданным
        List<Task> originalTasks = new ArrayList<>(manager.getAllTasksList());
        List<Task> loadedTasks = new ArrayList<>(loadedManager.getAllTasksList());
        for (int i = 0; i < originalTasks.size(); i++) {
            Task originalTask = originalTasks.get(i);
            Task loadedTask = loadedTasks.get(i);
            Assertions.assertEquals(originalTask.getId(), loadedTask.getId());
            Assertions.assertEquals(originalTask.getName(), loadedTask.getName());
            Assertions.assertEquals(originalTask.getStatus(), loadedTask.getStatus());
        }
    }
}