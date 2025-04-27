package classes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.time.format.DateTimeFormatter;


public class Task {
    private int id;
    private String taskName;
    private String description;
    private TaskStatus taskStatus;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(String taskName, String description, TaskStatus taskStatus, Duration duration, LocalDateTime startTime) {
        this.taskName = taskName;
        this.description = description;
        this.taskStatus = taskStatus;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = calculateEndTime();
    }

    public Task(int id, String taskName, String description, TaskStatus taskStatus, Duration duration,
                LocalDateTime startTime) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.taskStatus = taskStatus;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = calculateEndTime();
    }

    public Task(String taskName, String description, TaskStatus taskStatus) {
        this(taskName, description, taskStatus, null, null);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus=" + taskStatus +
                ", duration=" + (duration != null ? duration.toMinutes() : "null") +
                ", startTime=" + (startTime != null ? startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy")) : "null") +
                ", endTime=" + (endTime != null ? endTime.format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy")) : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String serialize() {
        long durationMinutes = (duration != null) ? duration.toMinutes() : 0;
        String startTimeStr = (startTime != null) ? startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy")) : "null";
        String endTimeStr = (endTime != null) ? endTime.format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy")) : "null";

        return String.format("%s,%s,%s,%s,%s,%d,%s,%s\n",
                id, TaskType.TASK, taskName, taskStatus, description,
                durationMinutes, startTimeStr, endTimeStr);
    }

    private LocalDateTime calculateEndTime() {
        return (startTime != null && duration != null) ? startTime.plus(duration) : null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
        this.endTime = calculateEndTime();
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        this.endTime = calculateEndTime();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public TaskStatus getStatus() {
        return taskStatus;
    }
}
