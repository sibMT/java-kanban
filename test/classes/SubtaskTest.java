package classes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubtaskTest {
     private Subtask subtask;

     @BeforeEach
     public void init() {
         subtask = new Subtask(1,1,"Сделать уборку","Помыть", TaskStatus.NEW);
     }

    @Test
    void getEpicId() {
         int expected = 1;
         int real = subtask.getEpicId();
        Assertions.assertEquals(expected,real);
    }

    @Test
    void setEpicId() {
         int expected = 1;
         subtask.setEpicId(1);
         int real = subtask.getEpicId();
         Assertions.assertEquals(expected,real);
    }
}