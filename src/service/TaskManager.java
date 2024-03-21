package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    int generateId();

    List<Task> getHistory();

    void createTask(Task task);

    void updateTask(Task task);

    ArrayList<Task> getAllTasksList();

    void deleteAllTasks();

    Task getTaskById(int id);

    void deleteTaskById(int id);

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void determineEpicStatus(int id);

    ArrayList<Epic> getAllEpicsList();

    void deleteAllEpics();

    Epic getEpicById(int id);

    void deleteEpicById(int id);

    void createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    ArrayList<Subtask> getAllSubtasksList();

    void deleteAllSubtasks();

    Subtask getSubtaskById(int id);

    void deleteSubtaskById(int id);

    ArrayList<Subtask> getMySubtasksListByEpicId(int id);
}
