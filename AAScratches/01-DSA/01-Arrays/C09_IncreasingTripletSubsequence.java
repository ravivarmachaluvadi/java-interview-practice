/**
 * LeetCode 334 — Increasing Triplet Subsequence.
 *
 * <p>Returns {@code true} if there exist indices {@code i < j < k} such that
 * {@code nums[i] < nums[j] < nums[k]}.
 *
 * <p><b>Approach.</b> Track the smallest value seen so far ({@code first}) and the
 * smallest value that has a smaller element before it ({@code second}). Any element
 * exceeding {@code second} completes a triplet.
 *
 * <p><b>Why the stale {@code first} is harmless.</b> {@code first} may be overwritten
 * by a value occurring <i>after</i> {@code second}, so the pair is not always a valid
 * subsequence. Correctness rests on {@code second} alone: it is assigned only in the
 * branch where {@code num > first}, and that {@code first} came from a strictly
 * earlier index. So a finite {@code second} always carries the guarantee that some
 * smaller element preceded it. Lowering {@code first} only makes future pairs easier
 * to form; it never invalidates that guarantee.
 *
 * <p><b>Why {@code <=} and not {@code <}.</b> Both comparisons must be non-strict to
 * reject duplicates. With strict {@code <}, the input {@code [1, 1, 2]} would assign
 * {@code second = 1}, and {@code 2} would then report a triplet that is not strictly
 * increasing.
 *
 * <pre>
 * Input:  nums = [2, 1, 5, 0, 4, 6]
 * Output: true                        // 0 &lt; 4 &lt; 6
 *
 * Input:  nums = [5, 4, 3, 2, 1]
 * Output: false
 * </pre>
 *
 * <p>O(n) time, O(1) space, single pass, input left unmodified. The trade-off is that
 * the actual indices are not recoverable — the sentinels report existence only.
 *
 * @see <a href="https://leetcode.com/problems/increasing-triplet-subsequence/">LeetCode 334</a>
 */
public static boolean increasingTriplet(int[] nums) {
    int first = Integer.MAX_VALUE;    // smallest value seen so far
    int second = Integer.MAX_VALUE;   // smallest value with something smaller before it

    for (int num : nums) {
        if (num <= first) {
            first = num;
        } else if (num <= second) {
            second = num;             // num > first, so a smaller element precedes it
        } else {
            return true;              // num > second > (something earlier)
        }
    }
    return false;
}

void main() {
    int[][] cases = {
            {1, 2, 3, 4, 5},              // true  — trivially increasing
            {5, 4, 3, 2, 1},              // false — strictly decreasing
            {2, 1, 5, 0, 4, 6},           // true  — 0 < 4 < 6
            {5, 6, 1, 7},                 // true  — 5 < 6 < 7, first goes stale
            {20, 100, 10, 12, 5, 13},     // true  — 10 < 12 < 13
            {1, 1, 1},                    // false — duplicates are not increasing
            {1, 1, 2, 2, 3},              // true  — 1 < 2 < 3
            {1, 5, 2},                    // false — only a pair
            {1, 2},                       // false — too short
            {}                            // false — empty
    };

    for (int[] nums : cases) {
        System.out.printf("%-30s -> %b%n", Arrays.toString(nums), increasingTriplet(nums));
    }
}