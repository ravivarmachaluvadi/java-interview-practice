/*
 * =====================================================================
 *  Random Pick with Weight                          LeetCode 528 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an array w of positive weights, implement pickIndex() so that index i is
 *   returned with probability w[i] / sum(w). pickIndex() is called many times, so the
 *   per-call cost matters more than the setup cost.
 *
 * EXAMPLE
 *   w = [1, 3, 4, 6]   sum = 14   prefix = [1, 4, 8, 14]
 *   random target in [1, 14]:  1 -> 0,  2..4 -> 1,  5..8 -> 2,  9..14 -> 3
 *   so index 3 is picked 6/14 of the time, index 0 only 1/14.
 *   w = [7] (single weight)  ->  always 0
 *
 * APPROACH  (prefix sums plus lower bound)
 *   1. Build prefix[i] = w[0] + ... + w[i]. It is strictly increasing because
 *      weights are positive, so it can be binary searched.
 *   2. Draw target uniformly from [1, totalSum].
 *   3. Lower bound: find the first index whose prefix[i] >= target. That index
 *      "owns" a slice of the number line whose width is exactly w[i].
 *
 * KEY INSIGHT
 *   Turn weights into intervals on a number line. Index i owns
 *   (prefix[i-1], prefix[i]], a slice of width w[i]; a uniform random point lands
 *   in it with probability w[i] / total. The array you binary search is often one
 *   you build yourself, not the input.
 *
 * COMPLEXITY
 *   Time  O(n) constructor, O(log n) per pickIndex   one lower-bound search per pick
 *   Space O(n)                                       the prefix array
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why nextInt(total) + 1 and not nextInt(total)? Slices are (prev, cur], so
 *     valid targets are 1..total; a target of 0 belongs to no slice.
 *   - What if weights can be updated? Fenwick tree gives O(log n) update and pick.
 *   - Alias method gives O(1) pick after O(n) setup; mention it, rarely coded live.
 *
 * RUN
 *   main() runs 3 cases: deterministic targets, a single-weight edge case, and a
 *   seeded 10 000-pick frequency check. Prints actual vs expected.
 */

import java.util.Arrays;
import java.util.Random;

class RandomPickWithWeight {
    private final int[] prefixSum;
    private final int totalSum;
    private final Random random;

    public RandomPickWithWeight(int[] w) {
        this(w, new Random());
    }

    // Seeded constructor so main() can reproduce the same sequence of picks.
    public RandomPickWithWeight(int[] w, Random random) {
        prefixSum = new int[w.length];
        int running = 0;
        for (int i = 0; i < w.length; i++) {
            running += w[i];
            prefixSum[i] = running;
        }
        totalSum = running;
        this.random = random;
    }

    public int pickIndex() {
        // target is uniform in [1, totalSum]; slice for index i is (prefix[i-1], prefix[i]]
        int target = random.nextInt(totalSum) + 1;
        return indexForTarget(target);
    }

    // Lower bound: first index whose prefix sum is >= target.
    int indexForTarget(int target) {
        int left = 0, right = prefixSum.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (prefixSum[mid] < target) {
                left = mid + 1;   // target lies to the right of mid's slice
            } else {
                right = mid;      // mid could be the answer, keep it
            }
        }
        return left;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: typical, deterministic targets against prefix [1, 4, 8, 14]
        RandomPickWithWeight typical = new RandomPickWithWeight(new int[]{1, 3, 4, 6});
        int[] targets = {1, 2, 4, 5, 8, 9, 14};
        int[] actual = new int[targets.length];
        for (int i = 0; i < targets.length; i++) {
            actual[i] = typical.indexForTarget(targets[i]);
        }
        print("case 1 targets " + Arrays.toString(targets), Arrays.toString(actual),
                "[0, 1, 1, 2, 2, 3, 3]");

        // case 2: edge, single weight always returns index 0
        RandomPickWithWeight single = new RandomPickWithWeight(new int[]{7}, new Random(1));
        boolean allZero = true;
        for (int i = 0; i < 20; i++) {
            allZero &= single.pickIndex() == 0;
        }
        print("case 2 single weight always index 0", allZero, true);

        // case 3: tricky, seeded frequency check: heaviest weight must be picked most often
        int[] w = {1, 3, 4, 6};
        RandomPickWithWeight seeded = new RandomPickWithWeight(w, new Random(42));
        int[] counts = new int[w.length];
        int picks = 10_000;
        for (int i = 0; i < picks; i++) {
            counts[seeded.pickIndex()]++;
        }
        boolean orderedLikeWeights =
                counts[3] > counts[2] && counts[2] > counts[1] && counts[1] > counts[0];
        print("case 3 counts " + Arrays.toString(counts) + " ordered like weights",
                orderedLikeWeights, true);
        // rough sanity: index 3 should get about 6/14 = 43% of picks
        boolean closeToRatio = Math.abs(counts[3] / (double) picks - 6.0 / 14) < 0.03;
        print("case 3 index 3 share within 3% of 6/14", closeToRatio, true);
    }
}
