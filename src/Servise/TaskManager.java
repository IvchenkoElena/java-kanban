package Servise;
import Model.Epic;
import Model.Subtask;
import Model.Task;
import Model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();

    int id = 0;
    private int generateId() {
        return ++id;
    }

    public void createTask(Task task){
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public void updateTask(Task task){
        tasks.put(task.getId(), task);
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
        epic.setStatus(determineEpicStatus(id));
    }

    public void updateEpic(Epic epic){
        epics.put(epic.getId(), epic);
        epic.setStatus(determineEpicStatus(id));
    }

    private TaskStatus determineEpicStatus(int id) {
        Epic epic = epics.get(id);
        if (epic == null){
            //не до конца поняла, на null нужно проверить Эпик?
            // и что сделать, если он будет равен null?
        }
        TaskStatus status;
        int isNew = 0;
        int isDone = 0;
        boolean isAllNew = false;
        boolean isAllDone = false;
        ArrayList<Integer> mySubtasksIdList = epic.getMySubtasksIdList();
        if (mySubtasksIdList != null){ //или на null надо было проверить список id сабтасок?
            // и как обработать вариант, если он Null?
            for (Integer i : mySubtasksIdList) {
                Subtask subtask = subtasks.get(i);
                if (subtask.getStatus().equals(TaskStatus.valueOf("NEW"))){
                    isNew++;
                }
                if (subtask.getStatus().equals(TaskStatus.valueOf("DONE"))){
                    isDone++;
                }
            }
        }
        if (isNew == mySubtasksIdList.size()){
            isAllNew = true;
        }
        if (isDone == mySubtasksIdList.size()){
            isAllDone = true;
        }
        if ((mySubtasksIdList.isEmpty()) || (isAllNew)){ //может тут вместо isEmpty нужно предусмотреть вариант Null?
            status = TaskStatus.valueOf("NEW");
        } else if (isAllDone) {
            status = TaskStatus.valueOf("DONE");
        } else {
            status = TaskStatus.valueOf("IN_PROGRESS");
        }
        return status;
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
        ArrayList<Integer> list = getEpicById(id).getMySubtasksIdList();
        for (Integer i : list){
            deleteSubtaskById(i);
        }
        epics.remove(id);
    }

    public void createSubtask(Subtask subtask){
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        Epic myEpic = epics.get(subtask.getMyEpicId());
        ArrayList<Integer> subtasksIdList = myEpic.getMySubtasksIdList();
        subtasksIdList.add(id);
        myEpic.setMySubtasksIdList(subtasksIdList);
        myEpic.setStatus(determineEpicStatus(subtask.getMyEpicId()));
    }

    public void updateSubtask(Subtask subtask){
        subtasks.put(subtask.getId(), subtask);
        Epic myEpic = epics.get(subtask.getMyEpicId());
        myEpic.setStatus(determineEpicStatus(subtask.getMyEpicId()));
    }

    public ArrayList<Subtask> getAllSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void deleteSubtaskById(int id) {
        subtasks.remove(id);
    }

    public ArrayList<Subtask> getMySubtasksListByEpicId(int id) {
        Epic epic = epics.get(id);
        ArrayList<Integer> mySubtasksIdList = epic.getMySubtasksIdList();
        ArrayList<Subtask> mySubtasksList = new ArrayList<>();
        for (Integer i : mySubtasksIdList){
            mySubtasksList.add(subtasks.get(i));
        }
    return mySubtasksList;
    }
}
