//Сергей, добрый день, внесла правки.


import model.Task;
import service.TaskManager;
import model.Epic;
import model.Subtask;
import model.TaskStatus;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

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


        System.out.println(taskManager.getAllTasksList().toString());
        System.out.println(taskManager.getAllEpicsList().toString());
        System.out.println(taskManager.getAllSubtasksList().toString());


        Task newTask1 = new Task("Задача 1", "Купить продукты");
        newTask1.setStatus(TaskStatus.DONE);
        newTask1.setId(1);
        taskManager.updateTask(newTask1);
        Task newTask2 = new Task("Задача 2", "Вынести мусор");
        newTask2.setStatus(TaskStatus.IN_PROGRESS);
        newTask2.setId(2);
        taskManager.updateTask(newTask2);

        Subtask newWall = new Subtask("Стены", "Красим", renovationId);
        newWall.setStatus(TaskStatus.IN_PROGRESS);
        newWall.setId(4);
        taskManager.updateSubtask(newWall);
        Subtask newFurniture = new Subtask("Мебель", "Заказать", renovationId);
        newFurniture.setStatus(TaskStatus.DONE);
        newFurniture.setId(5);
        taskManager.updateSubtask(newFurniture);


        Subtask newTickets = new Subtask("Билеты", "Найти выгодные даты", vacationId);
        newTickets.setStatus(TaskStatus.DONE);
        newTickets.setId(7);
        taskManager.updateSubtask(newTickets);

        System.out.println(taskManager.getAllTasksList().toString());
        System.out.println(taskManager.getAllEpicsList().toString());
        System.out.println(taskManager.getAllSubtasksList().toString());


        taskManager.deleteTaskById(2);
        taskManager.deleteEpicById(6);

        System.out.println(taskManager.getAllTasksList().toString());
        System.out.println(taskManager.getAllEpicsList().toString());
        System.out.println(taskManager.getAllSubtasksList().toString());
    }
}