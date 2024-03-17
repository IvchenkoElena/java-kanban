package Model;

public class Subtask extends Task {
    int myEpicId;

    public Subtask(String name, String description, int myEpicId) {
        super(name, description);
        this.myEpicId = myEpicId;
    }

    public int getMyEpicId() {
        return myEpicId;
    }

    public void setMyEpicId(int myEpicId) {
        this.myEpicId = myEpicId;
    }

    @Override
    public String toString() {
        return "Subtask-" + super.toString() + ", myEpicId = " + myEpicId + "}";
    }
}
