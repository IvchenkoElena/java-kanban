import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
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
    void epicsWithSameIdAreEqual() {
        int id = 1;
        Epic epicOne = taskManager.getEpicById(id);
        Epic epicTwo = taskManager.getEpicById(id);
        assertEquals(epicOne, epicTwo);
    }

    @Test
    void getMySubtasksIdList() {
        List<Integer> mySubtasksIdList = taskManager.getEpicById(1).getMySubtasksIdList();
        assertNotNull(mySubtasksIdList, "Задачи не возвращаются.");
        assertEquals(2, mySubtasksIdList.size(), "Неверное количество задач.");
        assertEquals(2, mySubtasksIdList.get(0), "Задачи не совпадают.");
    }
}