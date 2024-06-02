package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static service.Converts.formatter;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return Type.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        } else {
            return startTime.plusMinutes(duration.toMinutes());
        }
    }

    public static boolean taskFieldsEquals(Task task1, Task task2) {
        return task1.getName().equals(task2.getName()) &&
                task1.getDescription().equals(task2.getDescription()) &&
                task1.getId() == task2.getId() &&
                task1.getStatus().equals(task2.getStatus()) &&
                task1.getDuration().equals(task2.getDuration()) &&
                task1.getStartTime().equals(task2.getStartTime());
    }

    @Override
    public String toString() {
        String durationString = Optional.ofNullable(duration)
                .map(Duration::toMinutes)
                .map(Object::toString)
                .orElse("не задано");
        String startTimeString = Optional.ofNullable(startTime)
                .map(l -> l.format(formatter))
                .orElse("не задано");
        String endTimeString = Optional.ofNullable(getEndTime())
                .map(l -> l.format(formatter))
                .orElse("не задано");

        return getType() + "{" + "name= " + name + ", description= " + description + ", id= " + id + ", status= " + status + ", start= " + startTimeString + ", duration= " + durationString + ", end= " + endTimeString + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
