package controllers;

import classes.Task;
import classes.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void init() {
        historyManager = new InMemoryHistoryManager(); // Используем конкретную реализацию

        task1 = new Task(1, "Сделать уборку", "Помыть пол", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(5, 10)));
        task2 = new Task(2, "Пойти в магазин", "Купить молоко", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(6, 10)));
        task3 = new Task(3, "Погулять", "Купить", TaskStatus.DONE,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(3, 10)));
    }

    @Test
    void addToHistory() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи");
        assertTrue(history.contains(task1), "История должна содержать task1");
        assertTrue(history.contains(task2), "История должна содержать task2");
    }

    @Test
    void getHistoryEmpty() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой");
    }

    @Test
    void addToHistoryWithDuplicates() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна быть без дубликатов");
        assertTrue(history.contains(task1), "История должна содержать task1");
    }

    @Test
    void removeHistoryBeginning() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи");
        assertFalse(history.contains(task1), "История не должна содержать task1");
        assertTrue(history.contains(task2), "История должна содержать task2");
        assertTrue(history.contains(task3), "История должна содержать task3");
    }

    @Test
    void removeHistoryMiddle() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи");
        assertTrue(history.contains(task1), "История должна содержать task1");
        assertFalse(history.contains(task2), "История не должна содержать task2");
        assertTrue(history.contains(task3), "История должна содержать task3");
    }

    @Test
    void removeHistoryEnd() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);
        historyManager.addToHistory(task3);

        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна содержать две задачи");
        assertTrue(history.contains(task1), "История должна содержать task1");
        assertTrue(history.contains(task2), "История должна содержать task2");
        assertFalse(history.contains(task3), "История не должна содержать task3");
    }

    @Test
    void removeNonExistentTask() {
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);

        historyManager.remove(100);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "История должна остаться неизменной");
        assertTrue(history.contains(task1), "История должна содержать task1");
        assertTrue(history.contains(task2), "История должна содержать task2");
    }
}