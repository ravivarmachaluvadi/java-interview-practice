import java.util.HashMap;
import java.util.Map;

// https://leetcode.com/problems/copy-list-with-random-pointer/
class CopyRandomList {
    public static Node copyRandomList(Node head) {
        if (head == null)
            return null;

        // Map to store original nodes and their corresponding copies
        Map<Node, Node> map = new HashMap<>();

        // First pass: create a copy of each node without setting the random pointer
        Node curr = head;
        while (curr != null) {
            map.put(curr, new Node(curr.val));
            curr = curr.next;
        }

        // Second pass: set the next and random pointers for each copy
        curr = head;
        while (curr != null) {
            map.get(curr).next = map.get(curr.next);
            map.get(curr).random = map.get(curr.random);
            curr = curr.next;
        }
        // Return the head of the copied linked list
        return map.get(head);
    }

    public static void main(String[] args) {
        Node node = copyRandomList(new Node(1));
    }
}

class Node {
    Node next;
    Node random;
    int val;

    public Node(int val) {
        this.val = val;
    }
}
