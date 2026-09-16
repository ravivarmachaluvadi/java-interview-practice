import java.util.*;

// https://leetcode.com/problems/all-oone-data-structure/description/
// 432. All O`one Data Structure
// Doubly-linked list node (bucket) representing a specific count,
// and storing all keys that currently have that count.
class Node {
    int count;
    Set<String> keys;
    Node prev;
    Node next;

    Node(int count) {
        this.count = count;
        this.keys = new HashSet<>();
    }

    Node(int count, String key) {
        this.count = count;
        this.keys = new HashSet<>();
        this.keys.add(key);
    }
}

class AllOneII {

    // Sentinel (dummy) head & tail for easier list operations  
    private final Node head;
    private final Node tail;

    // Map from key → the bucket (Node) where it currently resides  
    private final Map<String, Node> keyNodeMap;

    public AllOneII() {
        head = new Node(Integer.MIN_VALUE);  // dummy
        tail = new Node(Integer.MAX_VALUE);  // dummy
        head.next = tail;
        tail.prev = head;
        keyNodeMap = new HashMap<>();
    }

    public void inc(String key) {
        if (!keyNodeMap.containsKey(key)) {
            // new key with count = 1
            Node next = head.next;
            if (next == tail || next.count != 1) {
                // no bucket for count=1
                Node newNode = new Node(1, key);
                insertAfter(head, newNode);
                keyNodeMap.put(key, newNode);
            } else {
                // bucket for count=1 exists
                next.keys.add(key);
                keyNodeMap.put(key, next);
            }
        } else {
            // existing key: move from count = c to c+1
            Node curr = keyNodeMap.get(key);
            int newCount = curr.count + 1;
            Node next = curr.next;
            if (next == tail || next.count != newCount) {
                // need to insert new bucket for newCount
                Node newNode = new Node(newCount, key);
                insertAfter(curr, newNode);
                keyNodeMap.put(key, newNode);
            } else {
                // bucket for newCount exists
                next.keys.add(key);
                keyNodeMap.put(key, next);
            }
            // remove key from old bucket
            curr.keys.remove(key);
            if (curr.keys.isEmpty()) {
                removeNode(curr);
            }
        }
    }

    public void dec(String key) {
        // It is guaranteed key exists
        Node curr = keyNodeMap.get(key);
        if (curr == null) return;  // safety check

        if (curr.count == 1) {
            // Removing key entirely
            keyNodeMap.remove(key);
            curr.keys.remove(key);
            if (curr.keys.isEmpty()) {
                removeNode(curr);
            }
        } else {
            // move to bucket count = c-1
            int newCount = curr.count - 1;
            Node prev = curr.prev;
            if (prev == head || prev.count != newCount) {
                // need to insert new bucket
                Node newNode = new Node(newCount, key);
                insertAfter(prev, newNode);
                keyNodeMap.put(key, newNode);
            } else {
                // bucket exists
                prev.keys.add(key);
                keyNodeMap.put(key, prev);
            }
            // remove key from old bucket
            curr.keys.remove(key);
            if (curr.keys.isEmpty()) {
                removeNode(curr);
            }
        }
    }

    public String getMaxKey() {
        if (tail.prev == head) {
            return "";  // empty
        }
        // take any key from the highest-count bucket
        return tail.prev.keys.iterator().next();
    }

    public String getMinKey() {
        if (head.next == tail) {
            return "";  // empty
        }
        // take any key from the lowest-count bucket
        return head.next.keys.iterator().next();
    }

    // Helper: insert node `toInsert` after `node`
    private void insertAfter(Node node, Node toInsert) {
        toInsert.prev = node;
        toInsert.next = node.next;
        node.next.prev = toInsert;
        node.next = toInsert;
    }

    // Helper: remove `node` from list
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
        // optional: clear node.prev/node.next for safety
        node.prev = null;
        node.next = null;
    }


    // Example main to demonstrate usage
    public static void main(String[] args) {
        AllOneII allOne = new AllOneII();
        allOne.inc("hello");
        allOne.inc("hello");
        System.out.println("MaxKey: " + allOne.getMaxKey());  // should print "hello"
        System.out.println("MinKey: " + allOne.getMinKey());  // should print "hello"

        allOne.inc("leet");
        System.out.println("MaxKey: " + allOne.getMaxKey());  // "hello" (count=2)  
        System.out.println("MinKey: " + allOne.getMinKey());  // "leet" (count=1)

        // More operations:
        allOne.inc("leet");
        allOne.inc("leet");
        System.out.println("MaxKey now: " + allOne.getMaxKey());  // "leet" (count=3)
        System.out.println("MinKey now: " + allOne.getMinKey());  // "hello" (count=2)

        allOne.dec("leet");  // "leet" down to count=2
        System.out.println("After dec: MaxKey = " + allOne.getMaxKey());  // either "hello" or "leet" (both count=2)
        System.out.println("After dec: MinKey = " + allOne.getMinKey());  // either "hello" or "leet"
    }
}
