package controllers;

import classes.Epic;
import classes.Subtask;
import classes.Task;
import classes.TaskStatus;
import exception.FileManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTaskManager(File file, HistoryManager historyManager) {
        super(historyManager);
        this.file = file;
    }

    public FileBackedTaskManager(File file) {
        super(new InMemoryHistoryManager());
        this.file = file;
    }

    public void save() throws FileManagerSaveException {
        final String title = "id,type,name,status,description,epic\n";
        List<String> lines = new ArrayList<>();
        lines.add(title);

        getAllTasks().forEach(task -> lines.add(task.serialize()));
        getAllEpics().forEach(epic -> lines.add(epic.serialize()));
        getAllSubtasks().forEach(subTask -> lines.add(subTask.serialize()));

        saveToFile(lines);
    }

    public void load() {
        List<String> lines = null;
        try {
            lines = loadFromFile(file);
        } catch (FileManagerSaveException e) {
            throw new RuntimeException("Ошибка при загрузке данных из файла", e);
        }
        if (lines != null && !lines.isEmpty()) {
            lines.removeFirst();
        }
        for (String line : lines) {
            fromString(line);
        }
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);

        try {
            save();
        } catch (FileManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);

        try {
            save();
        } catch (FileManagerSaveException e) {
            throw new RuntimeException(e);
        }

        return updatedTask;
    }

    @Override
    public Task removeTaskById(Integer id) {
        super.removeTaskById(id);

        try {
            save();
        } catch (FileManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return tasks.remove(id);
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();

        try {
            save();
        } catch (FileManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);

        try {
            save();
        } catch (FileManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);

        try {
            save();
        } catch (FileManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeEpicById(Integer id) {
        super.removeEpicById(id);

        try {
            save();
        } catch (FileManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();

        try {
            save();
        } catch (FileManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Subtask createSubtasks(Subtask subTask) {
        super.createSubtasks(subTask);

        try {
            save();
        } catch (FileManagerSaveException e) {
            throw new RuntimeException(e);
        }
        return subTask;
    }

    @Override
    public void updateSubtask(Subtask subTask) {
        super.updateSubtask(subTask);

        try {
            save();
        } catch (FileManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeSubtaskById(Integer id) {
        super.removeSubtaskById(id);

        try {
            save();
        } catch (FileManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();

        try {
            save();
        } catch (FileManagerSaveException e) {
            throw new RuntimeException(e);
        }
    }

    protected void saveToFile(List<String> lines) throws FileManagerSaveException {
        if (file == null) {
            throw new FileManagerSaveException("Невозможно сохранить данные в файл.");
        }

        try {
            FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (String line : lines) {
                bufferedWriter.write(line);
            }

            bufferedWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> loadFromFile(File file) throws FileManagerSaveException {
        if (file == null) {
            throw new FileManagerSaveException("Невозможно загрузить данные из файла.");
        }

        List<String> lines = new ArrayList<>();

        try (FileReader fileReader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }

        } catch (IOException e) {
            throw new FileManagerSaveException(e.getMessage());
        }

        return lines;
    }

    private void handleTask(String[] lines) {
        super.createTask(new Task(
                Integer.parseInt(lines[0]),
                lines[2],
                lines[4],
                getTaskStatusFromString(lines[3])
        ));
    }

    private void handleEpic(String[] lines) {
        super.createEpic(new Epic(
                Integer.parseInt(lines[0]),
                lines[2],
                lines[4],
                getTaskStatusFromString(lines[3])
        ));
    }

    private void handleSubtask(String[] lines) {
        int subtaskId = Integer.parseInt(lines[0]);
        int epicId = Integer.parseInt(lines[lines.length - 1]);
        super.createSubtasks(new Subtask(
                subtaskId,
                epicId,
                lines[2],
                lines[4],
                getTaskStatusFromString(lines[3])
        ));
    }

    private void fromString(String line) {
        String[] lines = line.trim().split(",");

        switch (lines[1]) {
            case "TASK" -> handleTask(lines);
            case "EPIC" -> handleEpic(lines);
            case "SUBTASK" -> handleSubtask(lines);
        }
    }

    private TaskStatus getTaskStatusFromString(String line) {
        return switch (line) {
            case "NEW" -> TaskStatus.NEW;
            case "IN_PROGRESS" -> TaskStatus.IN_PROGRESS;
            case "DONE" -> TaskStatus.DONE;
            default -> throw new IllegalStateException("Неизвестное значение: " + line);
        };
    }
}
