import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        Task task = new Task("Задача 1", "Выспаться");
        task.setId(1);
        historyManager.add(task);

        final List<Task> history1 = historyManager.getHistory();
        assertNotNull(history1, "История не пустая.");
        assertEquals(1, history1.size(), "В истории 1 элемент.");
        assertEquals(task, history1.getFirst(), "Задачи не совпадают");
        assertEquals(task.getName(), history1.getFirst().getName(), "Имена задач не совпадают");
    }

    @Test
    void addTaskWithSameId() {
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        task1.setId(1);
        historyManager.add(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        task2.setId(1);
        historyManager.add(task2);

        final List<Task> history1 = historyManager.getHistory();
        assertEquals(1, history1.size(), "В истории 1 элемент.");
        assertEquals(task2.getName(), history1.getFirst().getName(), "Должна сохраниться задача 2");
    }


    @Test
    void removeFirst() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        Task task3 = new Task("Задача 3", "Описание задачи 3");
        Task task4 = new Task("Задача 4", "Описание задачи 4");
        Task task5 = new Task("Задача 5", "Описание задачи 5");

        task.setId(1);
        historyManager.add(task);
        task2.setId(2);
        historyManager.add(task2);
        task3.setId(3);
        historyManager.add(task3);
        task4.setId(4);
        historyManager.add(task4);
        task5.setId(5);
        historyManager.add(task5);
        historyManager.remove(1);
        final List<Task> history = historyManager.getHistory();
        assertEquals(4, history.size(), "В истории 4 элемента");
        assertFalse(history.contains(task), "В истории не должно быть элемента с ID 1");
    }

    @Test
    void removeLast() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        Task task3 = new Task("Задача 3", "Описание задачи 3");
        Task task4 = new Task("Задача 4", "Описание задачи 4");
        Task task5 = new Task("Задача 5", "Описание задачи 5");

        task.setId(1);
        historyManager.add(task);
        task2.setId(2);
        historyManager.add(task2);
        task3.setId(3);
        historyManager.add(task3);
        task4.setId(4);
        historyManager.add(task4);
        task5.setId(5);
        historyManager.add(task5);
        historyManager.remove(5);
        final List<Task> history = historyManager.getHistory();
        assertEquals(4, history.size(), "В истории 4 элемента");
        assertFalse(history.contains(task5), "В истории не должно быть элемента с ID 5");
    }

    @Test
    void removeMiddle() {
        Task task = new Task("Задача 1", "Описание задачи 1");
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        Task task3 = new Task("Задача 3", "Описание задачи 3");
        Task task4 = new Task("Задача 4", "Описание задачи 4");
        Task task5 = new Task("Задача 5", "Описание задачи 5");

        task.setId(1);
        historyManager.add(task);
        task2.setId(2);
        historyManager.add(task2);
        task3.setId(3);
        historyManager.add(task3);
        task4.setId(4);
        historyManager.add(task4);
        task5.setId(5);
        historyManager.add(task5);
        historyManager.remove(3);
        historyManager.remove(4);
        final List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "В истории 3 элемента");
        assertFalse(history.contains(task3), "В истории не должно быть элемента с ID 3");
        assertFalse(history.contains(task4), "В истории не должно быть элемента с ID 4");
    }

    @Test
    void getHistory() {

        Task task3 = new Task("Задача 1", "Выспаться");
        Task task4 = new Task("Задача 2", "Покушать");

        task3.setId(1);
        historyManager.add(task3);
        task4.setId(2);
        historyManager.add(task4);
        final List<Task> history2 = historyManager.getHistory();
        assertEquals(2, history2.size(), "В истории 2 элемента");
    }
}

