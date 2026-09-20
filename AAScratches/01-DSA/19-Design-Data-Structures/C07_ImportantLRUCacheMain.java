/*
 * =====================================================================
 *  LRU Cache                              LeetCode 146 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Design a fixed-capacity key-value cache where get and put are both O(1).
 *   get(key) returns the value or -1 if absent, and counts as a use.
 *   put(key, value) inserts or updates, also counts as a use, and when the cache is
 *   over capacity it evicts the key that was used least recently.
 *
 * EXAMPLE
 *   capacity 2: put(1,1), put(2,2), get(1) -> 1   (order is now 1 newest, 2 oldest)
 *   put(3,3) evicts key 2       -> get(2) = -1
 *   put(4,4) evicts key 1       -> get(1) = -1, get(3) = 3, get(4) = 4
 *
 * DESIGN  (HashMap plus doubly linked list)
 *   Map<Integer,Node> cache   key -> its node, so lookup is O(1).
 *   Doubly linked list        usage order, head = most recently used, tail = least.
 *   Node                      key, value, prev, next. It stores the key as well as the
 *                             value so eviction can delete the map entry from the tail
 *                             node alone - without the key, eviction would be O(n).
 *   get  map lookup, then moveToHead (unlink, relink at the front).
 *   put  existing key: overwrite the value and moveToHead.
 *        new key: if full, drop the tail from both the list and the map, then addToHead.
 *   This version deliberately uses no dummy sentinel nodes, so head and tail can be null
 *   and unlinking has to branch on four cases.
 *
 * KEY DECISIONS
 *   - Why a doubly linked list? Eviction needs the element at the far end and reordering
 *     needs to unlink a node found by reference. Both are O(1) only with a prev pointer;
 *     a singly linked list would need an O(n) scan to find the predecessor.
 *   - Why not an ArrayList or a queue? Moving an element to the front of an array is
 *     O(n), and a queue cannot reorder an item already inside it.
 *   - Sentinels vs none: with a dummy head and tail, remove() is three lines and never
 *     null-checks. The no-sentinel version saves two objects and costs four branches -
 *     write the sentinel version in an interview, it is far harder to get wrong.
 *   - Fixed: addToHead left the new head's prev pointer dangling at its old predecessor,
 *     so the list was only consistent when walked forwards. Any backwards walk (or a
 *     later sentinel refactor) would have read a stale node. addToHead now clears it.
 *   - Fixed: capacity 0 made the first put dereference a null tail. put is now a no-op
 *     for a non-positive capacity.
 *
 * COMPLEXITY
 *   Time  O(1) for get and put - one hash lookup plus a constant number of relinks
 *   Space O(capacity) for the map entries and the node objects
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not just LinkedHashMap with accessOrder=true and removeEldestEntry? You can,
 *     and should say so, but the interviewer wants the list built by hand.
 *   - Make it thread-safe: one lock around both ops, or segment the cache by key hash.
 *   - Add a TTL: store an expiry on each node and treat an expired hit as a miss.
 *   - LFU (LC 460) is the standard follow-up: evict by frequency, not recency.
 *
 * RUN
 *   main() runs 3 cases: the LeetCode walkthrough, a capacity-1 cache (every insert
 *   evicts), and an update-in-place case that must NOT evict. Each line prints actual
 *   vs expected, including forwards and backwards views of the list to prove the links
 *   agree in both directions.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class LRUCache {

    private final int capacity;
    private final Map<Integer, Node> cache;

    /** head = most recently used, tail = least recently used. No dummy nodes. */
    private Node head;
    private Node tail;

    LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>(Math.max(1, capacity));
    }

    /** Returns the value and marks the key as most recently used, or -1 if absent. */
    int get(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return -1;
        }
        moveToHead(node);
        return node.value;
    }

    /** Inserts or updates a key, marking it most recently used. */
    void put(int key, int value) {
        if (capacity <= 0) {
            return; // nothing can be cached, and there is no tail to evict
        }
        Node node = cache.get(key);
        if (node != null) {
            // Update in place: the size does not change, so nothing is evicted.
            node.value = value;
            moveToHead(node);
            return;
        }
        if (cache.size() >= capacity) {
            // Evict the least recently used entry. The node carries its own key, which
            // is what makes removing it from the map O(1).
            cache.remove(tail.key);
            remove(tail);
        }
        Node fresh = new Node(key, value);
        addToHead(fresh);
        cache.put(key, fresh);
    }

    private void moveToHead(Node node) {
        if (node == head) {
            return; // already newest
        }
        remove(node);
        addToHead(node);
    }

    private void addToHead(Node node) {
        node.prev = null; // the head has no predecessor - a stale prev here corrupts
        if (head == null) {
            node.next = null;
            head = tail = node; // first node in the list
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
    }

    /** Unlinks a node from the list. Four cases because there are no sentinels. */
    private void remove(Node node) {
        if (node == head && node == tail) {
            head = tail = null;
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

    /** Keys walked from head to tail: most recently used first. Demo only. */
    List<Integer> keysNewestFirst() {
        List<Integer> keys = new ArrayList<>();
        for (Node n = head; n != null; n = n.next) {
            keys.add(n.key);
        }
        return keys;
    }

    /** Same list walked backwards via prev. Must be the exact reverse of the above. */
    List<Integer> keysOldestFirst() {
        List<Integer> keys = new ArrayList<>();
        for (Node n = tail; n != null; n = n.prev) {
            keys.add(n.key);
        }
        return keys;
    }
}

class ImportantLRUCacheMain {

    public static void main(String[] args) {
        // ---- case 1: the LeetCode walkthrough -----------------------------------
        LRUCache cache = new LRUCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        print("case 1 get(1)", cache.get(1), 1);
        print("case 1 order newest first", cache.keysNewestFirst(), "[1, 2]");
        cache.put(3, 3);                       // evicts key 2 (least recently used)
        print("case 1 get(2) after evict", cache.get(2), -1);
        cache.put(4, 4);                       // evicts key 1
        print("case 1 get(1) after evict", cache.get(1), -1);
        print("case 1 get(3)", cache.get(3), 3);
        print("case 1 get(4)", cache.get(4), 4);
        // 4 was read last, so it is newest; walking back from the tail must mirror it.
        print("case 1 order newest first", cache.keysNewestFirst(), "[4, 3]");
        print("case 1 order oldest first", cache.keysOldestFirst(), "[3, 4]");

        // ---- case 2: edge - capacity 1, every insert evicts ----------------------
        LRUCache tiny = new LRUCache(1);
        tiny.put(1, 100);
        print("case 2 get(1)", tiny.get(1), 100);
        tiny.put(2, 200);                      // evicts key 1
        print("case 2 get(1) after evict", tiny.get(1), -1);
        print("case 2 get(2)", tiny.get(2), 200);
        print("case 2 order", tiny.keysNewestFirst(), "[2]");

        // ---- case 3: tricky - updating an existing key must not evict ------------
        LRUCache upd = new LRUCache(2);
        upd.put(1, 1);
        upd.put(2, 2);
        upd.put(1, 11);                        // update, not insert: still 2 entries
        print("case 3 get(1) updated", upd.get(1), 11);
        print("case 3 get(2) survived", upd.get(2), 2);
        // get(2) just ran, so 2 is newest and 1 is the eviction candidate.
        upd.put(3, 3);
        print("case 3 get(1) after evict", upd.get(1), -1);
        print("case 3 order newest first", upd.keysNewestFirst(), "[3, 2]");
        print("case 3 order oldest first", upd.keysOldestFirst(), "[2, 3]");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

class Node {
    int key;
    int value;
    Node prev;
    Node next;

    Node(int key, int value) {
        this.key = key;
        this.value = value;
    }
}
