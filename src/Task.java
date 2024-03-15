public class Task {
    private final String name;
    private final String description;
    private final int taskId;
    private final TaskStatus status;


    public Task(String name, String description, int taskId, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.taskId = taskId;
        this.status = status;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId;
    }


    public int getTaskId() {
        return taskId;
    }


    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Task{" + "name= " + name + ", description= " + description + ", taskId= " + taskId + ", status= " + status + "}";
    }
}

