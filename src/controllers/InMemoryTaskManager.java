package controllers;

import classes.Epic;
import classes.Subtask;
import classes.Task;
import classes.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int counter = 1;
    private HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }


    @Override
    public Task createTask(Task task) {
        int newId = nextId();
        task.setId(newId);
        tasks.put(task.getId(), task);
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
    }

    @Override
    public Task removeTaskById(Integer id) {
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
        subtasks.remove(id);
        updateEpicStatus(epic);
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
        List<Subtask> subTasks = new ArrayList<>();
        for (Integer id : subTasksId) {
            subTasks.add(subTasks.get(id));
        }
        return subTasks;
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

    private void clearEpicSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
        }
    }

    private int nextId() {
        return counter++;
    }
}