import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    int taskId = 0;
    public int generateId(){
        taskId++;
        return taskId;
    }

    public Task createTask(String name, String description, TaskStatus status){
        taskId = generateId();
        Task task = new Task(name, description, taskId, status);
        tasks.put(task.getTaskId(), task);
        return task;
    }


    public void updateTask(String name, String description, int taskId, TaskStatus status){
        Task task = new Task(name, description, taskId, status);
        tasks.put(task.getTaskId(), task);
    }

    public ArrayList<Task> getAllTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int taskId) {
        Task taskById = null;
        if (tasks.containsKey(taskId)) {
            taskById = tasks.get(taskId);
        }
        return taskById;
    }

    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }


    public Epic createEpic(String name, String description, ArrayList<Subtask> mySubtasksList){
        taskId = generateId();
        TaskStatus status = determineEpicStatus(mySubtasksList);
        Epic epic = new Epic(name, description, taskId, status, mySubtasksList);
        epics.put(epic.getTaskId(), epic);
        return epic;
    }

    public TaskStatus determineEpicStatus(ArrayList<Subtask> mySubtasksList) {
        int isNew = 0;
        int isDone = 0;
        TaskStatus status;
        boolean isAllNew = false;
        boolean isAllDone = false;

        for (Subtask subtask : mySubtasksList) {
            if (subtask.getStatus().equals(TaskStatus.valueOf("NEW"))){
                isNew++;
            }
            if (subtask.getStatus().equals(TaskStatus.valueOf("DONE"))){
                isDone++;
            }
        }

        if (isNew == mySubtasksList.size()){
            isAllNew = true;
        }

        if (isDone == mySubtasksList.size()){
            isAllDone = true;
        }


        if ((mySubtasksList.isEmpty()) || (isAllNew)){
            status = TaskStatus.valueOf("NEW");
        } else if (isAllDone) {
            status = TaskStatus.valueOf("DONE");
        } else {
            status = TaskStatus.valueOf("IN_PROGRESS");
        }

        return status;
    }


    public Epic updateEpic(String name, String description, int taskId, ArrayList<Subtask> mySubtasksList){
        TaskStatus status = determineEpicStatus(mySubtasksList);
        Epic epic = new Epic(name, description, taskId, status, mySubtasksList);
        epics.put(epic.getTaskId(), epic);
        return epic;
    }


    public ArrayList<Epic> getAllEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
    }

    public Epic getEpicById(int taskId) {
        Epic epicById = null;
        if (epics.containsKey(taskId)) {
            epicById = epics.get(taskId);
        }
        return epicById;
    }

    public void deleteEpicById(int taskId) {
        epics.remove(taskId);
    }



    public Subtask createSubtask(String name, String description, TaskStatus status){
        taskId = generateId();
        Subtask subtask = new Subtask(name, description, taskId, status);
        subtasks.put(subtask.getTaskId(), subtask);
        return subtask;
    }


    public Subtask updateSubtask(String name, String description, int taskId, TaskStatus status){
        Subtask subtask = new Subtask(name, description, taskId, status);
        subtasks.put(subtask.getTaskId(), subtask);
        return subtask;
    }


    public ArrayList<Subtask> getAllSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
    }

    public Subtask getSubtaskById(int taskId) {
        Subtask subtaskById = null;
        if (subtasks.containsKey(taskId)) {
            subtaskById = subtasks.get(taskId);
        }
        return subtaskById;
    }

    public void deleteSubtaskById(int taskId) {
        subtasks.remove(taskId);
    }
}
