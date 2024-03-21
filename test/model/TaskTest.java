import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    static final TaskManager taskManager = Managers.getDefault();

    @Test
    void tasksWithSameIdAreEqual() {
        Task task1 = new Task("Задача 1", "Купить продукты");
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Вынести мусор");
        taskManager.createTask(task2);
        int id = 2;
        Task taskOne = taskManager.getTaskById(id);
        Task taskTwo = taskManager.getTaskById(id);
        assertEquals(taskOne, taskTwo);
    }
}