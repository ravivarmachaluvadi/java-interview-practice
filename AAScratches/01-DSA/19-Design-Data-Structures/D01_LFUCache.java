/*
 * =====================================================================
 *  LFU Cache                                   LeetCode 460 | Hard  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Build a fixed-capacity cache where get(key) and put(key, value) are both O(1).
 *   When the cache is full, evict the key with the smallest use count. If several keys
 *   tie on use count, evict the least recently used among them. get returns -1 if absent.
 *   Every get and every put counts as one use of that key.
 *
 * EXAMPLE
 *   cap=2: put(1,1) put(2,2) get(1)=1 put(3,3) -> evicts key 2 (count 1 vs key 1's count 2)
 *   cap=2: put(1,1) put(2,2) get(1) get(2) put(3,3) -> both count 2, so LRU wins: evict 1
 *   cap=0: put(1,1) then get(1) = -1   (nothing can ever be stored)
 *
 * DESIGN  (frequency buckets + minFrequency pointer)
 *   DLLNode           one cache entry: key, value, its use count, and list links.
 *   DoubleLinkedList  an unsorted bucket of nodes that all share one use count.
 *                     addNode pushes at the head, so the tail is always the oldest
 *                     node in that bucket -- that is what makes the LRU tie-break free.
 *   LFUCache          cache        : key -> node            (O(1) lookup)
 *                     frequencyMap : count -> bucket        (O(1) bucket lookup)
 *                     minFrequency : the smallest count that currently has a node
 *
 * KEY DECISIONS
 *   1. A "use" never rebuilds anything: unlink the node from bucket f, bump its count,
 *      push it at the head of bucket f+1. Two pointer fixes, no scan.
 *   2. minFrequency only ever has to move by ONE step, never search. Two facts force it:
 *      - a use of the last node in the min bucket moves that node to min+1, so min+1
 *        is non-empty and is the new minimum;
 *      - inserting a new key gives it count 1, so the minimum is 1 again.
 *      Because of that, a plain int replaces what looks like it needs a sorted structure.
 *   3. Eviction is frequencyMap.get(minFrequency).tail -- least used, and oldest of those.
 *   4. capacity 0 is handled up front; without that guard put would insert then evict itself.
 *
 * COMPLEXITY
 *   Time  O(1)         every operation is a bounded number of map lookups and pointer writes
 *   Space O(capacity)  one node per key, plus one bucket per distinct count in use
 *
 * INTERVIEW FOLLOW-UPS
 *   - How does this differ from LRU (LC 146)? LRU needs one list; LFU needs a list per count.
 *   - Why is a PriorityQueue by count wrong here? Updating a count is O(n) or O(log n), not O(1).
 *   - Make it thread-safe, or shard it: what breaks first? (minFrequency is the shared hot spot.)
 *   - Add aging or a decay factor so a key that was hot an hour ago stops pinning the cache.
 *
 * RUN
 *   main() runs 3 cases (LeetCode sample, the LRU tie-break, capacity 0) and prints
 *   actual vs expected for every call.
 */

import java.util.HashMap;
import java.util.Map;

/** One cache entry. Lives in exactly one frequency bucket at a time. */
class DLLNode {
    int key;
    int val;
    int frequency;
    DLLNode prev;
    DLLNode next;

    DLLNode(int key, int val) {
        this.key = key;
        this.val = val;
        this.frequency = 1; // a node is created by a put, which already counts as one use
    }
}

/**
 * Bucket of nodes that share one use count. Order inside the bucket is recency:
 * head = most recently used, tail = least recently used. That is the LFU tie-break.
 */
class DoubleLinkedList {
    int listSize;
    DLLNode head;
    DLLNode tail;

    /** Push at the head, so this node becomes the most recently used of its bucket. */
    void addNode(DLLNode curNode) {
        curNode.prev = null;
        curNode.next = head; // null when the bucket is empty, which is exactly what we want
        if (head != null) {
            head.prev = curNode;
        } else {
            tail = curNode; // first node in the bucket is both head and tail
        }
        head = curNode;
        listSize++;
    }

    /** Unlink a node we already hold a reference to, in O(1). */
    void removeNode(DLLNode curNode) {
        if (curNode.prev != null) {
            curNode.prev.next = curNode.next;
        } else {
            head = curNode.next; // it was the head
        }
        if (curNode.next != null) {
            curNode.next.prev = curNode.prev;
        } else {
            tail = curNode.prev; // it was the tail
        }
        // Clear the links so a stale pointer can never be followed after a move.
        curNode.prev = null;
        curNode.next = null;
        listSize--;
    }
}

class LFUCache {
    private final int capacity;
    private int curSize = 0;
    private int minFrequency = 0;

    private final Map<Integer, DLLNode> cache = new HashMap<>();
    private final Map<Integer, DoubleLinkedList> frequencyMap = new HashMap<>();

    LFUCache(int capacity) {
        this.capacity = capacity;
    }

    public int get(int key) {
        DLLNode curNode = cache.get(key);
        if (curNode == null) {
            return -1;
        }
        touch(curNode);
        return curNode.val;
    }

    public void put(int key, int value) {
        if (capacity == 0) {
            return; // nothing can be stored; without this guard the code below would evict itself
        }

        DLLNode existing = cache.get(key);
        if (existing != null) {
            existing.val = value;
            touch(existing);
            return;
        }

        if (curSize == capacity) {
            evictLeastFrequent();
        }

        DLLNode newNode = new DLLNode(key, value);
        cache.put(key, newNode);
        bucketFor(1).addNode(newNode);
        minFrequency = 1; // a brand-new key has count 1, so the minimum is 1 again
        curSize++;
    }

    /** Count one more use of this node: move it from bucket f to bucket f + 1. */
    private void touch(DLLNode curNode) {
        int curFreq = curNode.frequency;
        DoubleLinkedList curList = frequencyMap.get(curFreq);
        curList.removeNode(curNode);

        // If we just emptied the minimum bucket, the node we moved is now the rarest,
        // so the new minimum is exactly curFreq + 1 -- never further away than one step.
        if (curFreq == minFrequency && curList.listSize == 0) {
            minFrequency = curFreq + 1;
        }

        curNode.frequency = curFreq + 1;
        bucketFor(curNode.frequency).addNode(curNode);
    }

    /** Drop the oldest node in the least-used bucket. */
    private void evictLeastFrequent() {
        DoubleLinkedList minFreqList = frequencyMap.get(minFrequency);
        DLLNode victim = minFreqList.tail; // least frequently used, and oldest among those
        minFreqList.removeNode(victim);
        cache.remove(victim.key);
        curSize--;
    }

    private DoubleLinkedList bucketFor(int frequency) {
        return frequencyMap.computeIfAbsent(frequency, f -> new DoubleLinkedList());
    }

    // ------------------------------------------------------------------
    // Demo
    // ------------------------------------------------------------------

    private static void check(String label, Object actual, Object expected) {
        boolean ok = String.valueOf(actual).equals(String.valueOf(expected));
        System.out.println(label + " -> actual " + actual + "   expected " + expected
                + (ok ? "   OK" : "   FAIL"));
    }

    public static void main(String[] args) {
        System.out.println("case 1: LeetCode sample, capacity 2");
        LFUCache c1 = new LFUCache(2);
        c1.put(1, 1);
        c1.put(2, 2);
        check("  get(1)", c1.get(1), 1);
        c1.put(3, 3); // key 2 has count 1, key 1 has count 2 -> evict key 2
        check("  get(2) after evict", c1.get(2), -1);
        check("  get(3)", c1.get(3), 3);
        c1.put(4, 4); // keys 1 and 3 both count 2; key 1 is older -> evict key 1
        check("  get(1) after evict", c1.get(1), -1);
        check("  get(3)", c1.get(3), 3);
        check("  get(4)", c1.get(4), 4);

        System.out.println("case 2: tie on count, least recently used loses");
        LFUCache c2 = new LFUCache(2);
        c2.put(1, 1);
        c2.put(2, 2);
        check("  get(1)", c2.get(1), 1); // key 1 count 2
        check("  get(2)", c2.get(2), 2); // key 2 count 2, and now the most recent
        c2.put(3, 3);                    // tie at count 2 -> evict the older one, key 1
        check("  get(1) evicted", c2.get(1), -1);
        check("  get(2) kept", c2.get(2), 2);
        check("  get(3)", c2.get(3), 3);

        System.out.println("case 3: edge cases, capacity 0 and capacity 1");
        LFUCache c3 = new LFUCache(0);
        c3.put(1, 1);
        check("  cap 0, get(1)", c3.get(1), -1);
        check("  cap 0, get(9)", c3.get(9), -1);

        LFUCache c4 = new LFUCache(1);
        c4.put(1, 1);
        c4.put(1, 10); // overwrite, not an insert: nothing is evicted
        check("  cap 1, get(1) overwritten", c4.get(1), 10);
        c4.put(2, 2);  // capacity 1 -> key 1 must go even though it has a high count
        check("  cap 1, get(1) evicted", c4.get(1), -1);
        check("  cap 1, get(2)", c4.get(2), 2);
    }
}
