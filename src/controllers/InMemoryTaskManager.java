package controllers;

import classes.Epic;
import classes.Subtask;
import classes.Task;
import classes.TaskStatus;
import exception.InvalidTaskTimeException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int counter = 1;
    private HistoryManager historyManager;
    private Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }


    @Override
    public Task createTask(Task task) {
        int newId = nextId();
        task.setId(newId);
        try {
            validateTask(task);
        } catch (InvalidTaskTimeException e) {
            System.out.println(e.getMessage());
        }
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Integer newId = nextId();
        epic.setId(newId);
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtasks(Subtask subtask) {
        if (subtask == null) {
            return null;
        }
        subtask.setId(nextId());
        Epic epic = epics.get(subtask.getEpicId());
        if (epic == null) {
            return null;
        }
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epics.get(subtask.getEpicId()));
        updateEpicTime(epic);
        prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public Task updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            Task existingTask = tasks.get(task.getId());
            existingTask.setTaskName(task.getTaskName());
            existingTask.setDescription(task.getDescription());
            existingTask.setTaskStatus(task.getTaskStatus());
        }
        return task;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicStatus(epic);
                updateEpicTime(epics.get(subtask.getEpicId()));
                prioritizedTasks.add(subtask);
            }
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTasks() {
        for (Integer taskId : tasks.keySet()) {
            historyManager.remove(taskId);
        }
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (Integer epicId : epics.keySet()) {
            historyManager.remove(epicId);
        }
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        epics.values().forEach(prioritizedTasks::remove);
        epics.clear();
        subtasks.clear();

    }

    @Override
    public void removeAllSubtasks() {
        for (Integer subtaskId : subtasks.keySet()) {
            historyManager.remove(subtaskId);
        }
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }

        subtasks.clear();
        epics.values().forEach(epic -> {
            updateEpicStatus(epic);
            epic.setStartTime(LocalDateTime.of(LocalDate.now(), LocalTime.now()));
            epic.setEndTime(LocalDateTime.of(LocalDate.now(), LocalTime.now()));
            epic.setDuration(Duration.ofMinutes(0));
            prioritizedTasks.remove(epic);
        });
    }

    @Override
    public Task removeTaskById(Integer id) {
        prioritizedTasks.remove(tasks.get(id));
        historyManager.remove(id);
        return tasks.remove(id);
    }

    @Override
    public void removeEpicById(Integer id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasks()) {
                subtasks.remove(subtaskId);
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtaskById(Integer id) {
        Epic epic = epics.get(subtasks.get(id).getEpicId());
        epic.removeSubtaskId(id);
        prioritizedTasks.remove(subtasks.get(id));
        subtasks.remove(id);
        updateEpicStatus(epic);
        updateEpicTime(epic);
        historyManager.remove(id);
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = epics.get(id);
        historyManager.addToHistory(epic);
        return epic;
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = tasks.get(id);
        historyManager.addToHistory(task);
        return task;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = subtasks.get(id);
        historyManager.addToHistory(subtask);
        return subtask;
    }


    private List<Subtask> getSubtasksByEpicId(ArrayList<Integer> subTasksId) {
//        List<Subtask> subTasks = new ArrayList<>();
//        for (Integer id : subTasksId) {
//            subTasks.add(subTasks.get(id));
//        }
        return subTasksId.stream().map(subtasks::get).toList();
    }

    private void updateEpicStatus(Epic epic) {
        if (epic.getSubtasks().isEmpty()) {
            epic.setTaskStatus((TaskStatus.NEW));
            return;
        }
        boolean allTasksIsNew = true;
        boolean allTasksIsDone = true;

        List<Subtask> epicSubtasks = getSubtasksByEpicId(epic.getSubtasks());

        for (Subtask subtask : epicSubtasks) {
            if (subtask.getTaskStatus() != TaskStatus.NEW) {
                allTasksIsNew = false;
            }
            if (subtask.getTaskStatus() != TaskStatus.DONE) {
                allTasksIsDone = false;
            }
        }
        if (allTasksIsNew) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else if (allTasksIsDone) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private int nextId() {
        return counter++;
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return new LinkedHashSet<>(prioritizedTasks);
    }

    private void updateEpicTime(Epic epic) {
        LocalDateTime localDateTime = getMinimalDateTime(epic);
        long duration = calculateEpicDuration(epic.getSubtasks());
        epic.setStartTime(localDateTime);
        epic.setDuration(Duration.ofMinutes(duration));
        if (localDateTime != null) {
            epic.setEndTime(localDateTime.plus(epic.getDuration()));
        } else {
            epic.setEndTime(null);
        }
    }

    private long calculateEpicDuration(List<Integer> subTaskIds) {
        return subTaskIds.stream()
                .map(subtasks::get)
                .map(subtask -> subtask.getDuration().toMinutes())
                .reduce(0L, Long::sum);
    }

    private LocalDateTime getMinimalDateTime(Epic epic) {
        return epic.getSubtasks().stream()
                .map(subtasks::get)
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private void validateTask(Task task) throws InvalidTaskTimeException {
        List<Integer> collected = prioritizedTasks.stream()
                .filter(t -> t.getId() != task.getId())
                .filter(t -> ((t.getStartTime().isBefore(task.getStartTime()) && (t.getEndTime().isAfter(task.getStartTime())))) ||
                        (t.getStartTime().isBefore(task.getEndTime()) && (t.getEndTime().isAfter(task.getEndTime()))) ||
                        (t.getStartTime().isBefore(task.getStartTime()) && (t.getEndTime().isAfter(task.getEndTime()))) ||
                        (t.getStartTime().isAfter(task.getStartTime()) && (t.getEndTime().isBefore(task.getEndTime()))) ||
                        (t.getStartTime().equals(task.getStartTime())))
                .map(Task::getId)
                .toList();

        if (!collected.isEmpty()) {
            throw new InvalidTaskTimeException("Задача с id=" + task.getId() + " пересекается с задачами id=" + collected);
        }
    }

}