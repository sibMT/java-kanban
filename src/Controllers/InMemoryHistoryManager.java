package Controllers;

import Classes.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{

    private ArrayList<Task> history = new ArrayList<>();


    @Override
    public void addToHistory(Task task) {
        if (task != null) {
            history.add(task);
            if(history.size() > 10) {
                history.removeFirst();
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }
}
