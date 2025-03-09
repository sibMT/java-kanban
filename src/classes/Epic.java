package classes;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;


public class Epic extends Task {
    private ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String taskName, String description) {
        super(taskName, description, TaskStatus.NEW, Duration.ofMinutes(0), LocalDateTime.now());
    }

    public Epic(int id, String taskName, String description, TaskStatus taskStatus, Duration duration,
                LocalDateTime startTime) {
        super(id, taskName, description, TaskStatus.NEW, duration, startTime);
    }

    public Epic(String taskName, String description, Duration taskDuration, LocalDateTime startTime) {
        super(taskName, description, TaskStatus.NEW, taskDuration, startTime);
    }

    public void createSubtaskId(Subtask subtask) {
        subtasks.add(subtask.getId());
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    public void removeSubtaskId(Integer id) {
        subtasks.remove(id);
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    @Override
    public String serialize() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n", getId(), TaskType.EPIC, getTaskName(), getTaskStatus(),
                getDescription(), getDuration().toMinutes(), (getStartTime() != null ? getStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy"))
                        : "null"), (getEndTime() != null
                        ? getEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy")) : "null"), subtasks);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name=" + getTaskName() +
                ", subTasksIdList=" + subtasks +
                ", status=" + getTaskStatus() +
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + (getStartTime() != null
                ? getStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy"))
                : "null") +
                ", endTime=" + (getEndTime() != null
                ? getEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy"))
                : "null") +
                '}';
    }
}