/**
 * Implements an LFU (Least Frequently Used) cache with O(1) get and put operations.
 *
 * Problem: Design a cache that evicts the key with the lowest access frequency when capacity is exceeded.
 * Approach: Use a hash map to store key → node, another map from frequency → doubly linked list of nodes,
 * and track the current minimum frequency. On get/put, update the node's frequency by moving it
 * between lists; evict the tail of the min-frequency list when needed.
 *
 * Time Complexity: O(1) per get or put operation.
 * Space Complexity: O(capacity) for storing nodes and frequency buckets.
 */
import java.util.*;

class DLLNode {
    int key;
    int val;
    int frequency;
    DLLNode prev;
    DLLNode next;

    public DLLNode(int key, int val) {
        this.key = key;
        this.val = val;
        // remember
        this.frequency = 1;
    }
}

class DoubleLinkedList {
    int listSize;
    DLLNode head;
    DLLNode tail;

    public DoubleLinkedList() {
        this.listSize = 0;
        this.head = this.tail = null; // Modified to set both head and tail to null
    }

    public void addNode(DLLNode curNode) {
        if (head == null) { // If list is empty, curNode becomes head and tail
            head = tail = curNode;
        } else {
            curNode.next = head;
            head.prev = curNode;
            head = curNode;
        }
        listSize++;
    }

    // if else if ladder
    public void removeNode(DLLNode curNode) {
        if (curNode == head && curNode == tail) { // Only one node
            head = tail = null;
        } else if (curNode == head) { // Node to remove is head
            head = head.next;
            head.prev = null;
        } else if (curNode == tail) { // Node to remove is tail
            tail = tail.prev;
            tail.next = null;
        } else { // Node is in the middle
            curNode.prev.next = curNode.next;
            curNode.next.prev = curNode.prev;
        }
        listSize--;
    }
}

class LFUCache {
    final int capacity;
    int curSize = 0;
    int minFrequency = 0;
    Map<Integer, DLLNode> cache = new HashMap<>();
    Map<Integer, DoubleLinkedList> frequencyMap = new HashMap<>();

    public LFUCache(int capacity) {
        this.capacity = capacity;
    }

    public int get(int key) {
        DLLNode curNode = cache.get(key);
        if (curNode == null) {
            return -1;
        }
        updateNode(curNode);
        return curNode.val;
    }

    public void put(int key, int value) {
        if (capacity == 0) {
            return;
        }

        if (cache.containsKey(key)) {
            DLLNode curNode = cache.get(key);
            curNode.val = value;
            updateNode(curNode);
        } else {
            curSize++;
            if (curSize > capacity) {
                DoubleLinkedList minFreqList = frequencyMap.get(minFrequency);
                cache.remove(minFreqList.tail.key);
                minFreqList.removeNode(minFreqList.tail);
                curSize--;
            }
            minFrequency = 1;
            DLLNode newNode = new DLLNode(key, value);

            DoubleLinkedList curList = frequencyMap.getOrDefault(1, new DoubleLinkedList());
            curList.addNode(newNode);
            frequencyMap.put(minFrequency, curList);
            cache.put(key, newNode);
        }
    }

    public void updateNode(DLLNode curNode) {
        int curFreq = curNode.frequency;
        DoubleLinkedList curList = frequencyMap.get(curFreq);
        curList.removeNode(curNode);

        if (curFreq == minFrequency && curList.listSize == 0) {
            minFrequency++;
        }

        curNode.frequency++;
        DoubleLinkedList newList = frequencyMap.getOrDefault(curNode.frequency, new DoubleLinkedList());
        newList.addNode(curNode);
        frequencyMap.put(curNode.frequency, newList);
    }

    public static void main(String[] args) {
        LFUCache cache = new LFUCache(2);

        // Test cases
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1)); // returns 1
        cache.put(3, 3);                  // evicts key 2
        System.out.println(cache.get(2)); // returns -1 (not found)
        System.out.println(cache.get(3)); // returns 3
        cache.put(4, 4);                  // evicts key 1
        System.out.println(cache.get(1)); // returns -1 (not found)
        System.out.println(cache.get(3)); // returns 3
        System.out.println(cache.get(4)); // returns 4
    }
}
