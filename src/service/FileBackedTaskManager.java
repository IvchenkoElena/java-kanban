package service;

import model.*;

import java.io.*;
import java.nio.file.Files;
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
                String[] lines = fileData.split("\n");
                int maxId = 0;
                for (int i = 1; i < lines.length; i++) {
                    String value = lines[i];
                    Task task = Converts.fromString(value);
                    switch (task.getType()) {
                        case TASK:
                            fileBackedTaskManager.tasks.put(task.getId(), task);
                            if (task.getStartTime() != null && task.getEndTime() != null) {
                                fileBackedTaskManager.prioritizedTasksSet.add(task);
                            }
                        case SUBTASK:
                            if (task instanceof Subtask subtask) {
                                fileBackedTaskManager.subtasks.put(task.getId(), subtask);
                                if (task.getStartTime() != null && task.getEndTime() != null) {
                                    fileBackedTaskManager.prioritizedTasksSet.add(task);
                                }
                                Epic myEpic = fileBackedTaskManager.epics.get(subtask.getMyEpicId());
                                myEpic.getMySubtasksIdList().add(task.getId());
                            }
                        case EPIC:
                            if (task instanceof Epic) {
                                fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                            }
                    }
                    if (task.getId() > maxId) {
                        maxId = task.getId();
                    }
                }
                fileBackedTaskManager.generatedId = maxId;
            } catch (IOException e) {
                throw new ManagerSaveException(e);
            }
        }
        return fileBackedTaskManager;
    }

    private void save() {
        try (Writer writer = new FileWriter(fileToAutoSave)) {
            String firstLine = "id,type,name,status,description,start,duration,end,epic";
            writer.write(firstLine + "\n");
            for (Task task : tasks.values()) {
                String convertedString = Converts.convertToString(task);
                writer.write(convertedString + "\n");
            }
            for (Epic epic : epics.values()) {
                String convertedString = Converts.convertToString(epic);
                writer.write(convertedString + "\n");
            }
            for (Subtask subtask : subtasks.values()) {
                String convertedString = Converts.convertToString(subtask);
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