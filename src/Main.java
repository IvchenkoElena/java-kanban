import http.DurationAdapter;
import http.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.Task;
import model.Epic;
import model.Subtask;
import model.Status;
import service.IntersectionException;
import service.Managers;
import service.NotFoundException;
import service.TaskManager;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        File file = Path.of("src/database/file.csv").toFile();

        //final TaskManager taskManager = Managers.getInMemoryDefault();
        final TaskManager taskManager = Managers.getDefault(file);

        Task tasktest = new Task("Задача 18", "Вынести мусор");
        System.out.println(tasktest);


        System.out.println("Проверочный вызов пустого принта");
        printAllTasks(taskManager);

        try {
            taskManager.getTaskById(90);
        } catch (NotFoundException exception) {
            System.out.println("Поймано NotFound исключение: " + exception.getMessage());
        }

        Task task11 = new Task("Задача 1", "Купить продукты");
        final int taskId = taskManager.createTask(task11);
        System.out.println("Вызов 1 принта");
        printAllTasks(taskManager);


        Task newTask = new Task("Задача 1", "Купить разных продуктов");
        newTask.setId(taskId);
        taskManager.updateTask(newTask);
        System.out.println("Вызов 2 принта");
        printAllTasks(taskManager);

        Task task33 = new Task("Задача 3", "Вынести мусор3");
        task33.setId(1);
        task33.setStartTime(LocalDateTime.of(2024, 5, 19, 12, 15));
        task33.setDuration(Duration.ofMinutes(90));
        taskManager.updateTask(task33);
        System.out.println("Вызов 3 принта");
        printAllTasks(taskManager);

        Task task55 = new Task("Задача 5", "Вынести мусор5");
        task55.setId(1);
        taskManager.updateTask(task55);
        System.out.println("Вызов 4 принта");
        printAllTasks(taskManager);

        System.out.println("Напечатаем задачи (не сохраненные в мапу):");
        Task taskOne = new Task("Задача 17", "Купить продукты");
        taskOne.setId(17);
        Task taskTwo = new Task("Задача 18", "Вынести мусор");
        taskTwo.setId(18);
        System.out.println(taskOne);
        System.out.println(taskTwo);
        System.out.println("вызываем несуществующую в мапе задачу:");
        try {
            taskManager.getTaskById(17);
            taskManager.getTaskById(90);
        } catch (NotFoundException exception) {
            System.out.println("Поймано NotFound исключение: " + exception.getMessage());
        }

        System.out.println("первый вызов принта (в мапах и в истории ничего нет)");
        printAllTasks(taskManager);

        Task task1 = new Task("Задача 1", "Купить продукты");
        task1.setStartTime(LocalDateTime.of(2024, 5, 18, 12, 0));
        task1.setDuration(Duration.ofMinutes(80));
        taskManager.createTask(task1);
        Task task2 = new Task("Задача 2", "Вынести мусор");
        task2.setStartTime(LocalDateTime.of(2024, 5, 19, 12, 15));
        task2.setDuration(Duration.ofMinutes(90));
        taskManager.createTask(task2);
        Epic renovation2 = new Epic("Ремонт2", "Сделать ремонт2");
        taskManager.createEpic(renovation2);
        int renovation2Id = renovation2.getId();
        Subtask wall2 = new Subtask("Стены2", "Шпаклюем и штукатурим", renovation2Id);
        wall2.setStartTime(LocalDateTime.of(2024, 5, 25, 12, 0));
        wall2.setDuration(Duration.ofMinutes(89));
        taskManager.createSubtask(wall2);
        Subtask furniture2 = new Subtask("Мебель2", "Купить и собрать", renovation2Id);
        furniture2.setStartTime(LocalDateTime.of(2024, 5, 25, 13, 30));
        furniture2.setDuration(Duration.ofMinutes(90));
        taskManager.createSubtask(furniture2);
        Epic vacation = new Epic("Отпуск", "Запланировать путешествие");
        taskManager.createEpic(vacation);
        int vacationId = vacation.getId();
        Subtask tickets = new Subtask("Билеты", "Найти выгодные даты", vacationId);
        taskManager.createSubtask(tickets);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        System.out.println("второй вызов принта");
        printAllTasks(taskManager);

        try {
            taskManager.getSubtaskById(7);
            taskManager.getTaskById(1);
        } catch (NotFoundException exception) {
            System.out.println("Поймано NotFound исключение: " + exception.getMessage());
        }
        Task task3 = new Task("Задача 3", "Вынести мусор3");
        task3.setId(1);
        task3.setStartTime(LocalDateTime.of(2024, 5, 12, 12, 15));
        task3.setDuration(Duration.ofMinutes(10));
        taskManager.updateTask(task3);
        Subtask newWall = new Subtask("Стены", "Красим", renovation2Id);
        newWall.setStatus(Status.IN_PROGRESS);
        newWall.setStartTime(LocalDateTime.of(2024, 5, 25, 12, 0));
        newWall.setDuration(Duration.ofMinutes(90));
        newWall.setId(4);
        try {
            taskManager.updateSubtask(newWall);
        } catch (NotFoundException exception) {
            System.out.println("Поймано NotFound исключение: " + exception.getMessage());
        }
        Subtask newFurniture = new Subtask("Мебель", "Заказать", renovation2Id);
        newFurniture.setStatus(Status.DONE);
        newFurniture.setStartTime(LocalDateTime.of(2024, 5, 25, 12, 30));
        newFurniture.setDuration(Duration.ofMinutes(90));
        newFurniture.setId(5);
        try {
            taskManager.updateSubtask(newFurniture);
        } catch (IntersectionException exception) {
            System.out.println("Поймано исключение пересечения: " + exception.getMessage());
        }
        Subtask newTickets = new Subtask("Билеты", "Найти выгодные даты", vacationId);
        newTickets.setStatus(Status.DONE);
        newTickets.setId(7);
        try {
            taskManager.updateSubtask(newTickets);
        } catch (NotFoundException exception) {
            System.out.println("Поймано NotFound исключение: " + exception.getMessage());
        }

        System.out.println("Второй с половиной вызов принта");
        printAllTasks(taskManager);

        System.out.println("вызываем несуществующую в мапе задачу:");
        try {
            taskManager.getTaskById(9);
        } catch (NotFoundException exception) {
            System.out.println("Поймано NotFound исключение: " + exception.getMessage());
        }
        System.out.println("третий вызов принта");
        printAllTasks(taskManager);


        Task newTask1 = new Task("Задача 1 - вновь созданная", "Купить много продуктов");
        newTask1.setStatus(Status.DONE);
        newTask1.setId(1);
        taskManager.updateTask(newTask1);
        task2.setName("Задача 2 - обновленная");
        task2.setDescription("Вынести весь мусор");
        task2.setStatus(Status.IN_PROGRESS);
        task2.setStartTime(LocalDateTime.now().plus(Duration.ofDays(3)));
        task2.setDuration(Duration.ofMinutes(40));
        taskManager.updateTask(task2);
        System.out.println("Напечатаем задачи:");
        System.out.println("Старая задача c id 1");
        System.out.println(task1);
        System.out.println("Новая задача с id 1");
        System.out.println(newTask1);
        System.out.println("Обновленная задача 2");
        System.out.println(task2);
        try {
            taskManager.getEpicById(3);
        } catch (NotFoundException exception) {
            System.out.println("Поймано NotFound исключение: " + exception.getMessage());
        }

        System.out.println("четверый вызов принта");
        printAllTasks(taskManager);

        try {
            taskManager.deleteSubtaskById(4);
            taskManager.deleteSubtaskById(7);
        } catch (NotFoundException exception) {
            System.out.println("Поймано NotFound исключение: " + exception.getMessage());
        }


        System.out.println("четверый с половиной вызов принта");
        printAllTasks(taskManager);

        try {
            taskManager.deleteEpicById(6);
            taskManager.getSubtaskById(5);
        } catch (NotFoundException exception) {
            System.out.println("Поймано NotFound исключение: " + exception.getMessage());
        }


        taskManager.getTaskById(1);
        System.out.println("пятый вызов принта");
        printAllTasks(taskManager);


        final TaskManager taskManager2 = Managers.load(file);
        System.out.println();
        System.out.println(("вызов начально принта восстановления из файла").toUpperCase());
        printAllTasks(taskManager2);
        Task task8 = new Task("Задача 8", "Вынести мусор опять?");
        taskManager2.createTask(task8);
        taskManager2.getTaskById(2);
        System.out.println("вызов первого принта из файла");
        printAllTasks(taskManager2);

//        Task newTask9 = new Task("Задача новая 1", "Забрать посылку");
//        newTask9.setStatus(Status.IN_PROGRESS);
//        newTask9.setId(1);
//        taskManager2.updateTask(newTask9);

        System.out.println("вызов второго принта из файла");
        printAllTasks(taskManager2);

//        Task newTask10 = new Task("Задача новая 1", "Забрать посылку");
//        newTask10.setStatus(Status.IN_PROGRESS);
//        newTask10.setStartTime(LocalDateTime.now());
//        newTask10.setDuration(Duration.ofMinutes(55));
//        newTask10.setId(1);
//        taskManager2.updateTask(newTask10);

        taskManager2.getTaskById(1);

        System.out.println("вызов третьего принта из файла");
        printAllTasks(taskManager2);

        System.out.println("ЗАКОНЧИЛИ!!!");
        System.out.println();

        /*GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();*/

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        String response = gson.toJson(taskManager.getTaskById(1));
        System.out.println(response);

        response = gson.toJson(taskManager.getSubtaskById(5));
        System.out.println(response);

        response = gson.toJson(taskManager.getSubtaskById(6));
        System.out.println(response);

        response = gson.toJson(taskManager.getSubtaskById(8));
        System.out.println(response);

        response = gson.toJson(taskManager.getEpicById(4));
        System.out.println(response);

        response = gson.toJson(taskManager.getEpicById(7));
        System.out.println(response);
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

        System.out.println("Список задач в порядке приоритета:");
        for (Task task : manager.getPrioritizedTasks()) {
            System.out.println(task);
        }
    }
}