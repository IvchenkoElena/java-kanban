package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    static class Node {
        public Task task;
        public Node next;
        public Node prev;


        public Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    private Map<Integer, Node> linkedHistoryMap = new HashMap<>(); //
    private Node head;
    private Node tail;

    private Node linkLast(Task task) {
        final Node newNode = new Node(tail, task, null);
        if (tail == null)
            head = newNode;
        else
            tail.next = newNode;
        tail = newNode;
        return newNode;
    }

    private void removeNode(Node nodeToRemove) {
        if (nodeToRemove == null) {
            return;
        }
        linkedHistoryMap.remove(nodeToRemove.task.getId());

        Node prevNode = nodeToRemove.prev;
        Node nextNode = nodeToRemove.next;
        if (prevNode == null && nextNode == null) {
            head = null;
            tail = null;
        } else if (prevNode == null) {
            nextNode.prev = null;
            head = nextNode;
        } else if (nextNode == null) {
            prevNode.next = null;
            tail = prevNode;
        } else {
            prevNode.next = nextNode;
            nextNode.prev = prevNode;
        }
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            System.out.println("Введен номер не существующей задачи");
            return;
        }
        removeNode(linkedHistoryMap.get(task.getId()));

        linkedHistoryMap.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        removeNode(linkedHistoryMap.get(id));
    }

    @Override
    public List<Task> getHistory() {

        List<Task> tasksList = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null) {
            tasksList.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return tasksList;
    }
}
