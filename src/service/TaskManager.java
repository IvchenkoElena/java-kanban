package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public interface TaskManager {

    HistoryManager getHistoryManager();

    List<Task> getPrioritizedTasks();

    int generateId();

    int createTask(Task task);

    void updateTask(Task task);

    List<Task> getAllTasksList();

    void deleteAllTasks();

    Task getTaskById(int id);

    void deleteTaskById(int id);

    int createEpic(Epic epic);

    void updateEpic(Epic epic);

    List<Epic> getAllEpicsList();

    void deleteAllEpics();

    Epic getEpicById(int id);

    void deleteEpicById(int id);

    int createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    List<Subtask> getAllSubtasksList();

    void deleteAllSubtasks();

    Subtask getSubtaskById(int id);

    void deleteSubtaskById(int id);

    List<Subtask> getMySubtasksListByEpicId(int id);
}
