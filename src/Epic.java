import java.util.ArrayList;

public class Epic extends Task {


    private ArrayList<Subtask> mySubtasksList;
    public Epic(String name, String description, int taskId, TaskStatus status, ArrayList<Subtask> mySubtasksList) {
        super(name, description, taskId, status);
        this.mySubtasksList = mySubtasksList;
    }

    public ArrayList<Subtask> getMySubtasksList() {
        return mySubtasksList;
    }

    public void setMySubtasksList(ArrayList<Subtask> mySubtasksList) {
        this.mySubtasksList = mySubtasksList;
    }

    @Override
    public String toString() {
        return "Epic-" + super.toString() + ", mySubtasksList = " + mySubtasksList.size() + "}";
    }
}
