import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {
    static final TaskManager taskManager = Managers.getDefault();

    @BeforeAll
    static void beforeAll() {
        Epic renovation = new Epic("Ремонт","Сделать ремонт");
        taskManager.createEpic(renovation);
        int renovationId = renovation.getId();
        Subtask wall = new Subtask("Стены", "Шпаклюем и штукатурим", renovationId);
        taskManager.createSubtask(wall);
        Subtask furniture = new Subtask("Мебель", "Купить и собрать", renovationId);
        taskManager.createSubtask(furniture);

        Epic vacation = new Epic("Отпуск","Запланировать путешествие");
        taskManager.createEpic(vacation);
        int vacationId = vacation.getId();
        Subtask tickets = new Subtask("Билеты", "Найти выгодные даты", vacationId);
        taskManager.createSubtask(tickets);
    }

    @Test
    void subtasksWithSameIdAreEqual() {
        int id = 2;
        Subtask subtaskOne = taskManager.getSubtaskById(id);
        Subtask subtaskTwo = taskManager.getSubtaskById(id);
        assertEquals(subtaskOne, subtaskTwo);
    }

    @Test
    void getMyEpicId() {
        Subtask subtask = taskManager.getSubtaskById(2);
        assertEquals(1, subtask.getMyEpicId(),"ID зпиков не совпадают." );
    }
}