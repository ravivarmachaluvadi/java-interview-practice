/*
 * =====================================================================
 *  Find Median from Data Stream                 LeetCode 295 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Design a structure that accepts integers one at a time (addNum) and can
 *   return the median of everything seen so far (findMedian) at any moment.
 *   Up to 5e4 calls, so findMedian must not re-sort the data.
 *
 * EXAMPLE
 *   add 1, add 2        ->  findMedian = 1.5
 *   add 3               ->  findMedian = 2.0
 *   add 5, 15, 1, 3     ->  median after each add: 5.0, 10.0, 5.0, 4.0
 *   add -2, -2, -2      ->  median after each add: -2.0, -2.0, -2.0
 *
 * APPROACH  (two balanced heaps)
 *   1. lower = max-heap holding the smaller half; upper = min-heap holding the larger half.
 *   2. addNum: if lower is empty or num < lower.top, it belongs in the lower half,
 *      otherwise in the upper half.
 *   3. Rebalance so that lower.size is equal to upper.size or one more:
 *      lower two bigger -> move lower.top to upper; upper bigger -> move upper.top to lower.
 *   4. findMedian: equal sizes -> average of both tops; otherwise lower.top.
 *
 * KEY INSIGHT
 *   The median only depends on the boundary between the lower and upper halves,
 *   and a heap gives O(1) access to exactly one boundary element. Two heaps
 *   facing each other (max-heap below, min-heap above) keep BOTH boundary
 *   elements on top. The invariant to recite: every element of lower <= every
 *   element of upper, and the sizes differ by at most one, with lower allowed
 *   to be the larger. Insert into the natural side, then fix sizes.
 *
 * COMPLEXITY
 *   Time  O(log n) per addNum (at most two heap operations), O(1) per findMedian
 *   Space O(n)     every element lives in one of the two heaps
 *
 * INTERVIEW FOLLOW-UPS
 *   - All numbers in [0, 100]: a counting array of 101 buckets makes addNum O(1).
 *   - 99% of numbers in [0, 100]: buckets for the middle, two heaps for the outliers.
 *   - Sliding Window Median (LC 480): same two heaps plus lazy deletion of departing values.
 *   - Why not a sorted list? Insert is O(n). A balanced BST works but is far more code.
 *
 * RUN
 *   main() runs 3 cases (LeetCode example, mixed order, all equal negatives) and
 *   prints the median after each add vs expected.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

class ImportantMedianOfStream {
    private final PriorityQueue<Integer> lower = new PriorityQueue<>(Collections.reverseOrder());
    private final PriorityQueue<Integer> upper = new PriorityQueue<>();

    public void addNum(int num) {
        // 1. drop it on the side it naturally belongs to
        if (lower.isEmpty() || num < lower.peek()) {
            lower.offer(num);
        } else {
            upper.offer(num);
        }
        // 2. restore the size invariant: lower.size == upper.size or upper.size + 1
        if (lower.size() - upper.size() > 1) {
            upper.offer(lower.poll());
        } else if (lower.size() < upper.size()) {
            lower.offer(upper.poll());
        }
    }

    public double findMedian() {
        if (lower.size() == upper.size()) {
            return (lower.peek() + upper.peek()) / 2.0;
        }
        return lower.peek(); // lower is the larger heap when sizes differ
    }

    /** Feed the numbers in order and collect the median after each one. */
    private static List<Double> mediansAfterEachAdd(int[] nums) {
        ImportantMedianOfStream stream = new ImportantMedianOfStream();
        List<Double> medians = new ArrayList<>();
        for (int n : nums) {
            stream.addNum(n);
            medians.add(stream.findMedian());
        }
        return medians;
    }

    private static void print(String label, int[] nums, String expected) {
        System.out.println(label + ": " + mediansAfterEachAdd(nums) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 leetcode", new int[]{1, 2, 3}, "[1.0, 1.5, 2.0]");
        print("case 2 mixed order", new int[]{5, 15, 1, 3}, "[5.0, 10.0, 5.0, 4.0]");
        print("case 3 all equal negative", new int[]{-2, -2, -2}, "[-2.0, -2.0, -2.0]");
    }
}
