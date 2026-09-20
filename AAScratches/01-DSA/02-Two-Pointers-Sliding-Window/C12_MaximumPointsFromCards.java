/*
 * =====================================================================
 *  Maximum Points You Can Obtain from Cards          LeetCode 1423 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   cardPoints is a row of cards. You must take exactly k cards, each one from either the
 *   front or the back of the row. Return the maximum total you can collect.
 *   1 <= k <= n.
 *
 * EXAMPLE
 *   [1, 2, 3, 4, 5, 6, 1], k = 3    ->  12   take 1 from the front, 6 and 1 from the back
 *   [2, 2, 2],             k = 2    ->  4    any two cards
 *   [9, 7, 7, 9, 7, 7, 9], k = 2    ->  18   both 9s at the ends
 *   [1, 79, 80, 1, 1, 1, 200, 1], k = 8  ->  364   k == n, take everything
 *   [5],                   k = 1    ->  5    single card
 *
 * APPROACH  (fixed window on the complement)
 *   Two equivalent views, both O(k) or O(n) with a fixed-size window:
 *
 *   maxScore  (author's cyclic window): the k cards you take are always "some suffix
 *   plus some prefix", which is one contiguous block of length k if you imagine the array
 *   wrapped into a circle.
 *   1. Start with the window covering the last k cards: sum of [n-k, n).
 *   2. Slide it k times, one step at a time, using index i % n to wrap around the end:
 *      add cardPoints[i % n], drop cardPoints[i - k].
 *   3. The best of those k + 1 window sums is the answer.
 *
 *   maxScoreViaComplement (second method): the n - k cards you LEAVE form one contiguous
 *   block in the middle. Minimise that block with an ordinary fixed window of size n - k
 *   and subtract it from the total.
 *
 * KEY INSIGHT
 *   "Take from both ends" is not a choice problem, it is a window problem. Every legal
 *   selection is a contiguous block on the circular array (or, equivalently, a contiguous
 *   hole in the linear array). Recognising that collapses a 2^k search into one pass.
 *
 * Fixed: the original expected comment for [9,7,7,9,7,7,9], k=2 said 30; the correct
 * answer is 18 (max of any two end cards), which the code already produced.
 *
 * COMPLEXITY
 *   Time  O(k) for the cyclic window (k + 1 sums), O(n) for the complement window
 *   Space O(1)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Explain why the complement view is exactly "minimum sum window of size n - k".
 *   - What if k > n is allowed? Not by the constraints; guard or clamp if asked.
 *   - Prefix-sum version: precompute prefix and suffix sums, try each split in O(1).
 *   - Related: Maximum Average Subarray I (643) is the plain fixed window this is built on.
 *
 * RUN
 *   main() runs 5 cases through both methods and prints actual vs expected.
 */
class MaximumPointsFromCards {

    /** Author's approach: slide a window of size k over the array treated as a circle. */
    public static int maxScore(int[] cardPoints, int k) {
        int n = cardPoints.length;

        // start with the back k cards: window [n - k, n)
        int windowSum = 0;
        for (int i = n - k; i < n; i++) {
            windowSum += cardPoints[i];
        }
        int best = windowSum;

        // slide k times; i % n wraps the window around onto the front of the array
        for (int i = n; i < n + k; i++) {
            windowSum += cardPoints[i % n] - cardPoints[i - k];
            best = Math.max(best, windowSum);
        }
        return best;
    }

    /** Alternative: total minus the minimum-sum contiguous block of n - k cards left behind. */
    public static int maxScoreViaComplement(int[] cardPoints, int k) {
        int n = cardPoints.length;
        int leaveOut = n - k;

        int total = 0;
        for (int p : cardPoints) total += p;
        if (leaveOut == 0) return total; // taking every card

        int windowSum = 0;
        for (int i = 0; i < leaveOut; i++) windowSum += cardPoints[i];
        int minLeftBehind = windowSum;

        for (int i = leaveOut; i < n; i++) {
            windowSum += cardPoints[i] - cardPoints[i - leaveOut];
            minLeftBehind = Math.min(minLeftBehind, windowSum);
        }
        return total - minLeftBehind;
    }

    private static void run(String label, int[] cards, int k, int expected) {
        System.out.println(label + " cyclic: " + maxScore(cards, k)
                + "  complement: " + maxScoreViaComplement(cards, k)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 [1,2,3,4,5,6,1] k=3", new int[]{1, 2, 3, 4, 5, 6, 1}, 3, 12);
        run("case 2 [2,2,2] k=2", new int[]{2, 2, 2}, 2, 4);
        run("case 3 [9,7,7,9,7,7,9] k=2", new int[]{9, 7, 7, 9, 7, 7, 9}, 2, 18);
        run("case 4 k == n", new int[]{1, 79, 80, 1, 1, 1, 200, 1}, 8, 364);
        run("case 5 single [5] k=1", new int[]{5}, 1, 5);
    }
}
