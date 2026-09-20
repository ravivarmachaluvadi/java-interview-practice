/*
 * =====================================================================
 *  Maximum Frequency of an Element After Performing Operations I   LeetCode 3346 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given nums, an int k and numOperations. Exactly numOperations times you pick an index
 *   not picked before and add any value in [-k, k] to it. Return the largest frequency any
 *   single value can have afterwards. 1 <= n <= 10^5, 0 <= k <= 10^5,
 *   0 <= numOperations <= n.
 *
 * EXAMPLE
 *   nums = [1, 4, 5],       k = 1, ops = 2  ->  2   (4 -> 4, 5 -> 4)
 *   nums = [1, 4, 5],       k = 2, ops = 2  ->  2   (target 3 is reachable by all, but ops = 2)
 *   nums = [5, 11, 20, 20], k = 5, ops = 1  ->  2   (the two 20s already; 11 cannot reach 20)
 *   nums = [5, 5, 5, 1, 9], k = 1, ops = 1  ->  3   (the existing 5s, no operation helps)
 *
 * APPROACH  (difference-array sweep over candidate targets)
 *   1. count[x] = how many times x already occurs in nums.
 *   2. Each x can reach any target in [x - k, x + k]. Record that interval in a sorted
 *      difference map: +1 at x - k, -1 at x + k + 1. Also register x itself as a key with
 *      delta 0 so every existing value is evaluated as a target.
 *   3. Sweep the keys in order, keeping a running sum reachable = number of x whose
 *      interval covers the current target t.
 *   4. For target t: already = count[t] need no operation; the others need one each, but
 *      only numOperations are available. Candidate = already + min(ops, reachable - already).
 *   5. Return the best candidate.
 *
 * KEY INSIGHT
 *   The window view ("which sorted elements fit within 2k of each other") works, but the
 *   cleaner mental model is an interval sweep: every element votes for a range of targets
 *   and the answer is the best-voted target, adjusted for elements that are already there
 *   for free. The running sum changes only at interval endpoints, so it is enough to test
 *   the endpoints plus the original values. Recognise this shape: "each element can become
 *   any value in a range" = difference array over the ranges.
 *
 * COMPLEXITY
 *   Time  O(n log n)  the TreeMap holds up to 3n keys
 *   Space O(n)        the count map and the difference map
 *
 * INTERVIEW FOLLOW-UPS
 *   - Sort + sliding window alternative: for each target, count elements in [t-k, t+k].
 *   - Version II (LC 3347): k and values up to 10^9, same sweep, targets must be endpoints.
 *   - Why is it enough to test only the endpoints and the original values?
 *   - What changes if the same index may be picked more than once?
 *
 * Fixed: the original swept only the interval endpoints (x - k and x + k + 1), so a target
 * equal to an existing value was never evaluated and its free "already" count was lost.
 * On LeetCode example 2, [5, 11, 20, 20], k = 5, ops = 1, it returned 1 instead of 2.
 *
 * RUN
 *   main() runs 4 cases (two LeetCode examples, the k = 2 variant, and the case that only
 *   works once existing values are candidate targets) and prints actual vs expected.
 */
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class MaximumFrequencyOfAnElementAfterPerformingOperationsI {

    public static int maxFrequency(int[] nums, int k, int numOperations) {
        if (nums == null || nums.length == 0)
            return 0;

        Map<Integer, Integer> count = new HashMap<>();
        for (int x : nums)
            count.merge(x, 1, Integer::sum);

        // Difference map: +1 where x's reachable interval starts, -1 just after it ends.
        TreeMap<Integer, Integer> diff = new TreeMap<>();
        for (int x : nums) {
            diff.merge(x - k, 1, Integer::sum);
            diff.merge(x + k + 1, -1, Integer::sum);
            diff.merge(x, 0, Integer::sum); // make x itself a candidate target
        }

        int best = 0;
        int reachable = 0; // running sum: elements whose interval covers the current target
        for (Map.Entry<Integer, Integer> e : diff.entrySet()) {
            int target = e.getKey();
            reachable += e.getValue();

            int already = count.getOrDefault(target, 0);   // no operation needed
            int needOperation = reachable - already;        // one operation each
            best = Math.max(best, already + Math.min(numOperations, needOperation));
        }
        return best;
    }

    private static void print(String label, int actual, int expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 LC example 1   ", maxFrequency(new int[]{1, 4, 5}, 1, 2), 2);
        print("case 2 k = 2 variant  ", maxFrequency(new int[]{1, 4, 5}, 2, 2), 2);
        print("case 3 LC example 2   ", maxFrequency(new int[]{5, 11, 20, 20}, 5, 1), 2);
        print("case 4 existing value ", maxFrequency(new int[]{5, 5, 5, 1, 9}, 1, 1), 3);
    }
}
