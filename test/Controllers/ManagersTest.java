package Controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager real = Managers.getDefault();
        Assertions.assertInstanceOf(InMemoryTaskManager.class,real);
    }

    @Test
    void getDefaultHistory() {
        HistoryManager real = Managers.getDefaultHistory();
        Assertions.assertInstanceOf(InMemoryHistoryManager.class,real);
    }
}