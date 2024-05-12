package service;

import model.*;

public class Converts {

    static Task fromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];
        Type type = Type.valueOf(split[1]);
        if (type == Type.TASK) {
            Task task = new Task(name, description);
            task.setId(id);
            task.setStatus(status);
            return task;
        } else if (type == Type.EPIC) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);
            return epic;
        } else {
            int epicForSubtask = Integer.parseInt(split[5]);
            Subtask subtask = new Subtask(name, description, epicForSubtask);
            subtask.setId(id);
            subtask.setStatus(status);
            return subtask;
        }
    }

    //Эти методы так и оставить три штуки? или тоже надо в один объединить?
    static String convertTaskToString(Task task) {

        return task.getId() +
                "," +
                (task.getType()).toString() +
                "," +
                task.getName() +
                "," +
                (task.getStatus()).toString() +
                "," +
                task.getDescription();
    }

    static String convertEpicToString(Epic epic) {

        return epic.getId() +
                "," +
                (epic.getType()).toString() +
                "," +
                epic.getName() +
                "," +
                (epic.getStatus()).toString() +
                "," +
                epic.getDescription();
    }

    static String convertSubtaskToString(Subtask subtask) {

        return subtask.getId() +
                "," +
                (subtask.getType()).toString() +
                "," +
                subtask.getName() +
                "," +
                (subtask.getStatus()).toString() +
                "," +
                subtask.getDescription() +
                "," +
                subtask.getMyEpicId();
    }

}