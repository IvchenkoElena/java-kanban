package service;

import model.*;

import java.io.*;
import java.nio.file.Files;

import java.nio.file.Path;
import java.nio.file.Paths;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File fileToAutoSave;

    public FileBackedTaskManager(File file) {
        this.fileToAutoSave = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        if (Files.exists(Paths.get(file.toURI()))) {

            try {
                String fileData = Files.readString(file.toPath());
                String[] lines = fileData.split(System.lineSeparator());
                int maxId = 0;
                for (int i = 1; i < lines.length; i++) {
                    String value = lines[i];
                    Task task = Converts.fromString(value);
                    switch (task.getType()) {
                        case TASK:
                            fileBackedTaskManager.tasks.put(task.getId(), task);
                        case SUBTASK:
                            fileBackedTaskManager.subtasks.put(task.getId(), (Subtask)task);
                        case EPIC:
                            fileBackedTaskManager.epics.put(task.getId(), (Epic)task);
                    }
                    if (task.getId() > maxId) {
                        maxId = task.getId();
                    }
                }
                fileBackedTaskManager.id = maxId;
            } catch (IOException e) {
                throw new ManagerSaveException(e);
            }
        }
        return fileBackedTaskManager;
    }

    private void save() {
        try (Writer writer = new FileWriter(fileToAutoSave)) {
            String firstLine = "id,type,name,status,description,epic";
            writer.write(firstLine + "\n");
            for (Task task : tasks.values()) {
                String convertedString = Converts.convertTaskToString(task);
                writer.write(convertedString + "\n");
            }
            for (Epic epic : epics.values()) {
                String convertedString = Converts.convertEpicToString(epic);
                writer.write(convertedString + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                String convertedString = Converts.convertSubtaskToString(subtask);
                writer.write(convertedString + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

    @Override
    public int createTask(Task task) {
        int current = super.createTask(task);
        save();
        return current;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public int createEpic(Epic epic) {
        int current = super.createEpic(epic);
        save();
        return current;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int current = super.createSubtask(subtask);
        save();
        return current;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }


    public static void main(String[] args) {
        File file = Path.of("file.csv").toFile();
        final TaskManager taskManager2 = Managers.load(file);

        System.out.println("вызов принта восстановления из фала");
        printAllTasks(taskManager2);

        Task task8 = new Task("Задача 8", "Вынести мусор, опять?");
        taskManager2.createTask(task8);

        System.out.println("вызов шестого принта");
        printAllTasks(taskManager2);


        Task newTask9 = new Task("Задача 1", "Забрать посылку");
        newTask9.setStatus(Status.IN_PROGRESS);
        newTask9.setId(1);
        taskManager2.updateTask(newTask9);

        System.out.println("вызов седьмого принта");
        printAllTasks(taskManager2);

        taskManager2.getTaskById(1);

        System.out.println("восьмой вызов принта");
        printAllTasks(taskManager2);

        System.out.println(file);
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