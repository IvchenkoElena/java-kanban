import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Сергей,здравствуте. Есть странности с тестами, иногда они падают, иногда проходят. билась, билпсь, не нашла, в чем дело.
// По-отдельности каждый тест работает, вместе не всегда.
// Не знаю, значит ли это, что в коде ошибка, или что условия для тестов я не правильно напасала.
// Особенно часто проблемы случаются, если ID номера первые, вторые.
// Если заменить на номера реже встречаемые, то реже проблемы.
// Видимо надо как-то перед каждым тестом совсем очищать историю и все переменные, но я не знаю как.

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
    }


    @Test
    void remove() {
        Task task = new Task("Задача 3", "Выспаться3");
        Task task2 = new Task("Задача 4", "Покушать4");

        task.setId(5);
        historyManager.add(task);
        task2.setId(6);
        historyManager.add(task2);
        historyManager.remove(6);
        final List<Task> history3 = historyManager.getHistory();
        assertEquals(1, history3.size(), "В истории 1 элемент");
    }

    @Test
    void getHistory() {

        Task task3 = new Task("Задача 3", "Выспаться3");
        Task task4 = new Task("Задача 4", "Покушать4");

        task3.setId(8);
        historyManager.add(task3);
        task4.setId(9);
        historyManager.add(task4);
        final List<Task> history2 = historyManager.getHistory();
        assertEquals(2, history2.size(), "В истории 2 элемента");
    }
}

