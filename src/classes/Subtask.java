package classes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {

    private Integer epicId;


    public Subtask(Epic epic, String taskName, String description, TaskStatus taskStatus, Duration duration,
                   LocalDateTime startTime) {
        super(taskName, description, taskStatus, duration, startTime);
        this.epicId = epic.getId();
    }

    public Subtask(int id, int epicId, String taskName, String description, TaskStatus taskStatus, Duration duration,
                   LocalDateTime startTime) {
        super(id, taskName, description, taskStatus, duration, startTime);
        this.epicId = epicId;
    }

    @Override

    public String toString() {
        return "Classes.Subtask{" +
                "id=" + getId() + ", " +
                "name=" + getTaskName() + ", " +
                "status=" + getTaskStatus() +
                ", duration=" + getDuration().toMinutes() +
                ", startTime=" + getStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy")) +
                ", endTime=" + getEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy")) +
                "}";

    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String serialize() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s\n", getId(), TaskType.SUBTASK, getTaskName(), getTaskStatus(),
                getDescription(), getDuration().toMinutes(),
                getStartTime().format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy")),
                getEndTime().format(DateTimeFormatter.ofPattern("HH:mm:ss/dd.MM.yyyy")), getEpicId());
    }
}