/*
 * =====================================================================
 *  P103 Design: Combine Structures for O(1) Operations   Canonical LC 380 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 380, Insert Delete GetRandom O(1))
 *   A set with insert(val), remove(val) (both return whether they changed the set) and
 *   getRandom() returning each present element with equal probability, all in average O(1).
 *
 * EXAMPLE
 *   insert(1) true, remove(2) false, insert(2) true, getRandom() 1 or 2, remove(1) true,
 *   insert(2) false, getRandom() 2
 *
 * RECOGNIZE WHEN
 *   - A list of required operations, each "in O(1)", that no single collection supports:
 *     random access + delete, min + push/pop, FIFO from stacks, uniform sampling of a stream.
 *   - The trick is to keep TWO structures in sync, or to store extra info with each item.
 *   Not this if: eviction by recency / frequency -> P102_LruLfuCache; values change over
 *   time with lookups by timestamp -> P104_VersionedTimeMap.
 *
 * TEMPLATE
 *   random + delete:  list of values + map value -> index; delete = move the LAST value into
 *                     the hole, update its index, drop the last slot
 *   min stack:        push (value, min so far) pairs
 *   queue by stacks:  in-stack for pushes; out-stack for pops, refilled only when empty
 *   reservoir:        k-th item replaces the kept one with probability 1 / k
 *
 * APPROACH
 *   1. The ArrayList gives O(1) random access; the HashMap gives O(1) "where is val?".
 *   2. Removing from the middle of a list is O(n), so swap the target with the last element
 *      and remove the last one instead.
 *
 * KEY INSIGHT
 *   When the ORDER of elements does not matter, any deletion can be turned into "delete the
 *   last element" by a swap. Pairing each structure with an index map (or storing a running
 *   aggregate next to each element) is the general recipe for O(1) designs.
 *
 * COMPLEXITY
 *   All operations O(1) average (queue: O(1) amortised); space O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 155  Min Stack                each entry stores the minimum below it too
 *   [coded] LC 232  Queue Using Stacks       move in -> out only when out is empty
 *   [coded] LC 382  Linked List Random Node  reservoir sampling: keep node k with prob 1/k
 *           LC 381  RandomizedCollection     duplicates: map value -> SET of indices
 *           LC 716  Max Stack                TreeMap + doubly linked list (popMax in log n)
 *           LC 225  Stack Using Queues       rotate the queue after each push
 *           LC 528  Random Pick with Weight  -> P001_PrefixSumRangeQuery
 *
 * PITFALLS
 *   - LC 380 remove: update the moved element's index BEFORE removing the last slot, and
 *     handle "removing the last element itself".
 *   - Min stack: store the min per entry, or a separate stack pushed on <= (not <).
 *   - Queue by stacks: refilling out on every pop breaks the amortised O(1).
 *
 * DEEP DIVE
 *   C03_InsertDeleteGetRandomOof1, A02_MinStack, A01_RandomPickIndex
 *   (19-Design-Data-Structures), A04_QueueUsingStacks (07-Stack-Queue-Monotonic)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

class ConstantTimeCombos {

    // Canonical LC 380.
    static class RandomizedSet {
        private final List<Integer> values = new ArrayList<>();
        private final Map<Integer, Integer> indexOf = new HashMap<>();
        private final Random random = new Random(7);

        boolean insert(int val) {
            if (indexOf.containsKey(val)) {
                return false;
            }
            indexOf.put(val, values.size());
            values.add(val);
            return true;
        }

        boolean remove(int val) {
            Integer i = indexOf.remove(val);
            if (i == null) {
                return false;
            }
            int last = values.remove(values.size() - 1);
            if (i < values.size()) {
                values.set(i, last);               // move the last value into the hole
                indexOf.put(last, i);
            }
            return true;
        }

        int getRandom() {
            return values.get(random.nextInt(values.size()));
        }
    }

    // LC 155: each entry remembers the minimum at the time it was pushed.
    static class MinStack {
        private final Deque<int[]> stack = new ArrayDeque<>();   // {value, min so far}

        void push(int val) {
            int min = stack.isEmpty() ? val : Math.min(val, stack.peek()[1]);
            stack.push(new int[]{val, min});
        }

        void pop() {
            stack.pop();
        }

        int top() {
            return stack.peek()[0];
        }

        int getMin() {
            return stack.peek()[1];
        }
    }

    // LC 232.
    static class MyQueue {
        private final Deque<Integer> in = new ArrayDeque<>();
        private final Deque<Integer> out = new ArrayDeque<>();

        void push(int x) {
            in.push(x);
        }

        int pop() {
            peek();
            return out.pop();
        }

        int peek() {
            if (out.isEmpty()) {
                while (!in.isEmpty()) {
                    out.push(in.pop());            // reverses the order once
                }
            }
            return out.peek();
        }

        boolean empty() {
            return in.isEmpty() && out.isEmpty();
        }
    }

    // LC 382: one pass over a list of unknown length, uniform choice (reservoir of size 1).
    static int reservoirPick(List<Integer> stream, Random random) {
        int chosen = 0;
        int seen = 0;
        for (int x : stream) {
            seen++;
            if (random.nextInt(seen) == 0) {
                chosen = x;                        // keep the seen-th item with prob 1 / seen
            }
        }
        return chosen;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        RandomizedSet set = new RandomizedSet();
        List<Boolean> ops = new ArrayList<>();
        ops.add(set.insert(1));
        ops.add(set.remove(2));
        ops.add(set.insert(2));
        int r = set.getRandom();
        ops.add(r == 1 || r == 2);
        ops.add(set.remove(1));
        ops.add(set.insert(2));
        check("LC 380 operations", ops, "[true, false, true, true, true, false]");
        check("LC 380 only 2 left", set.getRandom(), 2);
        set.insert(5);
        set.insert(9);
        set.remove(2);                             // removes from the middle via swap
        check("LC 380 after middle removal",
                set.remove(9) && set.remove(5) && !set.remove(2), true);

        MinStack ms = new MinStack();
        ms.push(-2);
        ms.push(0);
        ms.push(-3);
        int min1 = ms.getMin();
        ms.pop();
        check("LC 155 min, top, min", min1 + "," + ms.top() + "," + ms.getMin(), "-3,0,-2");

        MyQueue q = new MyQueue();
        q.push(1);
        q.push(2);
        int peek = q.peek();
        int pop = q.pop();
        check("LC 232 peek, pop, empty", peek + "," + pop + "," + q.empty(), "1,1,false");
        q.push(3);
        check("LC 232 order kept after refill",
                q.pop() + "," + q.pop() + "," + q.empty(), "2,3,true");

        Random random = new Random(42);
        int[] hits = new int[3];
        for (int i = 0; i < 30000; i++) {
            hits[reservoirPick(List.of(10, 20, 30), random) / 10 - 1]++;
        }
        boolean uniform = true;
        for (int h : hits) {
            uniform &= Math.abs(h - 10000) < 500;  // each about a third of the draws
        }
        check("LC 382 roughly uniform", uniform, true);
    }
}
