/*
 * =====================================================================
 *  Intersection of Three Sorted Arrays                LeetCode 1213 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given three integer arrays, each sorted in non-decreasing order, return the values that
 *   appear in all three, in ascending order. Inputs may contain repeated values; each common
 *   value must be reported once.
 *
 * EXAMPLE
 *   a = [1,5,10,20,30], b = [5,13,15,20], c = [5,20]   ->  [5, 20]
 *   a = [1,2,2,3,5],    b = [2,2,3,4],    c = [2,2,3,5]  ->  [2, 3]   (duplicates reported once)
 *   a = [1,2,3],        b = [4,5],        c = [1,4]      ->  []       (nothing common)
 *   a = [],             b = [1],          c = [1]        ->  []       (an empty input ends it)
 *
 * APPROACH  (three-pointer merge walk)
 *   1. Keep one index per array, all starting at 0.
 *   2. If all three current values are equal, record the value (unless it equals the last one
 *      recorded) and advance all three.
 *   3. Otherwise advance a pointer that is provably not in the answer: if a[i] < b[j] then a[i]
 *      can never match anything b still holds, so i++; else if b[j] < c[k], j++; else k++.
 *   4. Stop as soon as any array is exhausted.
 *
 *   A second method, getCommonElementsSkipRuns, dedupes differently: after every advance it
 *   skips the whole run of equal values in that array, so no "previous value" is needed.
 *
 * KEY INSIGHT
 *   This is the two-array merge walk with one more pointer. The rule "advance the smallest"
 *   works because a value smaller than another array's current head can never appear later in
 *   that other array (it is sorted). The chained comparison need not find the true minimum;
 *   it only needs to pick something that is strictly less than some other head.
 *
 * COMPLEXITY
 *   Time  O(n + m + p)  each pointer only ever moves forward
 *   Space O(k)          k = number of common values (output only)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Arrays are NOT sorted: use a HashSet per array, or count map with value == 3, O(n + m + p)
 *   - K sorted arrays instead of three: min-heap of heads, or reduce pairwise
 *   - Keep duplicates (multiset intersection): drop the dedupe and record every triple match
 *   - Why is prevVal an Integer and not an int? (null lets us tell "no value yet" from a real 0)
 *
 * RUN
 *   main() runs 4 cases (typical, duplicates, no overlap, empty input) through both methods
 *   and prints actual vs expected.
 */
import java.util.ArrayList;
import java.util.List;

class IntersectionOfThreeSortedArrays {

    /** Dedupes by remembering the last value that was added to the answer. */
    static List<Integer> getCommonElements(int[] a, int[] b, int[] c) {
        int i = 0, j = 0, k = 0;
        List<Integer> ans = new ArrayList<>();
        Integer prevVal = null; // Integer so null can mean "nothing recorded yet"

        while (i < a.length && j < b.length && k < c.length) {
            if (a[i] == b[j] && b[j] == c[k]) {
                if (prevVal == null || a[i] != prevVal) {
                    ans.add(a[i]);
                    prevVal = a[i];
                }
                i++;
                j++;
                k++;
            } else if (a[i] < b[j]) {
                i++; // a[i] is below b's head, so no later b can equal it
            } else if (b[j] < c[k]) {
                j++; // b[j] is below c's head
            } else {
                k++; // c[k] is the smallest (or tied with a, but below b)
            }
        }
        return ans;
    }

    /** Same walk, but every advance skips the entire run of equal values in that array. */
    static List<Integer> getCommonElementsSkipRuns(int[] a, int[] b, int[] c) {
        int i = 0, j = 0, k = 0;
        List<Integer> ans = new ArrayList<>();

        while (i < a.length && j < b.length && k < c.length) {
            if (a[i] == b[j] && b[j] == c[k]) {
                int val = a[i];
                ans.add(val);
                i = skipRun(a, i, val);
                j = skipRun(b, j, val);
                k = skipRun(c, k, val);
            } else if (a[i] < b[j]) {
                i = skipRun(a, i, a[i]);
            } else if (b[j] < c[k]) {
                j = skipRun(b, j, b[j]);
            } else {
                k = skipRun(c, k, c[k]);
            }
        }
        return ans;
    }

    /** Returns the first index at or after 'from' whose value differs from 'val'. */
    private static int skipRun(int[] arr, int from, int val) {
        while (from < arr.length && arr[from] == val) {
            from++;
        }
        return from;
    }

    private static void run(String label, int[] a, int[] b, int[] c, String expected) {
        System.out.println(label + " prevVal : " + getCommonElements(a, b, c)
                + "   expected " + expected);
        System.out.println(label + " skipRun : " + getCommonElementsSkipRuns(a, b, c)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 typical   ", new int[]{1, 5, 10, 20, 30}, new int[]{5, 13, 15, 20},
                new int[]{5, 20}, "[5, 20]");
        run("case 2 duplicates", new int[]{1, 2, 2, 3, 5}, new int[]{2, 2, 3, 4},
                new int[]{2, 2, 3, 5}, "[2, 3]");
        run("case 3 no overlap", new int[]{1, 2, 3}, new int[]{4, 5},
                new int[]{1, 4}, "[]");
        run("case 4 empty     ", new int[]{}, new int[]{1},
                new int[]{1}, "[]");
    }
}
