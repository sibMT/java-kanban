package controllers;

import classes.Epic;
import classes.Subtask;
import classes.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    Subtask createSubtasks(Subtask subtask);

    Task updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<Subtask> getAllSubtasks();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    Task removeTaskById(Integer id);

    void removeEpicById(Integer id);

    void removeSubtaskById(Integer id);

    Epic getEpicById(Integer id);

    Task getTaskById(Integer id);

    List<Task> getHistory();

    Subtask getSubtaskById(Integer id);
}
