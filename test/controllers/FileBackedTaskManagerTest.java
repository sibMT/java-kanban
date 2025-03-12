package controllers;

import classes.Epic;
import classes.Subtask;
import classes.Task;
import classes.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


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
        task1 = new Task(1, "Сделать уборку", "Помыть пол", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.now(), LocalTime.of(5, 10)));
        task2 = new Task(2, "Пойти в магазин", "Купить молоко", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.now(), LocalTime.of(9, 5)));
        epic1 = new Epic(3, "Встретиться с друзьями", "Забронировать кафе", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 5)));
        epic2 = new Epic(4, "Пойти на работу", "Проснуться в 6 утра", TaskStatus.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(LocalDate.now(), LocalTime.of(11, 0)));
        subtask1 = new Subtask(5, 3, "Определиться со временем", "Уведомить", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.now(), LocalTime.of(12, 0)));
        subtask2 = new Subtask(6, 3, "Notification", "Meeting 10oclock", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.now(), LocalTime.of(13, 40)));
        try {
            filePath = Files.createTempFile("data-", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        taskManager = Managers.getFileBackedTaskManager(filePath.toFile());
    }

    @Test
    void load() {
        // Создаем задачи, эпики и подзадачи
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
        String expected = "3,EPIC,Встретиться с друзьями,NEW,Забронировать кафе,10,10:05:00/12.03.2025,10:15:00/12.03.2025,[]\n";


        String real = epic1.serialize();
        Assertions.assertEquals(expected, real);
    }

    @Test
    void serializeSubtask() {
        String expected = "5,SUBTASK,Определиться со временем,NEW,Уведомить,10,12:00:00/12.03.2025,12:10:00/12.03.2025,3\n";

        String real = subtask1.serialize();
        Assertions.assertEquals(expected, real);
    }

    @Test
    void serializeTask() {
        String expected = "1,TASK,Сделать уборку,NEW,Помыть пол,10,05:10:00/12.03.2025,05:20:00/12.03.2025\n";
        String real = task1.serialize();
        Assertions.assertEquals(expected, real);
    }
}
