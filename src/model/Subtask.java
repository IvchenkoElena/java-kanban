package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int myEpicId;

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

    @Override
    public String toString() {
        return super.toString() + ", myEpicId = " + myEpicId + "}";
    }
}
