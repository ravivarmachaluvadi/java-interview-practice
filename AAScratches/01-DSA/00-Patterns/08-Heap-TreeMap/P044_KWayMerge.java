/*
 * =====================================================================
 *  P044 Heap: K-Way Merge   Canonical LC 23 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 23, Merge k Sorted Lists)
 *   Merge k sorted linked lists into one sorted list.
 *
 * EXAMPLE
 *   [[1,4,5],[1,3,4],[2,6]]  ->  [1,1,2,3,4,4,5,6]
 *   []                       ->  []
 *   [[]]                     ->  []
 *
 * RECOGNIZE WHEN
 *   - Several SORTED sequences (lists, rows of a matrix, "pairs from two sorted arrays")
 *     and you need the global order, the k-th smallest, or a range touching all of them.
 *   - The next candidate always comes from the "frontier": the current head of each
 *     sequence.
 *   Not this if: only two sequences -> P014_WalkTwoSequences or P033_DummyHeadMerge;
 *   unsorted input with "k largest" -> P043_TopK.
 *
 * TEMPLATE
 *   heap = min-heap of (value, which sequence, position in it)
 *   push the first element of every sequence
 *   while heap not empty (and you still need output):
 *       (v, s, i) = heap.poll(); emit v
 *       if sequence s has an element after i: push (next value, s, i + 1)
 *
 * APPROACH
 *   1. Put the head of every non-empty list in a min-heap.
 *   2. Repeatedly take the smallest head, append it, and push that list's next node.
 *
 * KEY INSIGHT
 *   The overall smallest remaining element is always one of the k current heads, so the
 *   heap only ever needs k entries. Each element passes through the heap once:
 *   O(N log k) instead of O(N log N) for "concatenate and sort".
 *
 * COMPLEXITY
 *   Time O(N log k) for N total elements, space O(k).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 378  Kth Smallest in Matrix   each row is a list; pop k times
 *   [coded] LC 373  K Pairs Smallest Sums    sequence i = pairs (a[i], b[0..]); push
 *                                            (i, 0) for every i, advance j on pop
 *   [coded] LC 632  Smallest Range k Lists   heap of current heads + the current max;
 *                                            the range is [heap.min, max]; stop when a
 *                                            list runs out
 *           LC 786  Kth Smallest Fraction    sequences a[i] / a[j] for each i
 *           LC 1439 Kth Smallest Sum of Rows merge rows two at a time, keep k smallest
 *           External sort                    the same merge over sorted files on disk
 *
 * PITFALLS
 *   - Skip empty lists when seeding the heap.
 *   - Store the sequence id and index with the value, or you cannot advance.
 *   - LC 373: seeding all (i, 0) pairs is O(k log k) if you seed only min(k, n) of them.
 *
 * DEEP DIVE
 *   D02_MergeKLists (06-Linked-List)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

class KWayMerge {

    static class ListNode {
        int val;
        ListNode next;

        ListNode(int val) {
            this.val = val;
        }

        static ListNode of(int... vals) {
            ListNode dummy = new ListNode(0);
            ListNode cur = dummy;
            for (int v : vals) {
                cur.next = new ListNode(v);
                cur = cur.next;
            }
            return dummy.next;
        }
    }

    static String show(ListNode head) {
        StringBuilder sb = new StringBuilder("[");
        for (ListNode n = head; n != null; n = n.next) {
            sb.append(n.val).append(n.next == null ? "" : ",");
        }
        return sb.append("]").toString();
    }

    // Canonical LC 23.
    static ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<ListNode> heap = new PriorityQueue<>((a, b) -> Integer.compare(a.val, b.val));
        for (ListNode head : lists) {
            if (head != null) {
                heap.offer(head);
            }
        }
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while (!heap.isEmpty()) {
            ListNode smallest = heap.poll();
            tail.next = smallest;
            tail = smallest;
            if (smallest.next != null) {
                heap.offer(smallest.next);
            }
        }
        return dummy.next;
    }

    // LC 378: heap entries are {value, row, col}.
    static int kthSmallest(int[][] m, int k) {
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        for (int r = 0; r < m.length; r++) {
            heap.offer(new int[]{m[r][0], r, 0});
        }
        for (int i = 1; i < k; i++) {
            int[] e = heap.poll();
            if (e[2] + 1 < m[e[1]].length) {
                heap.offer(new int[]{m[e[1]][e[2] + 1], e[1], e[2] + 1});
            }
        }
        return heap.peek()[0];
    }

    // LC 373: heap entries are {sum, i, j}.
    static List<List<Integer>> kSmallestPairs(int[] a, int[] b, int k) {
        PriorityQueue<int[]> heap = new PriorityQueue<>((x, y) -> Integer.compare(x[0], y[0]));
        for (int i = 0; i < Math.min(a.length, k); i++) {
            heap.offer(new int[]{a[i] + b[0], i, 0});
        }
        List<List<Integer>> out = new ArrayList<>();
        while (!heap.isEmpty() && out.size() < k) {
            int[] e = heap.poll();
            out.add(Arrays.asList(a[e[1]], b[e[2]]));
            if (e[2] + 1 < b.length) {
                heap.offer(new int[]{a[e[1]] + b[e[2] + 1], e[1], e[2] + 1});
            }
        }
        return out;
    }

    // LC 632: smallest [lo, hi] that contains at least one number from every list.
    static int[] smallestRange(List<List<Integer>> nums) {
        PriorityQueue<int[]> heap = new PriorityQueue<>((x, y) -> Integer.compare(x[0], y[0]));
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < nums.size(); i++) {
            int v = nums.get(i).get(0);
            heap.offer(new int[]{v, i, 0});
            max = Math.max(max, v);
        }
        int[] best = {heap.peek()[0], max};
        while (true) {
            int[] e = heap.poll();                 // the current minimum
            if (max - e[0] < best[1] - best[0]) {
                best = new int[]{e[0], max};
            }
            List<Integer> list = nums.get(e[1]);
            if (e[2] + 1 == list.size()) {
                return best;                       // this list is used up
            }
            int next = list.get(e[2] + 1);
            max = Math.max(max, next);
            heap.offer(new int[]{next, e[1], e[2] + 1});
        }
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        ListNode[] three = {ListNode.of(1, 4, 5), ListNode.of(1, 3, 4), ListNode.of(2, 6)};
        check("LC 23 three lists", show(mergeKLists(three)), "[1,1,2,3,4,4,5,6]");
        check("LC 23 no lists", show(mergeKLists(new ListNode[]{})), "[]");
        check("LC 23 one empty list", show(mergeKLists(new ListNode[]{null})), "[]");

        check("LC 378 k=8", kthSmallest(new int[][]{{1, 5, 9}, {10, 11, 13}, {12, 13, 15}}, 8), 13);
        check("LC 378 k=1", kthSmallest(new int[][]{{-5}}, 1), -5);

        check("LC 373 k=3", kSmallestPairs(new int[]{1, 7, 11}, new int[]{2, 4, 6}, 3),
                "[[1, 2], [1, 4], [1, 6]]");
        check("LC 373 k=2 duplicates", kSmallestPairs(new int[]{1, 1, 2}, new int[]{1, 2, 3}, 2),
                "[[1, 1], [1, 1]]");

        List<List<Integer>> lists = List.of(List.of(4, 10, 15, 24, 26), List.of(0, 9, 12, 20),
                List.of(5, 18, 22, 30));
        check("LC 632 three lists", Arrays.toString(smallestRange(lists)), "[20, 24]");
        List<List<Integer>> same = List.of(List.of(1, 2, 3), List.of(1, 2, 3), List.of(1, 2, 3));
        check("LC 632 identical lists", Arrays.toString(smallestRange(same)), "[1, 1]");
    }
}
