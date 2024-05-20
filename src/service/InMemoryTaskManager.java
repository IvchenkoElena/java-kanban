package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected int generatedId = 0;
    protected final Set<Task> prioritizedTasksSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasksSet);
    }

    @Override
    public int generateId() {
        return ++generatedId;
    }

    @Override
    public boolean isTimeCross(Task task1, Task task2) {
        Task first;
        Task second;
        if (task1.getStartTime().isBefore(task2.getStartTime())) {
            first = task1;
            second = task2;
        } else if (task1.getStartTime().isAfter(task2.getStartTime())) {
            first = task2;
            second = task1;
        } else {
            return true;
        }
        return first.getEndTime().isAfter(second.getStartTime());
    }

    @Override
    public int createTask(Task task) {
        if (task.getStartTime() != null && task.getEndTime() != null) {
            if (getPrioritizedTasks().stream()
                    .noneMatch(t -> isTimeCross(t, task))) {
                prioritizedTasksSet.add(task);
            } else {
                System.out.println("Новая задача не может быть создана, время пересекается");
                return -1;
            }
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            if (task.getStartTime() != null && task.getEndTime() != null) {
                if (getPrioritizedTasks().stream()
                        .filter(i -> i.getId() != task.getId())
                        .noneMatch(t -> isTimeCross(t, task))) {
                    if (oldTask.getStartTime() != null && oldTask.getEndTime() != null) {
                        prioritizedTasksSet.remove(oldTask);
                    }
                    prioritizedTasksSet.add(task);
                } else {
                    System.out.println("Задача c ID " + task.getId() + " не может быть обновлена, время пересекается");
                    return;
                }
            } else {
                if (oldTask.getStartTime() != null && oldTask.getEndTime() != null) {
                    prioritizedTasksSet.remove(oldTask);
                }
            }
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
            if (task.getStartTime() != null && task.getEndTime() != null) {
                prioritizedTasksSet.remove(task);
            }
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
        Task task = tasks.get(id);
        if (task == null) {
            return;
        }
        tasks.remove(id);
        historyManager.remove(id);
        if (task.getStartTime() != null && task.getEndTime() != null) {
            prioritizedTasksSet.remove(task);
        }
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        determineEpicStatus(generatedId);
        determineEpicDuration(generatedId);
        determineEpicStartTime(generatedId);
        determineEpicEndTime(generatedId);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            determineEpicStatus(generatedId);
            determineEpicDuration(generatedId);
            determineEpicStartTime(generatedId);
            determineEpicEndTime(generatedId);
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
    public void determineEpicStartTime(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        epic.setStartTime(epic.getMySubtasksIdList().stream()
                .map(subtasks::get)
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null));
    }

    @Override
    public void determineEpicEndTime(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        epic.setEndTime(epic.getMySubtasksIdList().stream()
                .map(subtasks::get)
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null));
    }

    @Override
    public void determineEpicDuration(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        epic.setDuration(epic.getMySubtasksIdList().stream()
                .map(subtasks::get)
                .map(Task::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration::plus)
                .orElse(null));
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
        if (epics.get(id) == null) {
            return;
        }
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
        if (subtask.getStartTime() != null && subtask.getEndTime() != null) {
            if (getPrioritizedTasks().stream()
                    .noneMatch(t -> isTimeCross(t, subtask))) {
                prioritizedTasksSet.add(subtask);
            } else {
                System.out.println("Подзадача c ID " + subtask.getId() + " не может быть создана, время пересекается");
                return -1;
            }
        }
        subtask.setId(generateId());
        int subtaskId = subtask.getId();
        subtasks.put(subtaskId, subtask);
        Epic myEpic = epics.get(subtask.getMyEpicId());
        myEpic.getMySubtasksIdList().add(subtaskId);
        determineEpicStatus(subtask.getMyEpicId());
        determineEpicDuration(subtask.getMyEpicId());
        determineEpicStartTime(subtask.getMyEpicId());
        determineEpicEndTime(subtask.getMyEpicId());
        return subtaskId;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            return;
        }
        if (subtasks.containsKey(subtask.getId())) {
            Subtask oldSubtask = subtasks.get(subtask.getId());
            if (subtask.getStartTime() != null && subtask.getEndTime() != null) {
                if (getPrioritizedTasks().stream()
                        .filter(t -> t.getId() != subtask.getId())
                        .noneMatch(t -> isTimeCross(t, subtask))) {
                    if (oldSubtask.getStartTime() != null && oldSubtask.getEndTime() != null) {
                        prioritizedTasksSet.remove(oldSubtask);
                    }
                    prioritizedTasksSet.add(subtask);
                } else {
                    System.out.println("Подзадача c ID " + subtask.getId() + " не может быть обновлена, время пересекается");
                    return;
                }
            } else {
                if (oldSubtask.getStartTime() != null && oldSubtask.getEndTime() != null) {
                    prioritizedTasksSet.remove(oldSubtask);
                }
            }
            subtasks.put(subtask.getId(), subtask);
            determineEpicStatus(subtask.getMyEpicId());
            determineEpicDuration(subtask.getMyEpicId());
            determineEpicStartTime(subtask.getMyEpicId());
            determineEpicEndTime(subtask.getMyEpicId());
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
            if (subtask.getStartTime() != null && subtask.getEndTime() != null) {
                prioritizedTasksSet.remove(subtask);
            }
        }
        subtasks.clear();
        for (Integer id : epics.keySet()) {
            epics.get(id).getMySubtasksIdList().clear();
            determineEpicStatus(id);
            determineEpicDuration(id);
            determineEpicStartTime(id);
            determineEpicEndTime(id);
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        if (subtask.getStartTime() != null && subtask.getEndTime() != null) {
            prioritizedTasksSet.remove(subtask);
        }
        if (subtasks.containsKey(id)) {
            epics.get(subtasks.get(id).getMyEpicId()).getMySubtasksIdList().remove(Integer.valueOf(id));
            determineEpicStatus(subtasks.get(id).getMyEpicId());
            determineEpicDuration(subtasks.get(id).getMyEpicId());
            determineEpicStartTime(subtasks.get(id).getMyEpicId());
            determineEpicEndTime(subtasks.get(id).getMyEpicId());
        }
        historyManager.remove(id);
        subtasks.remove(id);
    }

    @Override
    public List<Subtask> getMySubtasksListByEpicId(int id) {
        return epics.get(id).getMySubtasksIdList().stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }
}
