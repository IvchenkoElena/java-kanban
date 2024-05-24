import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager historyManager;
    Task task;
    Task task2;
    Task task3;

    @BeforeEach
    public void beforeEach() {
        historyManager = Managers.getDefaultHistory();

        task = new Task("Задача 1", "Описание задачи 1");
        task2 = new Task("Задача 2", "Описание задачи 2");
        task3 = new Task("Задача 3", "Описание задачи 3");
    }

    @Test
    void emptyHistory() {
        List<Task> emptyHistory = historyManager.getHistory();
        assertEquals(0, emptyHistory.size(), "История должна быть пуста");
    }

    @Test
    void add() {
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
        task.setId(1);
        historyManager.add(task);
        task2.setId(1);
        historyManager.add(task2);

        final List<Task> history1 = historyManager.getHistory();
        assertEquals(1, history1.size(), "В истории 1 элемент.");
        assertEquals(task2.getName(), history1.getFirst().getName(), "Должна сохраниться задача 2");
        assertNotEquals(task.getName(), history1.getFirst().getName(), "В истории не должно быть задачи 1");
    }

    @Test
    void removeFirst() {
        task.setId(1);
        historyManager.add(task);
        task2.setId(2);
        historyManager.add(task2);
        task3.setId(3);
        historyManager.add(task3);

        historyManager.remove(1);
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "В истории 2 элемента");
        assertFalse(history.contains(task), "В истории не должно быть элемента с ID 1");
        assertTrue(history.contains(task2) && history.contains(task3), "В истории содержатся элементы с ID 2 и 3");
    }

    @Test
    void removeLast() {
        task.setId(1);
        historyManager.add(task);
        task2.setId(2);
        historyManager.add(task2);
        task3.setId(3);
        historyManager.add(task3);

        historyManager.remove(3);
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "В истории 2 элемента");
        assertFalse(history.contains(task3), "В истории не должно быть элемента с ID 3");
        assertTrue(history.contains(task) && history.contains(task2), "В истории содержатся элементы с ID 1 и 2");
    }

    @Test
    void removeMiddle() {
        task.setId(1);
        historyManager.add(task);
        task2.setId(2);
        historyManager.add(task2);
        task3.setId(3);
        historyManager.add(task3);

        historyManager.remove(2);

        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "В истории 2 элемента");
        assertFalse(history.contains(task2), "В истории не должно быть элемента с ID 2");
        assertTrue(history.contains(task) && history.contains(task3), "В истории содержатся элементы с ID 1 и 3");

    }

    @Test
    void getHistory() {
        task.setId(1);
        historyManager.add(task);
        task2.setId(2);
        historyManager.add(task2);
        final List<Task> history2 = historyManager.getHistory();
        assertEquals(2, history2.size(), "В истории 2 элемента");
    }
}

