package classes;

import java.util.ArrayList;


public class Epic extends Task {
    private ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String taskName, String description) {
        super(taskName, description, TaskStatus.NEW);
    }

    public Epic(int id, String taskName, String description, TaskStatus taskStatus) {
        super(id, taskName, description, taskStatus);
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
}