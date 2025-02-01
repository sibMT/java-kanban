package Controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Classes.Epic;
import Classes.Subtask;
import Classes.Task;
import Classes.TaskStatus;
import org.junit.jupiter.api.Assertions;


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
        task1 = new Task(1,"Сделать уборку", "Помыть пол",TaskStatus.NEW);
        task2 = new Task(2,"Пойти в магазин", "Купить молоко", TaskStatus.IN_PROGRESS);
        epic1 = new Epic(3, "Встретиться с друзьями", "Забронировать кафе",TaskStatus.NEW);
        epic2 = new Epic(4,"Пойти на работу", "Проснуться в 6 утра",TaskStatus.NEW);
        subtask1 =  new Subtask(5,3, "Определиться со временем", "Уведомить",TaskStatus.NEW);
        subtask2 = new Subtask(6,3,"Notification", "Meeting 10oclock", TaskStatus.NEW);
        subtask3 = new Subtask(7,4, "Поставить будильник", "Charge Phone", TaskStatus.NEW);
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

        String expected = "[Classes.Taskid=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW}," +
                " Classes.Taskid=2, taskName='Пойти в магазин', description='Купить молоко', taskStatus=IN_PROGRESS}," +
                " Classes.Taskid=3, taskName='Встретиться с друзьями', description='Забронировать кафе', taskStatus=NEW}," +
                " Classes.Taskid=4, taskName='Пойти на работу', description='Проснуться в 6 утра', taskStatus=NEW}, " +
                "Classes.Subtask{id=5, name=Определиться со временем, status=NEW}, Classes.Subtask{id=6," +
                " name=Notification, status=NEW}, Classes.Subtask{id=7, name=Поставить будильник, status=NEW}]";
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

        String expected = "[Classes.Taskid=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW}," +
                " Classes.Taskid=2, taskName='Пойти в магазин', description='Купить молоко', taskStatus=IN_PROGRESS}," +
                " Classes.Taskid=3, taskName='Встретиться с друзьями', description='Забронировать кафе', taskStatus=NEW}," +
                " Classes.Taskid=4, taskName='Пойти на работу', description='Проснуться в 6 утра', taskStatus=NEW}," +
                " Classes.Subtask{id=5, name=Определиться со временем, status=NEW}, Classes.Subtask{id=6," +
                " name=Notification, status=NEW}, Classes.Subtask{id=7, name=Поставить будильник, status=NEW}]";
        String real = historyManager.getHistory().toString();
        Assertions.assertEquals(expected,real);
    }

    @Test
    void sizeShouldNotMoreTenElements() {
        final int historySize = 10;
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

        String expected = "[Classes.Taskid=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW}," +
                " Classes.Taskid=3, taskName='Встретиться с друзьями', description='Забронировать кафе'," +
                " taskStatus=NEW}, Classes.Subtask{id=5, name=Определиться со временем, status=NEW}," +
                " Classes.Subtask{id=6, name=Notification, status=NEW}, Classes.Subtask{id=7, name=Поставить будильник," +
                " status=NEW}]";
        String real = historyManager.getHistory().toString();
        Assertions.assertEquals(expected,real);
    }
}