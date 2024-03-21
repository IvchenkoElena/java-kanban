package model;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Integer> mySubtasksIdList;

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
    public String toString() {
        return "Epic-" + super.toString() + ", mySubtasksIdList = " + mySubtasksIdList + "}";
    }
}
