import model.Task;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void tasksWithSameIdAreEqual() {
        Task taskOne = new Task("Задача 1", "Купить продукты");
        taskOne.setId(7);
        Task taskTwo = new Task("Задача 2", "Вынести мусор");
        taskTwo.setId(7);

        assertEquals(taskOne, taskTwo);
    }
}