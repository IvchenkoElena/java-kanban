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
    protected final Set<Task> prioritizedTasksSet = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(LocalDateTime::compareTo)));

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    private boolean isTimeCross(Task task1, Task task2) {
        boolean notCross = task1.getStartTime().isAfter(task2.getEndTime()) || task1.getEndTime().isBefore(task2.getStartTime());
        return !notCross;
    }

    private void determineEpicParameters(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        int isNew = 0;
        int isDone = 0;
        boolean isAllNew = false;
        boolean isAllDone = false;
        LocalDateTime minStartTime = null;
        LocalDateTime maxEndTime = null;
        Duration sumDurations = null;
        List<Integer> mySubtasksIdList = epic.getMySubtasksIdList();
        for (Integer subtaskId : mySubtasksIdList) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask.getStatus().equals(Status.NEW)) {
                isNew++;
            }
            if (subtask.getStatus().equals(Status.DONE)) {
                isDone++;
            }
            LocalDateTime currentStartTime = subtask.getStartTime();
            if (currentStartTime != null) {
                if (minStartTime != null) {
                    if (currentStartTime.isBefore(minStartTime)) {
                        minStartTime = currentStartTime;
                    }
                } else {
                    minStartTime = currentStartTime;
                }
            }
            Duration currentDuration = subtask.getDuration();
            if (currentDuration != null) {
                if (sumDurations != null) {
                    sumDurations = sumDurations.plus(currentDuration);
                } else {
                    sumDurations = currentDuration;
                }
            }
            LocalDateTime currentEndTime = subtask.getEndTime();
            if (currentEndTime != null) {
                if (maxEndTime != null) {
                    if (currentEndTime.isAfter(maxEndTime)) {
                        maxEndTime = currentEndTime;
                    }
                } else {
                    maxEndTime = currentEndTime;
                }
            }
        }
        epic.setStartTime(minStartTime);
        epic.setDuration(sumDurations);
        epic.setEndTime(maxEndTime);
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
    public int createTask(Task task) {
        if (task.getStartTime() != null && task.getEndTime() != null) {
            if (getPrioritizedTasks().stream()
                    .noneMatch(t -> isTimeCross(t, task))) {
                prioritizedTasksSet.add(task);
            } else {
                System.out.println("Новая задача не может быть создана, время пересекается");
                throw new IntersectionException("Новая задача не может быть создана, время пересекается");
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
                    prioritizedTasksSet.remove(oldTask);
                    prioritizedTasksSet.add(task);
                } else {
                    System.out.println("Задача c ID " + task.getId() + " не может быть обновлена, время пересекается");
                    throw new IntersectionException("Задача c ID " + task.getId() + " не может быть обновлена, время пересекается");
                }
            } else {
                prioritizedTasksSet.remove(oldTask);
            }
            tasks.put(task.getId(), task);
        } else {
            throw new NotFoundException("Задача для обновления c ID " + task.getId() + " не обнаружена");
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
            prioritizedTasksSet.remove(task);
        }
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        } else {
            throw new NotFoundException("Задача с id " + id + " не обнаружена");
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            Task task = getTaskById(id);
            tasks.remove(id);
            historyManager.remove(id);
            prioritizedTasksSet.remove(task);
        } else {
            throw new NotFoundException("Задача для удаления с id " + id + " не обнаружена");
        }
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(generateId());
        epic.setMySubtasksIdList(new ArrayList<>());
        epics.put(epic.getId(), epic);
        determineEpicParameters(generatedId);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            determineEpicParameters(generatedId);
        } else {
            throw new NotFoundException("Эпик для обновления с id " + epic.getId() + " не обнаружен");
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
            prioritizedTasksSet.remove(subtask);
        }
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.add(epics.get(id));
            return epics.get(id);
        } else {
            throw new NotFoundException("Эпик с id " + id + " не обнаружен");
        }
    }

    @Override
    public void deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            List<Integer> mySubtaskIdList = getEpicById(id).getMySubtasksIdList();
            for (Integer subtaskId : mySubtaskIdList) {
                historyManager.remove(subtaskId);
                prioritizedTasksSet.remove(subtasks.get(subtaskId));
                subtasks.remove(subtaskId);
            }
            historyManager.remove(id);
            epics.remove(id);
        } else {
            throw new NotFoundException("Эпик для удаления с id " + id + " не обнаружен");
        }
    }

    @Override
    public int createSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getMyEpicId())) {
            System.out.println("Новая подзадача не может быть создана, нет такого эпика");
            throw new NotFoundException("Новая подзадача не может быть создана, нет такого эпика");
        }
        if (subtask.getStartTime() != null && subtask.getEndTime() != null) {
            if (getPrioritizedTasks().stream()
                    .noneMatch(t -> isTimeCross(t, subtask))) {
                prioritizedTasksSet.add(subtask);
            } else {
                System.out.println("Новая подзадача не может быть создана, время пересекается");
                throw new IntersectionException("Новая подзадача не может быть создана, время пересекается");
            }
        }
        subtask.setId(generateId());
        int subtaskId = subtask.getId();
        subtasks.put(subtaskId, subtask);
        Epic myEpic = epics.get(subtask.getMyEpicId());
        myEpic.getMySubtasksIdList().add(subtaskId);
        determineEpicParameters(subtask.getMyEpicId());
        return subtaskId;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new NotFoundException("Передана пустая подзадача"); // эта проверка лишняя? такого не может произойти?
            // Или наоборот надо добавить подобные проверки в другие методы тоже?
        }
        if (subtasks.containsKey(subtask.getId())) {
            if (!epics.containsKey(subtask.getMyEpicId())) {
                System.out.println("Подзадача c ID " + subtask.getId() + " не может быть обновлена, нет такого эпика");
                throw new NotFoundException("Подзадача c ID " + subtask.getId() + " не может быть обновлена, нет такого эпика");
            }
            Subtask oldSubtask = subtasks.get(subtask.getId());
            if (subtask.getStartTime() != null && subtask.getEndTime() != null) {
                if (getPrioritizedTasks().stream()
                        .filter(t -> t.getId() != subtask.getId())
                        .noneMatch(t -> isTimeCross(t, subtask))) {
                    prioritizedTasksSet.remove(oldSubtask);
                    prioritizedTasksSet.add(subtask);
                } else {
                    System.out.println("Подзадача c ID " + subtask.getId() + " не может быть обновлена, время пересекается");
                    throw new IntersectionException("Подзадача c ID " + subtask.getId() + " не может быть обновлена, время пересекается");
                }
            } else {
                prioritizedTasksSet.remove(oldSubtask);
            }
            subtasks.put(subtask.getId(), subtask);
            determineEpicParameters(subtask.getMyEpicId());
        } else {
        throw new NotFoundException("Подзадача для обновления с id " + subtask.getId() + " не обнаружена");
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
            prioritizedTasksSet.remove(subtask);
        }
        subtasks.clear();
        for (Integer id : epics.keySet()) {
            epics.get(id).getMySubtasksIdList().clear();
            determineEpicParameters(id);
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.add(subtasks.get(id));
            return subtasks.get(id);
        } else {
            throw new NotFoundException("Подзадача с id " + id + " не обнаружена");
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = getSubtaskById(id);
            prioritizedTasksSet.remove(subtask);
            if (subtasks.containsKey(id)) {
                epics.get(subtasks.get(id).getMyEpicId()).getMySubtasksIdList().remove(Integer.valueOf(id));
                determineEpicParameters(subtasks.get(id).getMyEpicId());
            }
            historyManager.remove(id);
            subtasks.remove(id);
        } else {
            throw new NotFoundException("Подзадача для обновления с id " + id + " не обнаружена");
        }
    }

    @Override
    public List<Subtask> getMySubtasksListByEpicId(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id).getMySubtasksIdList().stream()
                    .map(subtasks::get)
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException("Эпик с id " + id + "не обнаружен");
        }
    }
}
