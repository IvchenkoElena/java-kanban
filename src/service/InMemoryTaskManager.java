package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected int id = 0;

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public int generateId() {
        return ++id;
    }

    @Override
    public int createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public List<Task> getAllTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : tasks.values()) {
            int id = task.getId();
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void deleteTaskById(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        determineEpicStatus(id);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            determineEpicStatus(id);
        }
    }

    @Override
    public void determineEpicStatus(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        int isNew = 0;
        int isDone = 0;
        boolean isAllNew = false;
        boolean isAllDone = false;
        List<Integer> mySubtasksIdList = epic.getMySubtasksIdList();
        for (Integer subtaskId : mySubtasksIdList) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus().equals(Status.NEW)) {
                isNew++;
            }
            if (subtask.getStatus().equals(Status.DONE)) {
                isDone++;
            }
        }
        if (isNew == mySubtasksIdList.size()) {
            isAllNew = true;
        }
        if (isDone == mySubtasksIdList.size()) {
            isAllDone = true;
        }
        if ((mySubtasksIdList.isEmpty()) || (isAllNew)) {
            epic.setStatus(Status.NEW);
        } else if (isAllDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


    @Override
    public List<Epic> getAllEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            int id = epic.getId();
            historyManager.remove(id);
        }
        for (Subtask subtask : subtasks.values()) {
            int id = subtask.getId();
            historyManager.remove(id);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void deleteEpicById(int id) {
        List<Integer> mySubtaskIdList = epics.get(id).getMySubtasksIdList();
        for (Integer subtaskId : mySubtaskIdList) {
            historyManager.remove(subtaskId);
            subtasks.remove(subtaskId);
        }
        historyManager.remove(id);
        epics.remove(id);
    }

    @Override
    public int createSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic myEpic = epics.get(subtask.getMyEpicId());
        myEpic.getMySubtasksIdList().add(id);
        determineEpicStatus(subtask.getMyEpicId());
        return subtask.getId();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            determineEpicStatus(subtask.getMyEpicId());
        }
    }

    @Override
    public List<Subtask> getAllSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            int id = subtask.getId();
            historyManager.remove(id);
        }
        subtasks.clear();
        for (Integer id : epics.keySet()) {
            epics.get(id).getMySubtasksIdList().clear();
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getMyEpicId()).getMySubtasksIdList().remove(Integer.valueOf(id));
        }
        historyManager.remove(id);
        subtasks.remove(id);
    }

    @Override
    public List<Subtask> getMySubtasksListByEpicId(int id) {
        Epic epic = epics.get(id);
        List<Integer> mySubtasksIdList = epic.getMySubtasksIdList();
        List<Subtask> mySubtasksList = new ArrayList<>();
        for (Integer subtaskId : mySubtasksIdList) {
            mySubtasksList.add(subtasks.get(subtaskId));
        }
        return mySubtasksList;
    }
}
