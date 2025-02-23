package controllers;

import classes.Epic;
import classes.Subtask;
import classes.Task;
import classes.TaskStatus;
import exception.FileManagerSaveException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    private Path filePath;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;
    private TaskManager taskManager;

    @BeforeEach
    void init() {
        task1 = new Task(1, "Сделать уборку", "Помыть пол", TaskStatus.NEW);
        task2 = new Task(2, "Пойти в магазин", "Купить молоко", TaskStatus.IN_PROGRESS);
        epic1 = new Epic(3, "Встретиться с друзьями", "Забронировать кафе", TaskStatus.NEW);
        epic2 = new Epic(4, "Пойти на работу", "Проснуться в 6 утра", TaskStatus.NEW);
        subtask1 = new Subtask(5, 3, "Определиться со временем", "Уведомить", TaskStatus.NEW);
        subtask2 = new Subtask(6, 3, "Notification", "Meeting 10oclock", TaskStatus.NEW);
        try {
            filePath = Files.createTempFile("data-", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        taskManager = Managers.getFileBackedTaskManager(filePath.toFile());
    }

    @Test
    void load() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtasks(subtask1);
        taskManager.createSubtasks(subtask2);
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllTasks() + " " + taskManager.getAllEpics() + " " + taskManager.getAllSubtasks();
        String real = loadedTaskManager.getAllTasks() + " " + loadedTaskManager.getAllEpics() + " "
                + loadedTaskManager.getAllSubtasks();
        assertEquals(expected, real);
    }

    @Test
    void createTask() {
        taskManager.createTask(task2);
        taskManager.createTask(task1);
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllTasks().toString();
        String actually = loadedTaskManager.getAllTasks().toString();
        assertEquals(expected, actually);
    }

    @Test
    void serializeEpic() {
        String expected = "3,EPIC,Встретиться с друзьями,NEW,Забронировать кафе,[]\n";
        String real = epic1.serialize();
        Assertions.assertEquals(expected, real);
    }

    @Test
    void serializeSubtask() {
        String expected = "5,SUBTASK,Определиться со временем,NEW,Уведомить,3\n";
        String real = subtask1.serialize();
        Assertions.assertEquals(expected, real);
    }

    @Test
    void serializeTask() {
        String expected = "1,TASK,Сделать уборку,NEW,Помыть пол\n";
        String real = task1.serialize();
        Assertions.assertEquals(expected, real);
    }
}
