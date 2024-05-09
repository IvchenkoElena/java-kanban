import model.Epic;
import model.Subtask;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    //static final File file = Path.of("file.csv").toFile();
    static final File file;

    static {
        try {
            file = File.createTempFile("Test", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static final TaskManager taskManager = Managers.getDefault(file);
    //static final TaskManager taskManager = Managers.load(file);

    @BeforeAll
    static void beforeAll() {
        Epic renovation = new Epic("Ремонт", "Сделать ремонт");
        taskManager.createEpic(renovation);
        int renovationId = renovation.getId();
        Subtask wall = new Subtask("Стены", "Шпаклюем и штукатурим", renovationId);
        taskManager.createSubtask(wall);
        Subtask furniture = new Subtask("Мебель", "Купить и собрать", renovationId);
        taskManager.createSubtask(furniture);

        Epic vacation = new Epic("Отпуск", "Запланировать путешествие");
        taskManager.createEpic(vacation);
        int vacationId = vacation.getId();
        Subtask tickets = new Subtask("Билеты", "Найти выгодные даты", vacationId);
        taskManager.createSubtask(tickets);
    }

    @Test
    void subtasksWithSameIdAreEqual() {

        int epicId = 15;

        Subtask subtaskOne = new Subtask("Тестовая подзадача 1", "Описание тестовой подзадачи 1", epicId);
        subtaskOne.setId(8);
        Subtask subtaskTwo = new Subtask("Тестовая подзадача 2", "Описание тестовой подзадачи 2", epicId);
        subtaskTwo.setId(8);

        assertEquals(subtaskOne, subtaskTwo);
    }

    @Test
    void getMyEpicId() {
        Subtask subtask = taskManager.getSubtaskById(2);
        assertEquals(1, subtask.getMyEpicId(), "ID зпиков не совпадают.");
    }
}