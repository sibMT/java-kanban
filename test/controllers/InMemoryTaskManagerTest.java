package controllers;

import classes.Epic;
import classes.Subtask;
import classes.Task;
import classes.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


class InMemoryTaskManagerTest {

    private TaskManager taskManager;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;


    @BeforeEach
    public void init() {
        taskManager = Managers.getDefault();

        task1 = new Task("Сделать уборку", "Помыть пол", TaskStatus.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(LocalDate.of(2025, 3, 8),
                        LocalTime.of(1, 10)));
        task2 = new Task("Пойти в магазин", "Купить молоко", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(3, 10)));
        epic1 = new Epic(3, "Встретиться с друзьями", "Забронировать кафе", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(5, 10)));
        epic2 = new Epic(4, "Пойти на работу", "Проснуться в 6 утра", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(7, 0)));

    }

    @org.junit.jupiter.api.Test
    void createTask() {
        taskManager.createTask(task1);
        String expected = "[Task{id=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW, " +
                "duration=10, startTime=01:10:00/08.03.2025, endTime=01:20:00/08.03.2025}]";
        String real = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, real);
    }

    @org.junit.jupiter.api.Test
    void createEpic() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        String expected = "[Epic{id=1, name=Встретиться с друзьями, subTasksIdList=[], status=NEW, duration=10," +
                " startTime=05:10:00/08.03.2025, endTime=05:20:00/08.03.2025}, Epic{id=2, name=Пойти на работу," +
                " subTasksIdList=[], status=NEW, duration=10, startTime=07:00:00/08.03.2025, endTime=07:10:00/08.03.2025}]";
        String real = taskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, real);
    }

    @org.junit.jupiter.api.Test
    void createSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        subtask1 = new Subtask(epic1, "Приготовить", "Помыть", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(14, 0)));
        taskManager.createSubtasks(subtask1);
        String expected = "[Classes.Subtask{id=3, name=Приготовить, status=NEW, duration=10, startTime=14:00:00/08.03.2025, endTime=14:10:00/08.03.2025}]";
        String real = taskManager.getAllSubtasks().toString();
        Assertions.assertEquals(expected, real);
    }

    @org.junit.jupiter.api.Test
    void updateTask() {
        taskManager.createTask(task1);
        String expected = "Task{id=1, taskName='Пойти на работу', description='Проснуться в 6 утра', taskStatus=IN_PROGRESS, " +
                "duration=10, startTime=01:10:00/08.03.2025, endTime=01:20:00/08.03.2025}";
        Task task3 = new Task(1, "Пойти на работу", "Проснуться в 6 утра", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(15, 10)));
        taskManager.updateTask(task3);
        String real = taskManager.getTaskById(1).toString();
        Assertions.assertEquals(expected, real);
    }

    @org.junit.jupiter.api.Test
    void updateEpic() {
        taskManager.createEpic(epic1);
        Epic epic3 = taskManager.getEpicById(1);
        epic1.setTaskName("Home");
        taskManager.updateEpic(epic3);
        String expected = "[Epic{id=1, name=Home, subTasksIdList=[], status=NEW, duration=10," +
                " startTime=05:10:00/08.03.2025, endTime=05:20:00/08.03.2025}]";
        String real = taskManager.getAllEpics().toString();

        Assertions.assertEquals(expected, real);
    }

    @org.junit.jupiter.api.Test
    void updateSubtask() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        subtask1 = new Subtask(epic1, "Определиться со временем", "Уведомить", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(13, 10)));
        Subtask subtask2 = new Subtask(epic1, "Notification", "Meeting 10oclock", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(11, 10)));
        Subtask subtask3 = new Subtask(epic2, "Поставить будильник", "Charge Phone",
                TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(7, 10)));

        taskManager.createSubtasks(subtask1);
        taskManager.createSubtasks(subtask2);
        taskManager.createSubtasks(subtask3);

        subtask1 = taskManager.getSubtaskById(3);
        subtask1.setTaskName("New Name");
        taskManager.updateSubtask(subtask1);
        String expected = "[Classes.Subtask{id=3, name=New Name, status=NEW, duration=10, startTime=13:10:00/08.03.2025," +
                " endTime=13:20:00/08.03.2025}, Classes.Subtask{id=4, name=Notification, status=NEW, duration=10," +
                " startTime=11:10:00/08.03.2025, endTime=11:20:00/08.03.2025}," +
                " Classes.Subtask{id=5, name=Поставить будильник, status=NEW, duration=10, startTime=07:10:00/08.03.2025, " +
                "endTime=07:20:00/08.03.2025}]";
        String real = taskManager.getAllSubtasks().toString();

        Assertions.assertEquals(expected, real);
    }

    @org.junit.jupiter.api.Test
    void getAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        String expected = "[Task{id=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW," +
                " duration=10, startTime=01:10:00/08.03.2025, endTime=01:20:00/08.03.2025}, Task{id=2, " +
                "taskName='Пойти в магазин', description='Купить молоко', taskStatus=IN_PROGRESS, duration=10, " +
                "startTime=03:10:00/08.03.2025, endTime=03:20:00/08.03.2025}]";
        String real = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, real);
    }

    @org.junit.jupiter.api.Test
    void getAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        String expected = "[Epic{id=1, name=Встретиться с друзьями, subTasksIdList=[], status=NEW, duration=10," +
                " startTime=05:10:00/08.03.2025, endTime=05:20:00/08.03.2025}, Epic{id=2, name=Пойти на работу," +
                " subTasksIdList=[], status=NEW, duration=10, startTime=07:00:00/08.03.2025, endTime=07:10:00/08.03.2025}]";
        String real = taskManager.getAllEpics().toString();

        Assertions.assertEquals(expected, real);
    }

    @org.junit.jupiter.api.Test
    void getAllSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        subtask1 = new Subtask(epic1, "Определиться со временем", "Уведомить", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(9, 10)));
        Subtask subtask2 = new Subtask(epic1, "Notification", "Meeting 10oclock", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(10, 10)));
        Subtask subtask3 = new Subtask(epic2, "Поставить будильник", "Charge Phone", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(12, 10)));

        taskManager.createSubtasks(subtask1);
        taskManager.createSubtasks(subtask2);
        taskManager.createSubtasks(subtask3);
        String expected = "[Classes.Subtask{id=3, name=Определиться со временем, status=NEW, duration=10, startTime=09:10:00/08.03.2025, endTime=09:20:00/08.03.2025}, Classes.Subtask{id=4, name=Notification, status=NEW, duration=10, startTime=10:10:00/08.03.2025, endTime=10:20:00/08.03.2025}, Classes.Subtask{id=5, name=Поставить будильник, status=NEW, duration=10, startTime=12:10:00/08.03.2025, endTime=12:20:00/08.03.2025}]";
        String real = taskManager.getAllSubtasks().toString();

        Assertions.assertEquals(expected, real);
    }

    @org.junit.jupiter.api.Test
    void removeAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.removeAllTasks();

        Assertions.assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @org.junit.jupiter.api.Test
    void removeAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.removeAllEpics();

        Assertions.assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @org.junit.jupiter.api.Test
    void removeAllSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        subtask1 = new Subtask(epic1, "Определиться со временем", "Уведомить", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(6, 10)));
        Subtask subtask2 = new Subtask(epic1, "Notification", "Meeting 10oclock", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(7, 10)));
        Subtask subtask3 = new Subtask(epic2, "Поставить будильник", "Charge Phone", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(8, 10)));
        taskManager.createSubtasks(subtask1);
        taskManager.createSubtasks(subtask2);
        taskManager.createSubtasks(subtask3);

        taskManager.removeAllSubtasks();

        Assertions.assertTrue(taskManager.getAllSubtasks().isEmpty());

    }

    @org.junit.jupiter.api.Test
    void removeTaskById() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.removeTaskById(2);
        String expected = "[Task{id=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW, " +
                "duration=10, startTime=01:10:00/08.03.2025, endTime=01:20:00/08.03.2025}]";
        String real = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, real);
    }

    @org.junit.jupiter.api.Test
    void removeEpicById() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.removeEpicById(1);

        String expected = "[Epic{id=2, name=Пойти на работу, subTasksIdList=[], status=NEW, duration=10," +
                " startTime=07:00:00/08.03.2025, endTime=07:10:00/08.03.2025}]";
        String real = taskManager.getAllEpics().toString();

        Assertions.assertEquals(expected, real);
    }

    @org.junit.jupiter.api.Test
    void removeSubtaskById() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        subtask1 = new Subtask(epic1, "Определиться со временем", "Уведомить", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(3, 10)));
        Subtask subtask2 = new Subtask(epic1, "Notification", "Meeting 10oclock", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(4, 10)));
        Subtask subtask3 = new Subtask(epic2, "Поставить будильник", "Charge Phone", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(5, 10)));

        taskManager.createSubtasks(subtask1);
        taskManager.createSubtasks(subtask2);
        taskManager.createSubtasks(subtask3);
        taskManager.removeSubtaskById(5);

        String expected = "[Classes.Subtask{id=3, name=Определиться со временем, status=NEW, duration=10, startTime=03:10:00/08.03.2025, endTime=03:20:00/08.03.2025}, Classes.Subtask{id=4, name=Notification, status=NEW, duration=10, startTime=04:10:00/08.03.2025, endTime=04:20:00/08.03.2025}]";
        String real = taskManager.getAllSubtasks().toString();

        Assertions.assertEquals(expected, real);

    }

    @org.junit.jupiter.api.Test
    void getEpicById() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        String expected = "Epic{id=2, name=Пойти на работу, subTasksIdList=[], status=NEW, duration=10," +
                " startTime=07:00:00/08.03.2025, endTime=07:10:00/08.03.2025}";
        String real = taskManager.getEpicById(2).toString();

        Assertions.assertEquals(expected, real);
    }

    @org.junit.jupiter.api.Test
    void getTaskById() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        String expected = "Task{id=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW, " +
                "duration=10, startTime=01:10:00/08.03.2025, endTime=01:20:00/08.03.2025}";
        String real = taskManager.getTaskById(1).toString();
        Assertions.assertEquals(expected, real);
    }

    @org.junit.jupiter.api.Test
    void getHistory() {
        int historySize = 6;
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtasks(subtask1);
        Subtask subtask2 = new Subtask(6, 3, "Notification", "Meeting 10oclock",
                TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(12, 10)));
        Subtask subtask3 = new Subtask(7, 4, "Поставить будильник", "Charge Phone", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(14, 10)));
        taskManager.createSubtasks(subtask2);
        taskManager.createSubtasks(subtask3);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getEpicById(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);

        Assertions.assertEquals(historySize, taskManager.getHistory().size());
        String expected = "[Classes.Subtask{id=6, name=Поставить будильник, status=NEW, duration=10," +
                " startTime=14:10:00/08.03.2025, endTime=14:20:00/08.03.2025}, Classes.Subtask{id=5, name=Notification, " +
                "status=NEW, duration=10, startTime=12:10:00/08.03.2025, endTime=12:20:00/08.03.2025}, " +
                "Epic{id=4, name=Пойти на работу, subTasksIdList=[6], status=NEW, duration=0, " +
                "startTime=14:10:00/08.03.2025, endTime=14:10:00/08.03.2025}, Epic{id=3, name=Встретиться с друзьями, " +
                "subTasksIdList=[5], status=NEW, duration=0, startTime=12:10:00/08.03.2025, endTime=12:10:00/08.03.2025}, " +
                "Task{id=2, taskName='Пойти в магазин', description='Купить молоко', taskStatus=IN_PROGRESS, " +
                "duration=10, startTime=03:10:00/08.03.2025, endTime=03:20:00/08.03.2025}, Task{id=1, " +
                "taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW, duration=10, " +
                "startTime=01:10:00/08.03.2025, endTime=01:20:00/08.03.2025}]";
        String real = taskManager.getHistory().toString();
        Assertions.assertEquals(expected, real);
    }

    @org.junit.jupiter.api.Test
    void getSubtaskById() {
        taskManager.createEpic(epic1);
        subtask1 = new Subtask(epic1, "Определиться со временем", "Уведомить", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(1, 10)));
        taskManager.createSubtasks(subtask1);
        String expected = "Classes.Subtask{id=2, name=Определиться со временем, status=NEW, duration=10, startTime=01:10:00/08.03.2025, endTime=01:20:00/08.03.2025}";
        String real = taskManager.getSubtaskById(2).toString();
        Assertions.assertEquals(expected, real);
    }

    @Test
    void prioritizedTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        String expected = "[Task{id=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW, duration=10," +
                " startTime=01:10:00/08.03.2025, endTime=01:20:00/08.03.2025}, Task{id=2, taskName='Пойти в магазин'," +
                " description='Купить молоко', taskStatus=IN_PROGRESS, duration=10, startTime=03:10:00/08.03.2025," +
                " endTime=03:20:00/08.03.2025}]";
        String real = taskManager.getPrioritizedTasks().toString();
        Assertions.assertEquals(expected, real);
    }
}