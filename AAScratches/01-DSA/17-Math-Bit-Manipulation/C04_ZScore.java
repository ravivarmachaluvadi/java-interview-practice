/*
 * =====================================================================
 *  Maximum Z-Score (H-index shaped)                         Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of marks and a multiplier k, find the largest z such that
 *   at least z students scored at least z * k marks. If even z = 1 fails
 *   (nobody scored k or more), the answer is 0. This is the H-index with the
 *   threshold scaled by k.
 *
 * EXAMPLE
 *   marks = [45, 60, 70, 80, 90, 95], k = 10  ->  5
 *       z = 5 works: 5 students scored >= 50 (60, 70, 80, 90, 95)
 *       z = 6 fails: only 5 students scored >= 60
 *   marks = [5], k = 1            ->  1   single element, 5 >= 1
 *   marks = [1, 1, 1], k = 5      ->  0   nobody reaches 5, no z works
 *   marks = [100,100,100,100], k = 25 -> 4   every z up to n succeeds
 *
 * APPROACH  (binary search on the answer)
 *   1. Sort the marks ascending once.
 *   2. The candidate answers are 1..n, so binary search over that range.
 *   3. Test a candidate z with one array lookup: the z-th LARGEST mark is
 *      marks[n - z] in an ascending array. "At least z students scored
 *      >= z*k" is exactly "the z-th largest mark >= z*k".
 *   4. If the test passes, record z and search higher; otherwise search lower.
 *
 * KEY INSIGHT
 *   The predicate "z is achievable" is monotone: as z grows, the bar z*k rises
 *   while the z-th largest mark falls, so once it fails it fails forever. That
 *   true-true-...-true-false-...-false shape is the ONLY thing binary search on
 *   the answer needs - the array being sorted is a means, not the point.
 *   The counting test collapses to a single index because the z-th largest
 *   element being >= T is the same statement as "at least z elements >= T".
 *
 * COMPLEXITY
 *   Time  O(n log n)  the sort dominates; the search itself is O(log n)
 *   Space O(n)        one defensive copy so the caller's array is untouched
 *
 * INTERVIEW FOLLOW-UPS
 *   - Set k = 1 and you have LeetCode 274, H-Index. Solve it in O(n) with
 *     counting sort, since no mark above n can ever help.
 *   - The array arrives already sorted (LeetCode 275) - drop to O(log n) total.
 *   - Marks stream in one at a time; keep the answer updated after each.
 *   - Why can the answer never exceed n? Why is 0 the correct floor?
 *
 * Fixed: the original called Arrays.sort (which sorts ASCENDING) under a
 * comment claiming descending order, then read marks[mid - 1] as if it were
 * the mid-th largest. On the sample input that returned 6 instead of 5, and
 * it also broke the monotonicity binary search relies on. The z-th largest is
 * now read correctly as marks[n - z].
 *
 * RUN
 *   main() runs 4 cases (typical, single element, no valid z, all maximal),
 *   printing the binary-search answer, a brute-force cross-check, and the
 *   expected value on one line each.
 */

import java.util.Arrays;

class Solution {

    /** Binary search on the answer: largest z with at least z marks >= z * k. */
    public int ZScore(int[] marks, int k) {
        int n = marks.length;

        int[] sorted = marks.clone();  // do not mutate the caller's array
        Arrays.sort(sorted);           // ASCENDING

        int left = 1, right = n, ans = 0;
        while (left <= right) {
            int mid = left + (right - left) / 2;

            // In an ascending array the mid-th LARGEST value sits at index n - mid.
            // It is >= mid * k exactly when at least mid students cleared mid * k.
            if (sorted[n - mid] >= mid * k) {
                ans = mid;        // mid is achievable, try to beat it
                left = mid + 1;
            } else {
                right = mid - 1;  // mid is too ambitious
            }
        }
        return ans;
    }

    /** O(n^2) reference implementation, used only to cross-check the fast one. */
    public int zScoreBruteForce(int[] marks, int k) {
        int best = 0;
        for (int z = 1; z <= marks.length; z++) {
            int count = 0;
            for (int mark : marks) {
                if (mark >= z * k) {
                    count++;
                }
            }
            if (count >= z) {
                best = z;
            }
        }
        return best;
    }
}

class ZScore {

    private static void print(String label, int[] marks, int k, int expected) {
        Solution sol = new Solution();
        int fast = sol.ZScore(marks, k);
        int slow = sol.zScoreBruteForce(marks, k);
        System.out.println(label + ": binarySearch=" + fast + " bruteForce=" + slow
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical", new int[]{45, 60, 70, 80, 90, 95}, 10, 5);
        print("case 2 single element (edge)", new int[]{5}, 1, 1);
        print("case 3 no z works (edge)", new int[]{1, 1, 1}, 5, 0);
        print("case 4 every z works (tricky)", new int[]{100, 100, 100, 100}, 25, 4);
    }
}
