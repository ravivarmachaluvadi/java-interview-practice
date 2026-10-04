/*
 * =====================================================================
 *  P102 Design: LRU and LFU Cache   Canonical LC 146 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 146, LRU Cache)
 *   A cache with a fixed capacity: get(key) returns the value or -1; put(key, value) inserts
 *   or updates, and when full evicts the LEAST RECENTLY USED key. Both in O(1).
 *
 * EXAMPLE
 *   capacity 2: put(1,1) put(2,2) get(1)=1 put(3,3) get(2)=-1 put(4,4) get(1)=-1
 *               get(3)=3 get(4)=4
 *
 * RECOGNIZE WHEN
 *   - "O(1) get and put" plus an ORDER to evict by (recency, frequency, insertion time).
 *   - Lookup by key AND removal from the middle of an ordering.
 *   Not this if: eviction is by smallest / largest value -> a heap or TreeMap
 *   (P047_TreeMapFloorCeiling); no eviction -> a plain HashMap.
 *
 * TEMPLATE
 *   map: key -> node; doubly linked list ordered by recency (head = newest, tail = oldest)
 *   get(k):    node = map[k]; if absent -1; move node to the front; return node.value
 *   put(k, v): if present: update + move to front
 *              else: if full: remove the tail node and its map entry; insert at the front
 *   sentinels: dummy head and tail remove every null check
 *   LFU: map key -> node, map freq -> its own recency list, and minFreq
 *
 * APPROACH
 *   1. The HashMap finds a node in O(1); the doubly linked list removes and re-inserts it
 *      in O(1) because each node knows both neighbours.
 *   2. The tail is always the least recently used: evict it when full.
 *
 * KEY INSIGHT
 *   Neither structure alone is enough: a map has no order, a list has no O(1) lookup. The
 *   map stores POINTERS into the list, so "find" and "reorder" are both O(1). In Java,
 *   LinkedHashMap(accessOrder = true) is exactly this pair, ready made.
 *
 * COMPLEXITY
 *   O(1) per get / put; space O(capacity).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LRU with LinkedHashMap           accessOrder = true + removeEldestEntry
 *   [coded] LC 460  LFU Cache                freq -> LinkedHashSet of keys; minFreq tells
 *                                            which bucket to evict from (its oldest key)
 *           LC 432  All O(1) Data Structure  buckets of keys by count in a linked list
 *           LC 1756 MRU Queue                order statistics; sqrt decomposition or BIT
 *           Time-based expiry (TTL cache)    the same list ordered by expiry time
 *
 * PITFALLS
 *   - put on an EXISTING key must also refresh its recency.
 *   - Remove the evicted key from the map too, not only from the list.
 *   - LFU: a put of a new key resets minFreq to 1; ties evict the least recent.
 *
 * DEEP DIVE
 *   C05_LRUCache, D01_LFUCache (19-Design-Data-Structures)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

class LruLfuCache {

    // Canonical LC 146 with a hand-made doubly linked list.
    static class LRUCache {
        static class Node {
            int key;
            int value;
            Node prev;
            Node next;

            Node(int key, int value) {
                this.key = key;
                this.value = value;
            }
        }

        private final int capacity;
        private final Map<Integer, Node> map = new HashMap<>();
        private final Node head = new Node(0, 0);      // newest after head
        private final Node tail = new Node(0, 0);      // oldest before tail

        LRUCache(int capacity) {
            this.capacity = capacity;
            head.next = tail;
            tail.prev = head;
        }

        int get(int key) {
            Node n = map.get(key);
            if (n == null) {
                return -1;
            }
            unlink(n);
            addFront(n);
            return n.value;
        }

        void put(int key, int value) {
            Node n = map.get(key);
            if (n != null) {
                n.value = value;
                unlink(n);
                addFront(n);
                return;
            }
            if (map.size() == capacity) {
                Node oldest = tail.prev;
                unlink(oldest);
                map.remove(oldest.key);
            }
            n = new Node(key, value);
            map.put(key, n);
            addFront(n);
        }

        private void unlink(Node n) {
            n.prev.next = n.next;
            n.next.prev = n.prev;
        }

        private void addFront(Node n) {
            n.next = head.next;
            n.prev = head;
            head.next.prev = n;
            head.next = n;
        }
    }

    // LRU with the JDK: LinkedHashMap in access order evicts its eldest entry.
    static class LinkedLru extends LinkedHashMap<Integer, Integer> {
        private final int capacity;

        LinkedLru(int capacity) {
            super(16, 0.75f, true);                // true = order by access, not insertion
            this.capacity = capacity;
        }

        int read(int key) {
            return getOrDefault(key, -1);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
            return size() > capacity;
        }
    }

    // LC 460.
    static class LFUCache {
        private final int capacity;
        private final Map<Integer, Integer> value = new HashMap<>();
        private final Map<Integer, Integer> freq = new HashMap<>();
        private final Map<Integer, LinkedHashSet<Integer>> byFreq = new HashMap<>();
        private int minFreq;

        LFUCache(int capacity) {
            this.capacity = capacity;
        }

        int get(int key) {
            if (!value.containsKey(key)) {
                return -1;
            }
            touch(key);
            return value.get(key);
        }

        void put(int key, int v) {
            if (capacity == 0) {
                return;
            }
            if (value.containsKey(key)) {
                value.put(key, v);
                touch(key);
                return;
            }
            if (value.size() == capacity) {
                Iterator<Integer> oldest = byFreq.get(minFreq).iterator();
                int evict = oldest.next();         // least frequent, then least recent
                oldest.remove();
                value.remove(evict);
                freq.remove(evict);
            }
            value.put(key, v);
            freq.put(key, 1);
            byFreq.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(key);
            minFreq = 1;
        }

        // Move key from its frequency bucket to the next one.
        private void touch(int key) {
            int f = freq.get(key);
            byFreq.get(f).remove(key);
            if (f == minFreq && byFreq.get(f).isEmpty()) {
                minFreq++;
            }
            freq.put(key, f + 1);
            byFreq.computeIfAbsent(f + 1, k -> new LinkedHashSet<>()).add(key);
        }
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        LRUCache lru = new LRUCache(2);
        List<Integer> got = new ArrayList<>();
        lru.put(1, 1);
        lru.put(2, 2);
        got.add(lru.get(1));
        lru.put(3, 3);                             // evicts 2
        got.add(lru.get(2));
        lru.put(4, 4);                             // evicts 1
        got.add(lru.get(1));
        got.add(lru.get(3));
        got.add(lru.get(4));
        check("LC 146 hand-made list", got, "[1, -1, -1, 3, 4]");

        LRUCache refresh = new LRUCache(2);
        refresh.put(1, 1);
        refresh.put(2, 2);
        refresh.put(1, 10);                        // update refreshes key 1
        refresh.put(3, 3);                         // so 2 is the one evicted
        check("LC 146 put refreshes recency", refresh.get(2) + "," + refresh.get(1), "-1,10");

        LinkedLru jdk = new LinkedLru(2);
        List<Integer> got2 = new ArrayList<>();
        jdk.put(1, 1);
        jdk.put(2, 2);
        got2.add(jdk.read(1));
        jdk.put(3, 3);
        got2.add(jdk.read(2));
        jdk.put(4, 4);
        got2.add(jdk.read(1));
        got2.add(jdk.read(3));
        got2.add(jdk.read(4));
        check("LC 146 LinkedHashMap", got2, "[1, -1, -1, 3, 4]");

        LFUCache lfu = new LFUCache(2);
        List<Integer> got3 = new ArrayList<>();
        lfu.put(1, 1);
        lfu.put(2, 2);
        got3.add(lfu.get(1));
        lfu.put(3, 3);                             // evicts 2 (used least)
        got3.add(lfu.get(2));
        got3.add(lfu.get(3));
        lfu.put(4, 4);                             // 1 and 3 tie on count; 1 is older
        got3.add(lfu.get(1));
        got3.add(lfu.get(3));
        got3.add(lfu.get(4));
        check("LC 460 six reads", got3, "[1, -1, 3, -1, 3, 4]");
    }
}
