package Classes;

import java.util.Objects;

public class Task {
    private int id;
    private String taskName;
    private String description;
    private TaskStatus taskStatus;


    public Task(int id, String taskName, String description, TaskStatus taskStatus) {
        this.id = id;
        this.taskName = taskName;
        this.description = description;
        this.taskStatus = taskStatus;
    }
    public Task(String taskName, String description, TaskStatus taskStatus) {
        this.taskName = taskName;
        this.description = description;
        this.taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return "Classes.Task" +
                "id=" + id + ", " +
                "taskName='" + taskName + "', " +
                "description='" + description + "', " +
                "taskStatus=" + taskStatus +
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
}
