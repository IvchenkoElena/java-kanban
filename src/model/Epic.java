package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> mySubtasksIdList;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
        this.mySubtasksIdList = new ArrayList<>();
    }

    public Epic(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
        this.mySubtasksIdList = new ArrayList<>();
    }

    public List<Integer> getMySubtasksIdList() {
        return mySubtasksIdList;
    }

    public void setMySubtasksIdList(List<Integer> mySubtasksIdList) {
        this.mySubtasksIdList = mySubtasksIdList;
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    @Override
    public String toString() {
        return super.toString() + ", mySubtasksIdList = " + mySubtasksIdList + "}";
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public static boolean epicsFieldsExceptIdEquals(Epic epic1, Epic epic2) {
        return epic1.getName().equals(epic2.getName()) &&
                epic1.getDescription().equals(epic2.getDescription()) &&
                epic1.getStatus().equals(epic2.getStatus()) &&
                epic1.getDuration().equals(epic2.getDuration()) &&
                epic1.getMySubtasksIdList().equals(epic2.getMySubtasksIdList()) && //?
                epic1.getStartTime().equals(epic2.getStartTime());
    }

    public static boolean epicsNameAndDescriptionFieldsEquals(Epic epic1, Epic epic2) {
        return epic1.getName().equals(epic2.getName()) &&
                epic1.getDescription().equals(epic2.getDescription());
    }
}
