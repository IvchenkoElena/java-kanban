import model.Task;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    Task task = new Task("Задача 1", "Выспаться");
    Task task2 = new Task("Задача 2", "Покушать");

    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    @Test
    void getHistory() {
        historyManager.add(task);
        historyManager.add(task2);
        final List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "В истории 2 элемента");
    }
}