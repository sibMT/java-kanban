package controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import classes.Epic;
import classes.Subtask;
import classes.Task;
import classes.TaskStatus;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;
    private Subtask subtask3;


    @BeforeEach
    void init() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task(1, "Сделать уборку", "Помыть пол", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(5, 10)));
        task2 = new Task(2, "Пойти в магазин", "Купить молоко", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(6, 10)));
        epic1 = new Epic(3, "Встретиться с друзьями", "Забронировать кафе", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(7, 10)));
        epic2 = new Epic(4, "Пойти на работу", "Проснуться в 6 утра", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(8, 10)));
        subtask1 = new Subtask(5, 3, "Определиться со временем", "Уведомить", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(9, 10)));
        subtask2 = new Subtask(6, 3, "Notification", "Meeting 10oclock", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(10, 10)));
        subtask3 = new Subtask(7, 4, "Поставить будильник", "Charge Phone", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(11, 10)));
    }

    @Test
    void addToHistory() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(epic1);
        historyManager.addToHistory(epic2);
        historyManager.addToHistory(subtask1);
        historyManager.addToHistory(subtask2);
        historyManager.addToHistory(subtask3);

        String expected = "[Classes.Taskid=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW," +
                " duration=10, startTime=05:10:00/08.03.2025, endTime=05:20:00/08.03.2025}, Classes.Taskid=2," +
                " taskName='Пойти в магазин', description='Купить молоко', taskStatus=IN_PROGRESS, duration=10," +
                " startTime=06:10:00/08.03.2025, endTime=06:20:00/08.03.2025}, Epic{id=3, name=Встретиться с друзьями," +
                " subTasksIdList=[], status=NEW, duration=10, startTime=07:10:00/08.03.2025, endTime=07:20:00/08.03.2025}," +
                " Epic{id=4, name=Пойти на работу, subTasksIdList=[], status=NEW, duration=10," +
                " startTime=08:10:00/08.03.2025, endTime=08:20:00/08.03.2025}, Classes.Subtask{id=5," +
                " name=Определиться со временем, status=NEW, duration=10, startTime=09:10:00/08.03.2025," +
                " endTime=09:20:00/08.03.2025}, Classes.Subtask{id=6, name=Notification, status=NEW, duration=10," +
                " startTime=10:10:00/08.03.2025, endTime=10:20:00/08.03.2025}, Classes.Subtask{id=7," +
                " name=Поставить будильник, status=NEW, duration=10, startTime=11:10:00/08.03.2025," +
                " endTime=11:20:00/08.03.2025}]";
        String real = historyManager.getHistory().toString();
        Assertions.assertEquals(expected, real);
    }

    @Test
    void getHistory() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(epic1);
        historyManager.addToHistory(epic2);
        historyManager.addToHistory(subtask1);
        historyManager.addToHistory(subtask2);
        historyManager.addToHistory(subtask3);

        String expected = "[Classes.Taskid=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW, " +
                "duration=10, startTime=05:10:00/08.03.2025, endTime=05:20:00/08.03.2025}, Classes.Taskid=2," +
                " taskName='Пойти в магазин', description='Купить молоко', taskStatus=IN_PROGRESS, duration=10," +
                " startTime=06:10:00/08.03.2025, endTime=06:20:00/08.03.2025}, Epic{id=3, name=Встретиться с друзьями," +
                " subTasksIdList=[], status=NEW, duration=10, startTime=07:10:00/08.03.2025, endTime=07:20:00/08.03.2025}," +
                " Epic{id=4, name=Пойти на работу, subTasksIdList=[], status=NEW, duration=10, startTime=08:10:00/08.03.2025," +
                " endTime=08:20:00/08.03.2025}, Classes.Subtask{id=5, name=Определиться со временем, status=NEW," +
                " duration=10, startTime=09:10:00/08.03.2025, endTime=09:20:00/08.03.2025}, Classes.Subtask{id=6," +
                " name=Notification, status=NEW, duration=10, startTime=10:10:00/08.03.2025, endTime=10:20:00/08.03.2025}," +
                " Classes.Subtask{id=7, name=Поставить будильник, status=NEW, duration=10, startTime=11:10:00/08.03.2025," +
                " endTime=11:20:00/08.03.2025}]";
        String real = historyManager.getHistory().toString();
        Assertions.assertEquals(expected, real);
    }

    @Test
    void sizeShouldNotMoreSevenElements() {
        final int historySize = 7;
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(epic1);
        historyManager.addToHistory(epic2);
        historyManager.addToHistory(subtask1);
        historyManager.addToHistory(subtask2);
        historyManager.addToHistory(subtask3);
        historyManager.addToHistory(subtask3);
        historyManager.addToHistory(subtask2);
        historyManager.addToHistory(task1);
        Assertions.assertEquals(historySize, historyManager.getHistory().size());
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(epic1);
        Assertions.assertEquals(historySize, historyManager.getHistory().size());
    }

    @Test
    void removeTaskById() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(epic1);
        historyManager.addToHistory(epic2);
        historyManager.addToHistory(subtask1);
        historyManager.addToHistory(subtask2);
        historyManager.addToHistory(subtask3);
        historyManager.remove(2);
        historyManager.remove(4);

        String expected = "[Classes.Taskid=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW," +
                " duration=10, startTime=05:10:00/08.03.2025, endTime=05:20:00/08.03.2025}, Epic{id=3," +
                " name=Встретиться с друзьями, subTasksIdList=[], status=NEW, duration=10, startTime=07:10:00/08.03.2025," +
                " endTime=07:20:00/08.03.2025}, Classes.Subtask{id=5, name=Определиться со временем, status=NEW," +
                " duration=10, startTime=09:10:00/08.03.2025, endTime=09:20:00/08.03.2025}, Classes.Subtask{id=6," +
                " name=Notification, status=NEW, duration=10, startTime=10:10:00/08.03.2025," +
                " endTime=10:20:00/08.03.2025}, Classes.Subtask{id=7, name=Поставить будильник, status=NEW," +
                " duration=10, startTime=11:10:00/08.03.2025, endTime=11:20:00/08.03.2025}]";
        String real = historyManager.getHistory().toString();
        Assertions.assertEquals(expected, real);
    }
}