package service;

import java.io.File;

public class Managers {

    public static TaskManager getInMemoryDefault() {
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefault(File file) {
        return new FileBackedTaskManager(file);
    }

    public static TaskManager load(File file) {
        return FileBackedTaskManager.loadFromFile(file);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
