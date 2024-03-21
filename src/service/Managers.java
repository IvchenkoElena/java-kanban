package service;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
}
