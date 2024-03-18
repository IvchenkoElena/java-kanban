package service;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int id = 0;

    private int generateId() {
        return ++id;
    }

    public void createTask(Task task){
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task){
        if (tasks.containsKey(task.getId())){
            tasks.put(task.getId(), task);
        }
    }

    public ArrayList<Task> getAllTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }


    public void createEpic(Epic epic){
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        determineEpicStatus(id);
    }

    public void updateEpic(Epic epic){
        if (epics.containsKey(epic.getId())){
            epics.put(epic.getId(), epic);
            determineEpicStatus(id);
        }
    }

    private void determineEpicStatus(int id) {
        Epic epic = epics.get(id);
        if (epic == null){
            return;
        }
        TaskStatus status;
        int isNew = 0;
        int isDone = 0;
        boolean isAllNew = false;
        boolean isAllDone = false;
        ArrayList<Integer> mySubtasksIdList = epic.getMySubtasksIdList();
        for (Integer subtaskId : mySubtasksIdList) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus().equals(TaskStatus.valueOf("NEW"))){
                isNew++;
            }
            if (subtask.getStatus().equals(TaskStatus.valueOf("DONE"))){
                isDone++;
            }
        }
        if (isNew == mySubtasksIdList.size()){
            isAllNew = true;
        }
        if (isDone == mySubtasksIdList.size()){
            isAllDone = true;
        }
        if ((mySubtasksIdList.isEmpty()) || (isAllNew)){
            epic.setStatus(TaskStatus.valueOf("NEW"));
        } else if (isAllDone) {
            epic.setStatus(TaskStatus.valueOf("DONE"));
        } else {
            epic.setStatus(TaskStatus.valueOf("IN_PROGRESS"));
        }
    }

    public ArrayList<Epic> getAllEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public void deleteEpicById(int id) {
        ArrayList<Integer> mySubtaskIdList = getEpicById(id).getMySubtasksIdList();
        for (Integer subtaskId : mySubtaskIdList){
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void createSubtask(Subtask subtask){
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic myEpic = epics.get(subtask.getMyEpicId());
        myEpic.getMySubtasksIdList().add(id);
        determineEpicStatus(subtask.getMyEpicId());
    }

    public void updateSubtask(Subtask subtask){
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            determineEpicStatus(subtask.getMyEpicId());
        }
    }

    public ArrayList<Subtask> getAllSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Integer id : epics.keySet()) {
            epics.get(id).getMySubtasksIdList().clear();
        }
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getMyEpicId()).getMySubtasksIdList().remove(Integer.valueOf(id));
        }
        subtasks.remove(id);
    }

    public ArrayList<Subtask> getMySubtasksListByEpicId(int id) {
        Epic epic = epics.get(id);
        ArrayList<Integer> mySubtasksIdList = epic.getMySubtasksIdList();
        ArrayList<Subtask> mySubtasksList = new ArrayList<>();
        for (Integer subtaskId : mySubtasksIdList){
            mySubtasksList.add(subtasks.get(subtaskId));
        }
    return mySubtasksList;
    }
}
