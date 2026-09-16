import java.util.HashMap;
import java.util.Map;

class LRUCache {
    private final int capacity;
    private final Map<Integer, Node> cache;
    private Node head, tail; // No dummy nodes

    // Initialize the cache with given capacity
    LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);
    }

    // Get the value of the key if present, else return -1
    int get(int key) {
        Node node = cache.get(key);
        if (node == null) return -1; // Key not found
        // Move the accessed node to the front (most recently used)
        moveToHead(node);
        return node.value;
    }

    // Insert or update a key-value pair
    void put(int key, int value) {
        Node node = cache.get(key);
        if (node != null) {
            // Update the value and move to head (most recently used)
            node.value = value;
            moveToHead(node);
        } else {
            // If capacity exceeded, remove least recently used node
            if (cache.size() >= capacity) {
                cache.remove(tail.key);
                remove(tail);
            }
            // Create new node, add to head, and insert into cache
            Node newNode = new Node(key, value);
            addToHead(newNode);
            cache.put(key, newNode);
        }
    }

    // Move a given node to the front (most recently used)
    private void moveToHead(Node node) {
        if (node == head) return;
        remove(node);
        addToHead(node);
    }

    // Add a node at the head (most recently used position)
    private void addToHead(Node node) {
        if (head == null) {
            head = tail = node; // First node in the list
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
    }

    // Remove a node from the linked list
    private void remove(Node node) {
        if (node == head && node == tail) {
            head = tail = null; // List becomes empty
        } else if (node == head) {
            head = head.next;
            head.prev = null;
        } else if (node == tail) {
            tail = tail.prev;
            tail.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
    }
}

class ImportantLRUCacheMain {
    public static void main(String[] args) {
        LRUCache cache = new LRUCache(2);

        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1));
        cache.put(3, 3);
        System.out.println(cache.get(2));
        cache.put(4, 4);
        System.out.println(cache.get(1));
        System.out.println(cache.get(3));
        System.out.println(cache.get(4));
    }
}

class Node {
    int key, value;
    Node next, prev;

    Node(int key, int value) {
        this.key = key;
        this.value = value;
    }
}
