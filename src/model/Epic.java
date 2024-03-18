package model;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> mySubtasksIdList;

    public Epic(String name, String description) {
        super(name, description);
        this.mySubtasksIdList = new ArrayList<>();
    }

    public ArrayList<Integer> getMySubtasksIdList() {
        return mySubtasksIdList;
    }

    public void setMySubtasksIdList(ArrayList<Integer> mySubtasksIdList) {
        this.mySubtasksIdList = mySubtasksIdList;
    }

    @Override
    public String toString() {
        return "Epic-" + super.toString() + ", mySubtasksIdList = " + mySubtasksIdList + "}";
    }
}
