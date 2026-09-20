/*
 * =====================================================================
 *  Merge k Sorted Lists                             LeetCode 23 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of k linked lists, each sorted ascending, merge them into one sorted
 *   list and return its head. k can be 0, individual lists can be null, and the total
 *   number of nodes N can be much larger than k.
 *
 * EXAMPLE
 *   [[1,4,5], [1,3,4], [2,6]]  ->  1 1 2 3 4 4 5 6
 *   []                         ->  (empty)
 *   [null, [1], null]          ->  1                 (null entries must be skipped)
 *   [[-3,7], [-5], [0,0]]      ->  -5 -3 0 0 7        (negatives and duplicates)
 *
 * APPROACH  (min-heap over the k current heads)
 *   1. Push the head of every non-null list into a PriorityQueue ordered by val. The heap
 *      never holds more than k nodes: one "cursor" per list.
 *   2. Poll the smallest node, append it to a dummy-headed result, and if that node has a
 *      next, push the next into the heap. That list's cursor has simply advanced by one.
 *   3. When the heap is empty every node has been consumed; return dummy.next.
 *
 * KEY INSIGHT
 *   You only ever need to compare the FRONT of each list, so keep exactly k candidates in
 *   a heap and let it pick the minimum in O(log k). A04_MergeTwoSortedLists is this same
 *   loop with k = 2 and an if/else instead of a heap. Naively merging lists one after
 *   another into a growing result is O(N * k) because early nodes get rescanned k times.
 *
 * COMPLEXITY
 *   Time  O(N log k)  each of the N nodes is pushed and polled once on a heap of size k
 *   Space O(k)        the heap; the result reuses existing nodes
 *
 * INTERVIEW FOLLOW-UPS
 *   - Divide and conquer: pairwise-merge lists like merge sort's merge step, also
 *     O(N log k) but O(1) extra space (iterative) or O(log k) stack. Shown below as
 *     mergeKListsDivideAndConquer and run from main.
 *   - Why is "merge them one by one" O(N k)? Sum of growing prefix lengths.
 *   - Same shape as "k-way merge" of sorted files / external sort, and of "kth smallest in
 *     a sorted matrix" (LC 378): heap of row cursors.
 *
 * RUN
 *   main() runs 4 cases (typical, empty array, null entries, negatives with duplicates)
 *   through both methods and prints actual vs expected.
 */
import java.util.Comparator;
import java.util.PriorityQueue;

class MergeKLists {

    public static void main(String[] args) {
        MergeKLists solver = new MergeKLists();
        int[][][] cases = {
                {{1, 4, 5}, {1, 3, 4}, {2, 6}},
                {},
                {null, {1}, null},
                {{-3, 7}, {-5}, {0, 0}},
        };
        String[] expected = {"1 1 2 3 4 4 5 6", "", "1", "-5 -3 0 0 7"};

        for (int i = 0; i < cases.length; i++) {
            // build fresh input for each method because merging relinks the nodes
            ListNode byHeap = solver.mergeKLists(buildLists(cases[i]));
            ListNode byDivide = solver.mergeKListsDivideAndConquer(buildLists(cases[i]));
            print("case " + (i + 1) + " heap", byHeap, expected[i]);
            print("case " + (i + 1) + " d&c ", byDivide, expected[i]);
        }
    }

    /** Approach 1: min-heap of the current head of each list. O(N log k) time, O(k) space. */
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;

        PriorityQueue<ListNode> minHeap =
                new PriorityQueue<>(Comparator.comparingInt(node -> node.val));
        for (ListNode head : lists) {
            if (head != null) minHeap.offer(head);   // empty lists contribute no cursor
        }

        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while (!minHeap.isEmpty()) {
            ListNode smallest = minHeap.poll();
            tail.next = smallest;
            tail = smallest;
            // advance that list's cursor: its next node becomes a candidate
            if (smallest.next != null) minHeap.offer(smallest.next);
        }
        return dummy.next;
    }

    /** Approach 2: pairwise merge, halving the number of lists each round. O(N log k) time. */
    public ListNode mergeKListsDivideAndConquer(ListNode[] lists) {
        if (lists == null || lists.length == 0) return null;

        int count = lists.length;
        while (count > 1) {
            // merge list i with its mirror (count-1-i); only the first half survives the round
            for (int i = 0; i < count / 2; i++) {
                lists[i] = mergeTwo(lists[i], lists[count - 1 - i]);
            }
            count = (count + 1) / 2;   // the odd middle list, if any, carries over untouched
        }
        return lists[0];
    }

    /** The classic dummy-headed two-list merge. */
    private ListNode mergeTwo(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;
        while (a != null && b != null) {
            if (a.val <= b.val) {
                tail.next = a;
                a = a.next;
            } else {
                tail.next = b;
                b = b.next;
            }
            tail = tail.next;
        }
        tail.next = (a != null) ? a : b;
        return dummy.next;
    }

    // ---- helpers for main ------------------------------------------------

    /** Turns int[][] into ListNode[]; a null row becomes a null list. */
    private static ListNode[] buildLists(int[][] rows) {
        ListNode[] lists = new ListNode[rows.length];
        for (int r = 0; r < rows.length; r++) {
            if (rows[r] == null) continue;
            ListNode dummy = new ListNode(0);
            ListNode current = dummy;
            for (int v : rows[r]) {
                current.next = new ListNode(v);
                current = current.next;
            }
            lists[r] = dummy.next;
        }
        return lists;
    }

    private static String toString(ListNode head) {
        StringBuilder sb = new StringBuilder();
        for (ListNode n = head; n != null; n = n.next) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(n.val);
        }
        return sb.toString();
    }

    private static void print(String label, ListNode actual, String expected) {
        System.out.println(label + ": [" + toString(actual) + "]   expected [" + expected + "]");
    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}
