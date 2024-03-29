import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;
import service.HistoryManager;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        final TaskManager taskManager = Managers.getDefault();

        Task taskOne = new Task("Задача 1", "Купить продукты");
        taskOne.setId(7);
        Task taskTwo = new Task("Задача 2", "Вынести мусор");
        taskTwo.setId(7);
        System.out.println(taskOne);
        System.out.println(taskTwo);

        Task task1 = new Task("Задача 1", "Купить продукты");
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Вынести мусор");
        taskManager.createTask(task2);

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

        taskManager.getTaskById(1);

        printAllTasks(taskManager);

        taskManager.getTaskById(2);
        taskManager.getTaskById(9);

        printAllTasks(taskManager);

        Task newTask1 = new Task("Задача 1", "Купить много продуктов");
        newTask1.setStatus(Status.DONE);
        newTask1.setId(1);
        taskManager.updateTask(newTask1);
        Task newTask2 = new Task("Задача 2", "Вынести мусор");
        newTask2.setStatus(Status.IN_PROGRESS);
        newTask2.setId(2);
        taskManager.updateTask(newTask2);

        Subtask newWall = new Subtask("Стены", "Красим", renovationId);
        newWall.setStatus(Status.IN_PROGRESS);
        newWall.setId(4);
        taskManager.updateSubtask(newWall);
        Subtask newFurniture = new Subtask("Мебель", "Заказать", renovationId);
        newFurniture.setStatus(Status.DONE);
        newFurniture.setId(5);
        taskManager.updateSubtask(newFurniture);

        Subtask newTickets = new Subtask("Билеты", "Найти выгодные даты", vacationId);
        newTickets.setStatus(Status.DONE);
        newTickets.setId(7);
        taskManager.updateSubtask(newTickets);

        taskManager.getEpicById(3);

        printAllTasks(taskManager);

        taskManager.deleteTaskById(2);
        taskManager.deleteEpicById(6);

        taskManager.getSubtaskById(5);

        printAllTasks(taskManager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasksList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpicsList()) {
            System.out.println(epic);

            for (Task task : manager.getMySubtasksListByEpicId(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasksList()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistoryManager().getHistory()) {
            System.out.println(task);
        }
    }
}