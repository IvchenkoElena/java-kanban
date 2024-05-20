package service;

import model.*;

import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Converts {
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    static Task fromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        String name = split[2];
        Status status = Status.valueOf(split[3]);
        String description = split[4];

        LocalDateTime start;
        if (split[5].isBlank()) {
            start = null;
        } else {
            start = LocalDateTime.parse(split[5], formatter);
        }

        Duration duration;
        if (split[6].isBlank()) {
            duration = null;
        } else {
            duration = Duration.ofMinutes(Long.parseLong(split[6]));
        }

        LocalDateTime end;
        if (split[7].isBlank()) {
            end = null;
        } else {
            end = LocalDateTime.parse(split[7], formatter);
        }

        Type type = Type.valueOf(split[1]);

        if (type == Type.TASK) {
            Task task = new Task(name, description);
            task.setId(id);
            task.setStatus(status);
            task.setStartTime(start);
            task.setDuration(duration);
            return task;
        } else if (type == Type.EPIC) {
            Epic epic = new Epic(name, description);
            epic.setId(id);
            epic.setStatus(status);
            epic.setStartTime(start);
            epic.setDuration(duration);
            epic.setEndTime(end);
            return epic;
        } else {
            int epicForSubtask = Integer.parseInt(split[8]);
            Subtask subtask = new Subtask(name, description, epicForSubtask);
            subtask.setId(id);
            subtask.setStatus(status);
            subtask.setStartTime(start);
            subtask.setDuration(duration);
            return subtask;
        }
    }

    static String convertToString(Task task) {

        String startString = Optional.ofNullable(task.getStartTime())
                .map(l -> l.format(formatter))
                .orElse(" ");

        String durationString = Optional.ofNullable(task.getDuration())
                .map(Duration::toMinutes)
                .map(Object::toString)
                .orElse(" ");

        String endString = Optional.ofNullable(task.getEndTime())
                .map(l -> l.format(formatter))
                .orElse(" ");

        String result = task.getId() +
                "," +
                (task.getType()).toString() +
                "," +
                task.getName() +
                "," +
                (task.getStatus()).toString() +
                "," +
                task.getDescription() +
                "," +
                startString +
                "," +
                durationString +
                "," +
                endString;

        if (task instanceof Subtask subtask) {
            return result +
                    "," +
                    subtask.getMyEpicId();
        } else {
            return result;
        }
    }
}
