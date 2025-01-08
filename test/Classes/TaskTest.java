package Classes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private static Task task1;
    private static Task task2;

    @BeforeEach
    public void init() {
        task1 = new Task(1,"Сделать уборку","Помыть пол",TaskStatus.NEW);
        task2 = new Task(1,"Пойти в магазин","Купить молоко",TaskStatus.IN_PROGRESS);

    }


    @Test
    void testEquals() {
        boolean real = task1.equals(task2);
        Assertions.assertTrue(real);
    }


    @Test
    void getId() {
        int expected = 1;
        int real = task1.getId();
        Assertions.assertEquals(expected,real);
    }

    @Test
    void setTaskStatus() {
    TaskStatus expected = TaskStatus.IN_PROGRESS;
    task1.setTaskStatus(TaskStatus.IN_PROGRESS);
    TaskStatus real =task1.getTaskStatus();
    Assertions.assertEquals(expected,real);
    }
}