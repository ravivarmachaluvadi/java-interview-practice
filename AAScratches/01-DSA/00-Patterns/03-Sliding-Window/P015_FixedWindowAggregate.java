/*
 * =====================================================================
 *  P015 Fixed Sliding Window: Aggregate   Canonical LC 643 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 643, Maximum Average Subarray I)
 *   Given nums and k, find the contiguous subarray of length exactly k with the largest
 *   average, and return that average.
 *
 * EXAMPLE
 *   [1, 12, -5, -6, 50, 3], k = 4  ->  12.75     (12 - 5 - 6 + 50) / 4
 *   [5],                    k = 1  ->  5.0
 *
 * RECOGNIZE WHEN
 *   - The window LENGTH is given: "subarray of size k", "k consecutive", "every k minutes".
 *   - You need an aggregate of each window (sum, count of vowels, count of 1s, ...).
 *   - "Take k from the two ends": the part you do NOT take is one fixed window.
 *   Not this if: the length is free and a condition decides it -> P017_VariableWindowLongest
 *   or P018_VariableWindowShortest; you need the max / min INSIDE each window (not a sum)
 *   -> P042_MonotonicDeque; you need an exact multiset match -> P016_FixedWindowFrequencyMatch.
 *
 * TEMPLATE
 *   window = aggregate of a[0..k-1]
 *   best = window
 *   for i in k..n-1:
 *       window += a[i]          // element entering on the right
 *       window -= a[i - k]      // element leaving on the left
 *       best = max(best, window)
 *
 * APPROACH
 *   1. Sum the first k values.
 *   2. Slide one step at a time: add the new right value, subtract the one that fell off.
 *   3. Track the best sum; divide by k once at the end.
 *
 * KEY INSIGHT
 *   Two neighbouring windows share k - 1 elements, so each slide costs O(1) instead of
 *   re-summing k values. Compare SUMS, not averages: dividing by the same k changes nothing.
 *
 * COMPLEXITY
 *   Time O(n), space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 1052 Grumpy Bookstore Owner   base = always-happy customers; slide a window
 *                                            over the grumpy-minute customers you can win back
 *   [coded] LC 1456 Max Vowels in Substring  the aggregate is "number of vowels"
 *   [coded] LC 1423 Max Points from Cards    k cards from the ends = total - the minimum
 *                                            window of length n - k in the middle
 *           LC 2090 K Radius Averages        window of 2k + 1 centred at i
 *           LC 1343 Subarrays Avg >= Threshold  count windows with sum >= k * threshold
 *           LC 2379 Min Recolors for K Blacks   min count of 'W' in any window of size k
 *           LC 2461 Max Sum Distinct Subarray   fixed window + frequency map of size k
 *           LC 346  Moving Average from Stream  a queue holds the window
 *           LC 239  Sliding Window Maximum      max is not invertible -> P042_MonotonicDeque
 *
 * PITFALLS
 *   - The first window must be complete before you record a best.
 *   - Initialise best from the first window, not from 0 (all values may be negative).
 *   - LC 1423: k can equal n; the middle window is then empty.
 *
 * DEEP DIVE
 *   A08_MaximumAverageSubarray, C09_MaximumPointsFromCards
 *   (02-Two-Pointers-Sliding-Window)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class FixedWindowAggregate {

    // Canonical LC 643.
    static double findMaxAverage(int[] nums, int k) {
        int window = 0;
        for (int i = 0; i < k; i++) {
            window += nums[i];
        }
        int best = window;
        for (int i = k; i < nums.length; i++) {
            window += nums[i] - nums[i - k];
            best = Math.max(best, window);
        }
        return (double) best / k;
    }

    // LC 1052: the technique keeps the owner calm for `minutes` in a row; pick the window
    // that wins back the most customers who would otherwise be unhappy.
    static int maxSatisfied(int[] customers, int[] grumpy, int minutes) {
        int base = 0;
        for (int i = 0; i < customers.length; i++) {
            if (grumpy[i] == 0) {
                base += customers[i];
            }
        }
        int gain = 0;
        int bestGain = 0;
        for (int i = 0; i < customers.length; i++) {
            gain += grumpy[i] * customers[i];                       // entering
            if (i >= minutes) {
                gain -= grumpy[i - minutes] * customers[i - minutes]; // leaving
            }
            bestGain = Math.max(bestGain, gain);
        }
        return base + bestGain;
    }

    // LC 1456.
    static int maxVowels(String s, int k) {
        int window = 0;
        int best = 0;
        for (int i = 0; i < s.length(); i++) {
            if (isVowel(s.charAt(i))) {
                window++;
            }
            if (i >= k && isVowel(s.charAt(i - k))) {
                window--;
            }
            best = Math.max(best, window);
        }
        return best;
    }

    private static boolean isVowel(char c) {
        return "aeiou".indexOf(c) >= 0;
    }

    // LC 1423: whatever you leave behind is one contiguous window of length n - k.
    static int maxScore(int[] cardPoints, int k) {
        int n = cardPoints.length;
        int total = 0;
        for (int x : cardPoints) {
            total += x;
        }
        int keep = n - k;
        int window = 0;
        for (int i = 0; i < keep; i++) {
            window += cardPoints[i];
        }
        int minWindow = window;
        for (int i = keep; i < n; i++) {
            window += cardPoints[i] - cardPoints[i - keep];
            minWindow = Math.min(minWindow, window);
        }
        return total - minWindow;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 643 k=4", findMaxAverage(new int[]{1, 12, -5, -6, 50, 3}, 4), 12.75);
        check("LC 643 [5] k=1", findMaxAverage(new int[]{5}, 1), 5.0);
        check("LC 643 all negative k=2", findMaxAverage(new int[]{-3, -1, -2}, 2), -1.5);

        int[] customers = {1, 0, 1, 2, 1, 1, 7, 5};
        int[] grumpy = {0, 1, 0, 1, 0, 1, 0, 1};
        check("LC 1052 minutes=3", maxSatisfied(customers, grumpy, 3), 16);
        check("LC 1052 single minute", maxSatisfied(new int[]{1}, new int[]{0}, 1), 1);
        check("LC 1052 [3,2,5,10,20,5] minutes=3",
                maxSatisfied(new int[]{3, 2, 5, 10, 20, 5}, new int[]{1, 1, 1, 0, 0, 1}, 3), 40);

        check("LC 1456 abciiidef k=3", maxVowels("abciiidef", 3), 3);
        check("LC 1456 aeiou k=2", maxVowels("aeiou", 2), 2);
        check("LC 1456 leetcode k=3", maxVowels("leetcode", 3), 2);

        check("LC 1423 [1,2,3,4,5,6,1] k=3", maxScore(new int[]{1, 2, 3, 4, 5, 6, 1}, 3), 12);
        check("LC 1423 [2,2,2] k=2", maxScore(new int[]{2, 2, 2}, 2), 4);
        check("LC 1423 k=n takes all", maxScore(new int[]{9, 7, 7, 9, 7, 7, 9}, 7), 55);
        check("LC 1423 [1,79,80,1,1,1,200,1] k=3",
                maxScore(new int[]{1, 79, 80, 1, 1, 1, 200, 1}, 3), 202);
    }
}
