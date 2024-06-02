package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int myEpicId;

    public Subtask(String name, String description, int myEpicId) {
        super(name, description);
        this.myEpicId = myEpicId;
    }

    public Subtask(String name, String description, Status status, Duration duration, LocalDateTime startTime, int myEpicId) {
        super(name, description, status, duration, startTime);
        this.myEpicId = myEpicId;
    }

    public int getMyEpicId() {
        return myEpicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    public static boolean subtaskFieldsExceptIdEquals(Subtask subtask1, Subtask subtask2) {
        return subtask1.getName().equals(subtask2.getName()) &&
                subtask1.getDescription().equals(subtask2.getDescription()) &&
                subtask1.getStatus().equals(subtask2.getStatus()) &&
                subtask1.getDuration().equals(subtask2.getDuration()) &&
                subtask1.getStartTime().equals(subtask2.getStartTime()) &&
                subtask1.getMyEpicId() == subtask2.getMyEpicId();
    }

    @Override
    public String toString() {
        return super.toString() + ", myEpicId = " + myEpicId + "}";
    }
}
