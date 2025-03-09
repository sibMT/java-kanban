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
        this.endTime = startTime.plus(duration);
    }

    public Task(int id, String taskName, String description, TaskStatus taskStatus, Duration duration,
                LocalDateTime startTime) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.taskStatus = taskStatus;
        this.duration = duration;
        this.startTime = startTime;
        this.endTime = (startTime != null) ? startTime.plus(duration) : null;
    }

    @Override
    public String toString() {
        return "Classes.Task" +
                "id=" + id + ", " +
                "taskName='" + taskName + "', " +
                "description='" + description + "', " +
                "taskStatus=" + taskStatus + ", duration=" + duration.toMinutes() +
                ", startTime=" + startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy")) +
                ", endTime=" + endTime.format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy")) +
                "}";
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, taskName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String serialize() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", id, TaskType.TASK, taskName, taskStatus, description,
                duration.toMinutes(), startTime.format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy")),
                endTime.format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy")));
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
