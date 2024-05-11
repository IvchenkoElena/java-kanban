package model;

public class Subtask extends Task {
    private int myEpicId;

    public Subtask(String name, String description, int myEpicId) {
        super(name, description);
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
        return "Subtask-" + super.toString() + ", myEpicId = " + myEpicId + "}";
    }
}
