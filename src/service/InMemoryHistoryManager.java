package service;
import model.Task;
import java.util.*;

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

    static Map<Integer, Node> linkedHistoryMap = new HashMap<>(0);
    private Node head;
    private Node tail;


    public Node linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null)
            head = newNode;
        else
            oldTail.next = newNode;
        return newNode;
    }

    private void removeNode(Node nodeToRemove) {
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
        if (linkedHistoryMap.containsKey(task.getId())) {
            removeNode(linkedHistoryMap.get(task.getId()));
        }
        linkedHistoryMap.put(task.getId(), linkLast(task));
    }

    @Override
    public void remove(int id) {
        if (linkedHistoryMap.containsKey(id)) {
            removeNode(linkedHistoryMap.get(id));
            linkedHistoryMap.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> tasksList = new ArrayList<>();
        Node currentNode = head;
        while (currentNode != null){
            tasksList.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return tasksList;
    }

}
