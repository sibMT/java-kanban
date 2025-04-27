package controllers;

import classes.Epic;
import classes.Subtask;
import classes.Task;
import classes.TaskStatus;
import exception.FileManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public void save() {
        final String title = "id,type,name,status,description,epic\n";
        List<String> lines = new ArrayList<>();
        lines.add(title);

        getAllTasks().forEach(task -> lines.add(task.serialize()));
        getAllEpics().forEach(epic -> lines.add(epic.serialize()));
        getAllSubtasks().forEach(subTask -> lines.add(subTask.serialize()));

        saveToFile(lines);
    }

    @Override
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public void removeTaskById(Integer id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(Integer id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeAllEpics() {
        super. removeAllEpics();
        save();
    }

    @Override
    public Subtask createSubtasks(Subtask subTask) {
        super.createSubtasks(subTask);
        save();
        return subTask;
    }

    @Override
    public void updateSubtask(Subtask subTask) {
        super.updateSubtask(subTask);
        save();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    private void saveToFile(List<String> lines) {
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

    private void handleTask(String[] lines) {
        LocalDateTime startTime = "null".equals(lines[6])
                ? null
                : LocalDateTime.parse(lines[6], DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy"));
        Duration duration = "null".equals(lines[5])
                ? Duration.ZERO
                : Duration.ofMinutes(Long.parseLong(lines[5]));
        super.createTask(new Task(
                Integer.parseInt(lines[0]),
                lines[2],
                lines[4],
                getTaskStatusFromString(lines[3]),
                duration,
                startTime
        ));
    }

    private void handleEpic(String[] lines) {
        LocalDateTime startTime = "null".equals(lines[6])
                ? null
                : LocalDateTime.parse(lines[6], DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy"));
        Duration duration = "null".equals(lines[5])
                ? Duration.ZERO
                : Duration.ofMinutes(Long.parseLong(lines[5]));
        super.createEpic(new Epic(
                Integer.parseInt(lines[0]),
                lines[2],
                lines[4],
                getTaskStatusFromString(lines[3]),
                duration,
                startTime
        ));
    }

    private void handleSubtask(String[] lines) {
        int subtaskId = Integer.parseInt(lines[0]);
        int epicId = Integer.parseInt(lines[lines.length - 1]);

        LocalDateTime startTime = "null".equals(lines[6])
                ? null
                : LocalDateTime.parse(lines[6], DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy"));
        Duration duration = "null".equals(lines[5])
                ? Duration.ZERO
                : Duration.ofMinutes(Long.parseLong(lines[5]));
        super.createSubtasks(new Subtask(
                subtaskId,
                epicId,
                lines[2],
                lines[4],
                getTaskStatusFromString(lines[3]),
                duration,
                startTime
        ));
    }

    protected void fromString(String line) {
        String[] lines = line.trim().split(",");

        switch (lines[1]) {
            case "TASK" -> handleTask(lines);
            case "EPIC" -> handleEpic(lines);
            case "SUBTASK" -> handleSubtask(lines);
            default -> throw new IllegalStateException("Неизвестный тип задачи: " + lines[1]);
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
