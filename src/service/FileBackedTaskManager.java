package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.*;
import java.nio.file.Files;

import java.nio.file.Paths;


public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File fileToAutoSave;

    public FileBackedTaskManager(File file) {
        this.fileToAutoSave = file;
    }

    public enum Type {
        TASK,
        SUBTASK,
        EPIC
    }

    //пыталась сделать один метод fromString через типизацию метода, но не смогла
    /*private static <T extends Task> T fromString(String value) {
        String[] split = value.split(",");

        int id = 0;
        try {
            id = Integer.parseInt(split[0]);
        } catch (NumberFormatException nfe) {
        }

        String name = split[2];

        Status status = Status.valueOf(split[3]);

        String description = split[4];

        int epicForSubtask = 0;

        if (split.length > 5) {
            try {
                epicForSubtask = Integer.parseInt(split[5]);
            } catch (NumberFormatException nfe) {
            }
        }

        T  task = null;

        if (split[1].equals(Type.TASK.toString())) {
            task = (T) new Task(name, description);
            task.setId(id);
            task.setStatus(status);


        }
        else if (split[1].equals(Type.EPIC.toString())) {
            task = (T) new Epic(name, description);
            task.setId(id);
            task.setStatus(status);

        }
        else if (split[1].equals(Type.SUBTASK.toString())) {
            task = (T) new Subtask(name, description, epicForSubtask);
            task.setId(id);
            task.setStatus(status);

        }
        return task;
    }


    static <T extends Task> FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        if (Files.exists(Paths.get(file.toURI()))) {

            try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
                //Files.readString(file.toPath()); не поняла как это использовать
                int maxId = 0;

                while (br.ready()) {
                    String value = br.readLine();
                    if (value.startsWith("id")) {
                        continue;
                    }

                    T  convertedFromStringTask = fromString(value);

                    if (convertedFromStringTask.getId() > maxId) {
                        maxId = convertedFromStringTask.getId();
                    }

                    if (convertedFromStringTask.getName().equals("Task")) {

                        fileBackedTaskManager.tasks.put(convertedFromStringTask.getId(), convertedFromStringTask);

                    }
                    else if (convertedFromStringTask.getName().equals("Epic")) {
                        Epic epic = (Epic)convertedFromStringTask;

                        fileBackedTaskManager.epics.put(epic.getId(), epic);
                    }
                    else if (convertedFromStringTask.getName().equals("Subtask")) {
                        Subtask subtask = (Subtask)convertedFromStringTask;

                        Epic myEpic = fileBackedTaskManager.epics.get(subtask.getMyEpicId());
                        myEpic.getMySubtasksIdList().add(convertedFromStringTask.getId());

                        fileBackedTaskManager.subtasks.put(subtask.getId(), subtask);
                    }
                }
                fileBackedTaskManager.id = maxId;
            } catch (IOException e) {
                throw new ManagerSaveException(e);
            }
        }
        return fileBackedTaskManager;
    }*/

    private static Task taskFromString(String value) {
        String[] split = value.split(",");

        int id = 0;
        try {
            id = Integer.parseInt(split[0]);
        } catch (NumberFormatException nfe) {
        }

        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];

        Task task = new Task(name, description);
        task.setId(id);
        task.setStatus(status);
        return task;
    }


    private static Epic epicFromString(String value) {
        String[] split = value.split(",");

        int id = 0;
        try {
            id = Integer.parseInt(split[0]);
        } catch (NumberFormatException nfe) {
        }

        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];

        Epic epic = new Epic(name, description);
        epic.setId(id);
        epic.setStatus(status);
        return epic;
    }

    private static Subtask subtaskFromString(String value) {
        String[] split = value.split(",");

        int id = 0;
        try {
            id = Integer.parseInt(split[0]);
        } catch (NumberFormatException nfe) {
        }

        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];

        int epicForSubtask = 0;
        if (split.length > 5) {
            try {
                epicForSubtask = Integer.parseInt(split[5]);
            } catch (NumberFormatException nfe) {
            }
        }

        Subtask subtask = new Subtask(name, description, epicForSubtask);
        subtask.setId(id);
        subtask.setStatus(status);
        return subtask;
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        //получилось реализовать вынос функциональности fromString только с помощью трех отдельных методов для Task, Epic и Subtask
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        if (Files.exists(Paths.get(file.toURI()))) {

            try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
                //Files.readString(file.toPath()); не поняла как это использовать
                int maxId = 0;

                while (br.ready()) {
                    String value = br.readLine();
                    if (value.startsWith("id")) {
                        continue;
                    }

                    if (value.contains(Type.EPIC.toString())) {
                        Epic convertedFromStringEpic = epicFromString(value);
                        fileBackedTaskManager.epics.put(convertedFromStringEpic.getId(), convertedFromStringEpic);
                        if (convertedFromStringEpic.getId() > maxId) {
                            maxId = convertedFromStringEpic.getId();
                        }
                    } else if (value.contains(Type.SUBTASK.toString())) {
                        Subtask convertedFromStringSubtask = subtaskFromString(value);
                        Epic myEpic = fileBackedTaskManager.epics.get(convertedFromStringSubtask.getMyEpicId());
                        myEpic.getMySubtasksIdList().add(convertedFromStringSubtask.getId());
                        fileBackedTaskManager.subtasks.put(convertedFromStringSubtask.getId(), convertedFromStringSubtask);
                        if (convertedFromStringSubtask.getId() > maxId) {
                            maxId = convertedFromStringSubtask.getId();
                        }
                    } else {
                        Task convertedFromStringTask = taskFromString(value);
                        fileBackedTaskManager.tasks.put(convertedFromStringTask.getId(), convertedFromStringTask);
                        if (convertedFromStringTask.getId() > maxId) {
                            maxId = convertedFromStringTask.getId();
                        }
                    }
                }
                fileBackedTaskManager.id = maxId;
            } catch (IOException e) {
                throw new ManagerSaveException(e);
            }
        }
        return fileBackedTaskManager;
    }

//    static FileBackedTaskManager loadFromFile(File file) {
//        //это вариант, где вся функциональность внутри метода load,
//        //не получилось реализовать метод load с выносом функциональности в отдельный метод fromString
//        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
//
//        if (Files.exists(Paths.get(file.toURI()))) {
//
//            try (FileReader reader = new FileReader(file); BufferedReader br = new BufferedReader(reader)) {
//                //Files.readString(file.toPath()); не поняла как это использовать
//                int maxId = 0;
//
//                while (br.ready()) {
//                    String value = br.readLine();
//                    if (value.startsWith("id")) {
//                      continue;
//                    }
//                    String[] split = value.split(",");
//
//                    int id = 0;
//                    try {
//                        id = Integer.parseInt(split[0]);
//                    } catch (NumberFormatException nfe) {
//                    }
//                    if (id > maxId) {
//                        maxId = id;
//                    }
//
//                    String name = split[2];
//
//                    Status status = Status.valueOf(split[3]);
//
//                    String description = split[4];
//
//                    int epicForSubtask = 0;
//
//                    if (split.length > 5) {
//                        try {
//                            epicForSubtask = Integer.parseInt(split[5]);
//                        } catch (NumberFormatException nfe) {
//                        }
//                    }
//
//                    if (split[1].equals(Type.TASK.toString())) {
//                        Task task = new Task(name, description);
//                        task.setId(id);
//                        task.setStatus(status);
//
//                        fileBackedTaskManager.tasks.put(task.getId(), task);
//
//                    }
//                    else if (split[1].equals(Type.EPIC.toString())) {
//                        Epic epic = new Epic(name, description);
//                        epic.setId(id);
//                        epic.setStatus(status);
//
//                        fileBackedTaskManager.epics.put(epic.getId(), epic);
//                    }
//                    else if (split[1].equals(Type.SUBTASK.toString())) {
//                        Subtask subtask = new Subtask(name, description, epicForSubtask);
//                        subtask.setId(id);
//                        subtask.setStatus(status);
//
//                        Epic myEpic = fileBackedTaskManager.epics.get(subtask.getMyEpicId());
//                        myEpic.getMySubtasksIdList().add(id);
//
//                        fileBackedTaskManager.subtasks.put(subtask.getId(), subtask);
//                    }
//                }
//                fileBackedTaskManager.id = maxId;
//            } catch (IOException e) {
//                throw new ManagerSaveException(e);
//            }
//        }
//        return fileBackedTaskManager;
//    }

    private static String convertTaskToString(Task task) {

        return task.getId() +
                "," +
                "TASK" +
                "," +
                task.getName() +
                "," +
                (task.getStatus()).toString() +
                "," +
                task.getDescription();
    }

    private static String convertEpicToString(Epic epic) {

        return epic.getId() +
                "," +
                "EPIC" +
                "," +
                epic.getName() +
                "," +
                (epic.getStatus()).toString() +
                "," +
                epic.getDescription();
    }

    private static String convertSubtaskToString(Subtask subtask) {

        return subtask.getId() +
                "," +
                "SUBTASK" +
                "," +
                subtask.getName() +
                "," +
                (subtask.getStatus()).toString() +
                "," +
                subtask.getDescription() +
                "," +
                subtask.getMyEpicId();
    }

    public void save() {
        try (Writer writer = new FileWriter(fileToAutoSave)) {
            String firstLine = "id,type,name,status,description,epic";
            writer.write(firstLine + "\n");
            for (Task task : tasks.values()) {
                String convertedString = convertTaskToString(task);
                writer.write(convertedString + "\n");
            }
            for (Epic epic : epics.values()) {
                String convertedString = convertEpicToString(epic);
                writer.write(convertedString + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                String convertedString = convertSubtaskToString(subtask);
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
}