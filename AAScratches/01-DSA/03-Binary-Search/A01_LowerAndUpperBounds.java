/*
 * =====================================================================
 *  Lower Bound and Upper Bound          (building block | Easy)   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a sorted int array and a target, return two indices.
 *   lowerBound = the first index i with arr[i] >= target.
 *   upperBound = the first index i with arr[i] >  target.
 *   If no such element exists the answer is arr.length (the "insert at end" position).
 *   Together they give the half-open range [lowerBound, upperBound) of all copies of target,
 *   so upperBound - lowerBound is the count of target in the array.
 *
 * EXAMPLE
 *   arr = [1, 2, 2, 2, 3, 4, 5], target = 2  ->  lower 1, upper 4   (2 occupies indices 1..3)
 *   arr = [1, 2, 2, 2, 3, 4, 5], target = 9  ->  lower 7, upper 7   (past the end, count 0)
 *   arr = [1, 3, 5],             target = 4  ->  lower 2, upper 2   (absent: both point at 5)
 *   arr = [],                    target = 1  ->  lower 0, upper 0
 *
 * APPROACH  (binary search with a remembered candidate)
 *   1. Start with ans = arr.length. That is the correct answer if nothing qualifies.
 *   2. Look at mid. If arr[mid] satisfies the predicate (>= target for lower, > target for
 *      upper), mid is a valid answer: record it in ans, then search LEFT for a smaller one.
 *   3. Otherwise mid is too small; search RIGHT.
 *   4. When low crosses high, ans holds the smallest qualifying index.
 *   The two methods differ only in the comparison operator (>= versus >).
 *
 * KEY INSIGHT
 *   The predicate "arr[i] >= target" is false...false, true...true over a sorted array.
 *   Binary search finds the first true. Keep the best candidate seen so far and shrink the
 *   window toward it; never return -1 from inside the loop. Several files in this folder
 *   (first/last occurrence, floor/ceil, Koko, ship capacity) are this template with a
 *   different predicate; the rotated-array and two-array-median files use a different
 *   binary-search shape.
 *
 * COMPLEXITY
 *   Time  O(log n)  each search halves the window
 *   Space O(1)      three ints
 *
 * INTERVIEW FOLLOW-UPS
 *   - Count of target: upperBound - lowerBound. Zero means absent.
 *   - First and last occurrence (LC 34): lower and upper - 1, after checking the count is > 0.
 *   - Search insert position (LC 35): exactly lowerBound.
 *   - Why low + (high - low) / 2 instead of (low + high) / 2: avoids int overflow at large n.
 *
 * RUN
 *   main() runs 4 cases (typical, absent target, past-the-end, empty array) and prints
 *   actual vs expected.
 */
class AImportantLowerAndUpperBounds {

    /** First index i with arr[i] >= target, or arr.length if none. */
    public static int findLowerBound(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        int ans = arr.length; // if every element is smaller, target belongs at the end

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] >= target) {
                ans = mid;      // mid qualifies; remember it
                high = mid - 1; // but a smaller qualifying index may exist on the left
            } else {
                low = mid + 1;  // mid is too small; nothing on the left can qualify
            }
        }
        return ans;
    }

    /** First index i with arr[i] > target, or arr.length if none. */
    public static int findUpperBound(int[] arr, int target) {
        int low = 0, high = arr.length - 1;
        int ans = arr.length;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] > target) {   // the only line that differs from lower bound
                ans = mid;
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return ans;
    }

    private static void print(String label, int[] arr, int target, String expected) {
        int lower = findLowerBound(arr, target);
        int upper = findUpperBound(arr, target);
        String actual = "lower " + lower + ", upper " + upper + ", count " + (upper - lower);
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 2, 2, 3, 4, 5};
        print("case 1 typical      ", nums, 2, "lower 1, upper 4, count 3");
        print("case 2 absent middle", new int[]{1, 3, 5}, 4, "lower 2, upper 2, count 0");
        print("case 3 past the end ", nums, 9, "lower 7, upper 7, count 0");
        print("case 4 empty array  ", new int[]{}, 1, "lower 0, upper 0, count 0");
    }
}
