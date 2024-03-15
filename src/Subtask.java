public class Subtask extends Task {

    Epic myEpic;

    public Subtask(String name, String description, int taskId, TaskStatus status) {
        super(name, description, taskId, status);
    }

   public Epic getMyEpic() {
        return myEpic;
    }

    public void setMyEpic(Epic myEpic) {
        this.myEpic = myEpic;
    }

    @Override
    public String toString() {
        return "Subtask-" + super.toString() + ", myEpicId = " + myEpic.getTaskId() + "}";
    }
}
