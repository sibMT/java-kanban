package controllers;

import exception.FileManagerSaveException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Managers {

    public static TaskManager getDefault() {
        HistoryManager historyManager = getDefaultHistory();
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getFileBackedTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        if (file == null) {
            throw new FileManagerSaveException("Невозможно загрузить данные из файла.");
        }

        FileBackedTaskManager manager = new FileBackedTaskManager(file, new InMemoryHistoryManager());
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            while ((line = bufferedReader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                manager.fromString(line);
            }
        } catch (IOException e) {
            throw new FileManagerSaveException(e.getMessage());
        }
        return manager;
    }
}