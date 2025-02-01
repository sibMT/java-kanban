package classes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class EpicTest {
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    public void init() {
        epic1 = new Epic(1, "Ужин", "Приготовить ужин", TaskStatus.NEW);
        epic2 = new Epic("Потренероваться", "Сходить в зал");
        subtask1 = new Subtask(1, 1, "Готовка", "Стирка", TaskStatus.NEW);
        subtask2 = new Subtask(2, 1, "Сделать уроки", "Отдохнуть", TaskStatus.NEW);
    }


    @Test
    void createSubtaskId() {
        List<Integer> expected = List.of(1, 2);
        epic1.createSubtaskId(subtask1);
        epic1.createSubtaskId(subtask2);
        List<Integer> real = epic1.getSubtasks();
        Assertions.assertEquals(expected, real);
    }

    @Test
    void getSubtasks() {
        List<Integer> expected = List.of(1, 2);
        epic1.createSubtaskId(subtask1);
        epic1.createSubtaskId(subtask2);
        List<Integer> real = epic1.getSubtasks();
        Assertions.assertEquals(expected, real);
    }

    @Test
    void removeSubtaskId() {
        List<Integer> expected = List.of(1);
        epic2.createSubtaskId(subtask1);
        epic2.createSubtaskId(subtask2);
        epic2.removeSubtaskId(2);
        List<Integer> real = epic2.getSubtasks();
        Assertions.assertEquals(expected, real);
    }

    @Test
    void clearSubtasks() {
        List<Integer> expected = List.of();
        epic1.createSubtaskId(subtask1);
        epic1.createSubtaskId(subtask2);
        epic1.clearSubtasks();
        List<Integer> real = epic1.getSubtasks();
        Assertions.assertEquals(expected, real);
    }
}