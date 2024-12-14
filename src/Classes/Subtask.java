package Classes;

public class Subtask extends Task {

    private Integer epicId;


    public Subtask(Epic epic, String taskName, String description, TaskStatus taskStatus) {
        super(taskName, description, taskStatus);
        this.epicId = epic.getId();
    }

    @Override

    public String toString() {
        return "Classes.Subtask{" +
                "id=" + getId() + ", " +
                "name=" + getTaskName() + ", " +
                "status=" + getTaskStatus() +
                "}";

    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }
}
