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
        if (!(task == null)) {
            remove(task.getId());
            lastLink(task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(obtainedTask.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTask();
    }

    private void lastLink(Task link) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, null, link);
        tail = newNode;
        obtainedTask.put(link.getId(), newNode);
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
    }

    private List<Task> getTask() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> presNode = head;
        while (!(presNode == null)) {
            tasks.add(presNode.data);
            presNode = presNode.next;
        }
        return tasks;
    }

    private void removeNode(Node<Task> node) {
        if (!(node == null)) {
            final Node<Task> prev = node.prev;
            final Node<Task> next = node.next;
            node.data = null;

            if (head == node && tail == node) {
                head = null;
                tail = null;
            } else if (tail == node) {
                tail = prev;
                tail.next = null;
            } else if (head == node) {
                head = next;
                head.prev = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }
        }
    }
}