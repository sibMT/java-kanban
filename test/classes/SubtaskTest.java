package classes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

class SubtaskTest {
    private Subtask subtask;

    @BeforeEach
    public void init() {
        subtask = new Subtask(1, 1, "Сделать уборку", "Помыть", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(LocalDate.of(2025, 3, 8),
                LocalTime.of(1, 10)));
    }

    @Test
    void getEpicId() {
        int expected = 1;
        int real = subtask.getEpicId();
        Assertions.assertEquals(expected, real);
    }

    @Test
    void setEpicId() {
        int expected = 1;
        subtask.setEpicId(1);
        int real = subtask.getEpicId();
        Assertions.assertEquals(expected, real);
    }
}