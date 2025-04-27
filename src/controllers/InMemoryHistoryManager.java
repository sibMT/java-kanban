package controllers;

import classes.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node<Task>> obtainedTask;
    private Node<Task> head;
    private Node<Task> tail;

    public InMemoryHistoryManager() {
        this.obtainedTask = new HashMap<>();
    }

    public static class Node<T> {
        public Node<T> prev;
        public Node<T> next;
        public T data;

        public Node(Node<T> prev, Node<T> next, T data) {
            this.prev = prev;
            this.next = next;
            this.data = data;
        }
    }

    @Override
    public void addToHistory(Task task) {
        if (task == null) return;

        remove(task.getId());
        lastLink(task);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = obtainedTask.get(id);
        if (node != null) {
            // Корректное обновление связей
            if (node.prev != null) {
                node.prev.next = node.next;
            } else {
                head = node.next;
            }

            if (node.next != null) {
                node.next.prev = node.prev;
            } else {
                tail = node.prev;
            }

            obtainedTask.remove(id);
        }
    }


    @Override
    public List<Task> getHistory() {
        return getTasks();

    }

    private void lastLink(Task task) {
        Node<Task> newNode = new Node<>(null, head, task);
        obtainedTask.put(task.getId(), newNode);

        if (head == null) {
            tail = newNode;
        } else {
            head.prev = newNode;
        }
        head = newNode;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> current = head;
        while (current != null) {
            tasks.add(current.data);
            current = current.next;
        }
        return tasks;
    }
}