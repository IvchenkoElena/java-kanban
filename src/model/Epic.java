package model;

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
}
