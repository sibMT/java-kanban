package controllers;

import classes.Epic;
import classes.Subtask;
import classes.Task;
import classes.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {


// не совсем понял зачем создавать класс абстрактным,если нельзя отсюда запускать тесты

    protected T taskManager;

    protected abstract T createManager();

    protected Task task1;
    protected Task task2;
    protected Epic epic1;
    protected Epic epic2;
    protected Subtask subtask1;
    protected Subtask subtask2;

    @BeforeEach
    void init() {
        taskManager = createManager();

        task1 = new Task("Сделать уборку", "Помыть пол", TaskStatus.NEW, Duration.ofMinutes(10),
                LocalDateTime.of(2025, 3, 8, 1, 10));
        task2 = new Task("Пойти в магазин", "Купить молоко", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(10), LocalDateTime.of(2025, 3, 8, 2, 10));
        epic1 = new Epic("Встретиться с друзьями", "Забронировать кафе");
        epic1.setDuration(Duration.ofMinutes(10));
        epic1.setStartTime(LocalDateTime.of(2025, 3, 8, 7, 10));
        epic2 = new Epic("Пойти на работу", "Проснуться в 6 утра", Duration.ofMinutes(10),
                LocalDateTime.of(2025, 3, 8, 5, 0));
        subtask1 = new Subtask(epic1, "Купить продукты", "Молоко, хлеб, яйца", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2025, 3, 8, 8, 0));
        subtask2 = new Subtask(2, epic1.getId(), "Подготовить презентацию", "Сделать слайды", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2025, 3, 8, 9, 0));
    }

    @Test
    void createTask() {
        Task createdTask = taskManager.createTask(task1);
        assertNotNull(createdTask, "Задача не должна быть null");
        assertEquals(task1, createdTask, "Созданная задача должна совпадать с исходной");
        assertEquals(1, taskManager.getAllTasks().size(), "Список должен содержать одну задачу");
    }

    @Test
    void createEpic() {
        Epic createdEpic = taskManager.createEpic(epic1);
        assertNotNull(createdEpic, "Эпик не должен быть null");
        assertEquals(epic1, createdEpic, "Созданный эпик должен совпадать с исходным");
        assertEquals(1, taskManager.getAllEpics().size(), "Список должен содержать один эпик");
    }

    @Test
    void createSubtask() {
        taskManager.createEpic(epic1);
        Subtask createdSubtask = taskManager.createSubtasks(subtask1);
        assertNotNull(createdSubtask, "Подзадача не должна быть null");
        assertEquals(subtask1, createdSubtask, "Созданная подзадача должна совпадать с исходной");
        assertEquals(1, taskManager.getAllSubtasks().size(), "Список должен содержать одну подзадачу");
    }

    @Test
    void updateTask() {
        taskManager.createTask(task1);
        Task updatedTask = new Task(task1.getId(), "Обновленная задача", "Новое описание", TaskStatus.DONE,
                Duration.ofMinutes(20), LocalDateTime.of(2025, 3, 8, 1, 10));
        taskManager.updateTask(updatedTask);

        Task retrievedTask = taskManager.getTaskById(task1.getId());
        assertEquals(updatedTask, retrievedTask, "Задача должна быть обновлена");
    }

    @Test
    void updateEpic() {
        taskManager.createEpic(epic1);
        Epic updatedEpic = new Epic(epic1.getId(), "Обновленный эпик", "Новое описание", TaskStatus.DONE,
                Duration.ofMinutes(20), LocalDateTime.of(2025, 3, 8, 7, 10));
        taskManager.updateEpic(updatedEpic);

        Epic retrievedEpic = taskManager.getEpicById(epic1.getId());
        assertEquals(updatedEpic, retrievedEpic, "Эпик должен быть обновлен");
    }

    @Test
    void updateSubtask() {
        taskManager.createEpic(epic1);
        taskManager.createSubtasks(subtask1);
        Subtask updatedSubtask = new Subtask(subtask1.getId(), epic1.getId(), "Обновленная подзадача", "Новое описание",
                TaskStatus.DONE, Duration.ofMinutes(20), LocalDateTime.of(2025, 3, 8, 8, 0));
        taskManager.updateSubtask(updatedSubtask);

        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask1.getId());
        assertEquals(updatedSubtask, retrievedSubtask, "Подзадача должна быть обновлена");
    }

    @Test
    void getAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size(), "Список должен содержать две задачи");
        assertTrue(tasks.contains(task1), "Список должен содержать task1");
        assertTrue(tasks.contains(task2), "Список должен содержать task2");
    }

    @Test
    void getAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(2, epics.size(), "Списо должен содержать два эпика");
        assertTrue(epics.contains(epic1), "Список должен содержать epic1");
        assertTrue(epics.contains(epic2), "Список должен содержать epic2");
    }

    @Test
    void getAllSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createSubtasks(subtask1);
        taskManager.createSubtasks(subtask2);
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertEquals(2, subtasks.size(), "Список должен содержать две подзадачи");
        assertTrue(subtasks.contains(subtask1), "Список должен содержать subtask1");
        assertTrue(subtasks.contains(subtask2), "Список должен содержать subtask2");
    }

    @Test
    void removeAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.removeAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач должен быть пустым");
    }

    @Test
    void removeAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.removeAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty(), "Список эпиков должен быть пустым");
    }

    @Test
    void removeAllSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createSubtasks(subtask1);
        taskManager.createSubtasks(subtask2);
        taskManager.removeAllSubtasks();
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Список подзадач должен быть пустым");
    }

    @Test
    void removeTaskById() {
        taskManager.createTask(task1);
        taskManager.removeTaskById(task1.getId());
        assertNull(taskManager.getTaskById(task1.getId()), "Задача должна быть удалена");
    }

    @Test
    void removeEpicById() {
        taskManager.createEpic(epic1);
        taskManager.removeEpicById(epic1.getId());
        assertNull(taskManager.getEpicById(epic1.getId()), "Эпик должен быть удален");
    }

    @Test
    void removeSubtaskById() {
        taskManager.createEpic(epic1);
        taskManager.createSubtasks(subtask1);
        taskManager.removeSubtaskById(subtask1.getId());
        assertNull(taskManager.getSubtaskById(subtask1.getId()), "Подзадача должна быть удалена");
    }

    @Test
    void getHistory() {
        taskManager.createTask(task1);
        taskManager.createEpic(epic1);
        taskManager.createSubtasks(subtask1);

        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epic1.getId());
        taskManager.getSubtaskById(subtask1.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "История должна содержать три задачи");
        assertTrue(history.contains(task1), "История должна содержать task1");
        assertTrue(history.contains(epic1), "История должна содержать epic1");
        assertTrue(history.contains(subtask1), "История должна содержать subtask1");
    }

    @Test
    void getPrioritizedTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createSubtasks(subtask1);

        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(3, prioritizedTasks.size(), "Список задач должен содержать три задачи");
        assertTrue(prioritizedTasks.contains(task1), "Список задач должен содержать task1");
        assertTrue(prioritizedTasks.contains(task2), "Список задач должен содержать task2");
        assertTrue(prioritizedTasks.contains(subtask1), "Список задач должен содержать subtask1");
    }
}
