package controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager real = Managers.getDefault();
        Assertions.assertInstanceOf(InMemoryTaskManager.class, real);
    }

    @Test
    void getDefaultHistory() {
        HistoryManager real = Managers.getDefaultHistory();
        Assertions.assertInstanceOf(InMemoryHistoryManager.class, real);
    }

    @Test
    void loadFromFile() {
        Path filePath = null;
        try {
            filePath = Files.createTempFile("data-", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TaskManager real = Managers.getFileBackedTaskManager(filePath.toFile());
        Assertions.assertInstanceOf(FileBackedTaskManager.class, real);
    }
}