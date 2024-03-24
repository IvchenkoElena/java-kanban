package service;
import model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> historyList = new LinkedList<>();
    private static int listSize = 10;

    @Override
    public void add(Task task){
        if (task == null){
            System.out.println("Введен номер не существующей задачи");
            return;
        }

        if (historyList.size() == listSize) {
            historyList.remove(0);
        }
        historyList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyList);
    }
}