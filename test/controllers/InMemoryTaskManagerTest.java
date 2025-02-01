package controllers;

import classes.Epic;
import classes.Subtask;
import classes.Task;
import classes.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;


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

        task1 = new Task("Сделать уборку","Помыть пол",TaskStatus.NEW);
        task2 = new Task("Пойти в магазин", "Купить молоко", TaskStatus.IN_PROGRESS);
        epic1 = new Epic("Встретиться с друзьями", "Забронировать кафе");
        epic2 = new Epic("Пойти на работу", "Проснуться в 6 утра");

    }

    @org.junit.jupiter.api.Test
    void createTask() {
        taskManager.createTask(task1);
        String expected = "[Classes.Taskid=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW}]";
        String real = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected,real);
    }

    @org.junit.jupiter.api.Test
    void createEpic() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        String expected = "[Classes.Taskid=1, taskName='Встретиться с друзьями', description='Забронировать кафе'," +
                " taskStatus=NEW}, Classes.Taskid=2, taskName='Пойти на работу', description='Проснуться в 6 утра'," +
                " taskStatus=NEW}]";
        String real = taskManager.getAllEpics().toString();
        Assertions.assertEquals(expected,real);
    }

    @org.junit.jupiter.api.Test
    void createSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        subtask1 = new Subtask(epic1,"Приготовить","Помыть",TaskStatus.NEW);
        taskManager.createSubtasks(subtask1);
        String expected = "[Classes.Subtask{id=3, name=Приготовить, status=NEW}]";
        String real = taskManager.getAllSubtasks().toString();
        Assertions.assertEquals(expected,real);
    }

    @org.junit.jupiter.api.Test
    void updateTask() {
        taskManager.createTask(task1);
        String expected = "Classes.Taskid=1, taskName='Пойти на работу', description='Проснуться в 6 утра'," +
                " taskStatus=IN_PROGRESS}";
        Task task3 = new Task(1,"Пойти на работу", "Проснуться в 6 утра",TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task3);
        String real = taskManager.getTaskById(1).toString();
        Assertions.assertEquals(expected,real);
    }

    @org.junit.jupiter.api.Test
    void updateEpic() {
        taskManager.createEpic(epic1);
        Epic epic3 = taskManager.getEpicById(1);
        epic1.setTaskName("Home");
        taskManager.updateEpic(epic3);
        String expected = "[Classes.Taskid=1, taskName='Home', description='Забронировать кафе', taskStatus=NEW}]";
        String real = taskManager.getAllEpics().toString();

        Assertions.assertEquals(expected,real);
    }

    @org.junit.jupiter.api.Test
    void updateSubtask() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        subtask1 = new Subtask(epic1,"Определиться со временем", "Уведомить",TaskStatus.NEW);
        Subtask subtask2 = new Subtask(epic1,"Notification", "Meeting 10oclock", TaskStatus.NEW);
        Subtask subtask3 = new Subtask(epic2, "Поставить будильник", "Charge Phone", TaskStatus.NEW);

        taskManager.createSubtasks(subtask1);
        taskManager.createSubtasks(subtask2);
        taskManager.createSubtasks(subtask3);

        subtask1 = taskManager.getSubtaskById(3);
        subtask1.setTaskName("New Name");
        taskManager.updateSubtask(subtask1);
        String expected = "[Classes.Subtask{id=3, name=New Name, status=NEW}, Classes.Subtask{id=4, name=Notification," +
                " status=NEW}, Classes.Subtask{id=5, name=Поставить будильник, status=NEW}]";
        String real = taskManager.getAllSubtasks().toString();

        Assertions.assertEquals(expected,real);
    }

    @org.junit.jupiter.api.Test
    void getAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        String expected = "[Classes.Taskid=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW}," +
                " Classes.Taskid=2, taskName='Пойти в магазин', description='Купить молоко', taskStatus=IN_PROGRESS}]";
        String real = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected,real);
    }

    @org.junit.jupiter.api.Test
    void getAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        String expected = "[Classes.Taskid=1, taskName='Встретиться с друзьями', description='Забронировать кафе'" +
                ", taskStatus=NEW}, Classes.Taskid=2, taskName='Пойти на работу', description='Проснуться в 6 утра'" +
                ", taskStatus=NEW}]";
        String real = taskManager.getAllEpics().toString();

        Assertions.assertEquals(expected,real);
    }

    @org.junit.jupiter.api.Test
    void getAllSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        subtask1 = new Subtask(epic1,"Определиться со временем", "Уведомить",TaskStatus.NEW);
        Subtask subtask2 = new Subtask(epic1,"Notification", "Meeting 10oclock", TaskStatus.NEW);
        Subtask subtask3 = new Subtask(epic2, "Поставить будильник", "Charge Phone", TaskStatus.NEW);

        taskManager.createSubtasks(subtask1);
        taskManager.createSubtasks(subtask2);
        taskManager.createSubtasks(subtask3);
        String expected = "[Classes.Subtask{id=3, name=Определиться со временем, status=NEW}, Classes.Subtask{id=4," +
                " name=Notification, status=NEW}, Classes.Subtask{id=5, name=Поставить будильник, status=NEW}]";
        String real = taskManager.getAllSubtasks().toString();

        Assertions.assertEquals(expected,real);
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
        subtask1 = new Subtask(epic1, "Определиться со временем", "Уведомить",TaskStatus.NEW);
        Subtask subtask2 = new Subtask(epic1,"Notification", "Meeting 10oclock", TaskStatus.NEW);
        Subtask subtask3 = new Subtask(epic2, "Поставить будильник", "Charge Phone", TaskStatus.NEW);
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
        String expected = "[Classes.Taskid=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW}]";
        String real = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected,real);
    }

    @org.junit.jupiter.api.Test
    void removeEpicById() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.removeEpicById(1);

        String expected = "[Classes.Taskid=2, taskName='Пойти на работу', description='Проснуться в 6 утра'," +
                " taskStatus=NEW}]";
        String real = taskManager.getAllEpics().toString();

        Assertions.assertEquals(expected,real);
    }

    @org.junit.jupiter.api.Test
    void removeSubtaskById() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        subtask1 = new Subtask(epic1, "Определиться со временем", "Уведомить",TaskStatus.NEW);
        Subtask subtask2 = new Subtask(epic1,"Notification", "Meeting 10oclock", TaskStatus.NEW);
        Subtask subtask3 = new Subtask(epic2, "Поставить будильник", "Charge Phone", TaskStatus.NEW);

        taskManager.createSubtasks(subtask1);
        taskManager.createSubtasks(subtask2);
        taskManager.createSubtasks(subtask3);
        taskManager.removeSubtaskById(5);

        String expected = "[Classes.Subtask{id=3, name=Определиться со временем, status=NEW}, Classes.Subtask{id=4," +
                " name=Notification, status=NEW}]";
        String real = taskManager.getAllSubtasks().toString();

        Assertions.assertEquals(expected,real);

    }

    @org.junit.jupiter.api.Test
    void getEpicById() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        String expected = "Classes.Taskid=2, taskName='Пойти на работу', description='Проснуться в 6 утра'," +
                " taskStatus=NEW}";
        String real = taskManager.getEpicById(2).toString();

        Assertions.assertEquals(expected,real);
    }

    @org.junit.jupiter.api.Test
    void getTaskById() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        String expected = "Classes.Taskid=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW}";
        String real = taskManager.getTaskById(1).toString();
        Assertions.assertEquals(expected,real);
    }

    @org.junit.jupiter.api.Test
    void getHistory() {
        int historySize = 6;
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubtasks(subtask1);
        Subtask subtask2 = new Subtask(6,3,"Notification", "Meeting 10oclock", TaskStatus.NEW);
        Subtask subtask3 = new Subtask(7, 4,"Поставить будильник", "Charge Phone", TaskStatus.NEW);
        taskManager.createSubtasks(subtask2);
        taskManager.createSubtasks(subtask3);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getEpicById(4);
        taskManager.getSubtaskById(5);
        taskManager.getSubtaskById(6);

        Assertions.assertEquals(historySize,taskManager.getHistory().size());
        String expected = "[Classes.Taskid=1, taskName='Сделать уборку', description='Помыть пол', taskStatus=NEW}," +
                " Classes.Taskid=2, taskName='Пойти в магазин', description='Купить молоко', taskStatus=IN_PROGRESS}," +
                " Classes.Taskid=3, taskName='Встретиться с друзьями', description='Забронировать кафе', taskStatus=NEW}, " +
                "Classes.Taskid=4, taskName='Пойти на работу', description='Проснуться в 6 утра', taskStatus=NEW}," +
                " Classes.Subtask{id=5, name=Notification, status=NEW}, Classes.Subtask{id=6, name=Поставить будильник," +
                " status=NEW}]";
        String real = taskManager.getHistory().toString();
        Assertions.assertEquals(expected,real);
    }

    @org.junit.jupiter.api.Test
    void getSubtaskById() {
        taskManager.createEpic(epic1);
        subtask1 = new Subtask(epic1, "Определиться со временем", "Уведомить",TaskStatus.NEW);
        taskManager.createSubtasks(subtask1);
        String expected = "Classes.Subtask{id=2, name=Определиться со временем, status=NEW}";
        String real = taskManager.getSubtaskById(2).toString();
        Assertions.assertEquals(expected,real);
    }
}