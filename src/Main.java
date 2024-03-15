import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");

        TaskManager taskManager = new TaskManager();

        taskManager.createTask("Задача 1", "Купить продукты", TaskStatus.NEW);
        taskManager.createTask("Задача 2", "Вынести мусор", TaskStatus.NEW);

        Subtask wall = taskManager.createSubtask("Стены", "Шпаклюем и штукатурим", TaskStatus.NEW);
        Subtask furniture = taskManager.createSubtask("Мебель", "Купить и собрать", TaskStatus.NEW);
        ArrayList<Subtask> list1 = new ArrayList<>();
        list1.add(wall);
        list1.add(furniture);
        Epic renovation = taskManager.createEpic("Ремонт","Сделать ремонт", list1);
        wall.setMyEpic(renovation);
        furniture.setMyEpic(renovation);

        Subtask tickets = taskManager.createSubtask("Билеты", "Найти выгодные даты", TaskStatus.NEW);
        ArrayList<Subtask> list2 = new ArrayList<>();
        list2.add(tickets);
        Epic vacation = taskManager.createEpic("Отпуск","Запланировать путешествие", list2);
        tickets.setMyEpic(vacation);

        System.out.println(taskManager.tasks.toString());
        System.out.println(taskManager.subtasks.toString());
        System.out.println(taskManager.epics.toString());


        taskManager.updateTask("Задача 1", "Купить продукты", 1, TaskStatus.DONE);
        taskManager.updateTask("Задача 2", "Вынести мусор", 2,  TaskStatus.IN_PROGRESS);

        wall = taskManager.updateSubtask("Стены", "Шпаклюем и штукатурим", 3, TaskStatus.DONE);
        furniture = taskManager.updateSubtask("Мебель", "Купить и собрать", 4, TaskStatus.IN_PROGRESS);
        list1 = new ArrayList<>();
        list1.add(wall);
        list1.add(furniture);
        renovation = taskManager.updateEpic("Ремонт","Сделать ремонт", 5, list1);
        wall.setMyEpic(renovation);
        furniture.setMyEpic(renovation);

        tickets = taskManager.updateSubtask("Билеты", "Найти выгодные даты", 6, TaskStatus.DONE);
        list2 = new ArrayList<>();
        list2.add(tickets);
        vacation = taskManager.updateEpic("Отпуск","Запланировать путешествие",7, list2);
        tickets.setMyEpic(vacation);

        System.out.println(taskManager.tasks.toString());
        System.out.println(taskManager.subtasks.toString());
        System.out.println(taskManager.epics.toString());


        taskManager.deleteTaskById(2);
        taskManager.deleteEpicById(5);

        System.out.println(taskManager.getAllTasksList());
        System.out.println(taskManager.getAllSubtasksList());
        System.out.println(taskManager.getAllEpicsList());
    }
}
