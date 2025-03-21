package dto;

import classes.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public record TaskDTO(
        String taskName,
        String description,
        TaskStatus taskStatus,
        Duration duration,
        LocalDateTime startTime
) {
    @Override
    public String taskName() {
        return taskName;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public TaskStatus taskStatus() {
        return taskStatus;
    }

    @Override
    public Duration duration() {
        return duration;
    }

    @Override
    public LocalDateTime startTime() {
        return startTime;
    }
}
