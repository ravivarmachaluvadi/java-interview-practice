/*
 * =====================================================================
 *  Union of Two Sorted Arrays                 GeeksforGeeks | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given two sorted integer arrays (each may contain duplicates), return their
 *   union as a sorted list with no duplicates. Either array may be empty.
 *
 * EXAMPLE
 *   a = [1..10], b = [2, 3, 4, 4, 5, 11, 12]  ->  [1, 2, ..., 10, 11, 12]
 *   a = [],      b = [1, 1, 2]                ->  [1, 2]      (empty side)
 *   a = [1, 2],  b = [1, 2]                   ->  [1, 2]      (identical)
 *
 * APPROACH  (Merge walk, advance the smaller)
 *   1. One pointer per array, both start at 0.
 *   2. While both have elements: take the smaller head (ties: take from a),
 *      append it only if it differs from the last value already appended,
 *      then advance that pointer.
 *   3. When one array runs out, drain the other with the same "append if new"
 *      rule.
 *
 * KEY INSIGHT
 *   Because both inputs are sorted, the merged stream is sorted too, so a
 *   duplicate can only ever be equal to the LAST value appended. One
 *   comparison against the tail replaces a HashSet. This exact loop shape
 *   (compare heads, advance the smaller, drain the leftover) is reused by
 *   every intersection / difference / merge problem that follows.
 *
 * COMPLEXITY
 *   Time  O(n + m)   each pointer moves forward at most once per element
 *   Space O(n + m)   the output list; O(1) extra beyond the answer
 *
 * INTERVIEW FOLLOW-UPS
 *   - Intersection instead of union: append only when heads are equal.
 *   - Unsorted inputs: sort first (O(n log n)) or use a HashSet (O(n) space).
 *   - k sorted arrays: replace the head comparison with a min-heap of heads.
 *
 * Fixed: the drain loops read list.get(size - 1) without an empty check,
 *        so an empty first array threw IndexOutOfBoundsException.
 *
 * RUN
 *   main() runs 3 cases (typical, empty side, identical arrays) and prints
 *   actual vs expected.
 */
import java.util.ArrayList;
import java.util.List;

class UnionOfArrays {

    static List<Integer> findUnion(int[] a, int[] b) {
        List<Integer> union = new ArrayList<>();
        int i = 0, j = 0;

        // Walk both arrays; always consume the smaller head.
        while (i < a.length && j < b.length) {
            if (a[i] <= b[j]) {
                addIfNew(union, a[i]);
                i++;
            } else {
                addIfNew(union, b[j]);
                j++;
            }
        }

        // Drain whichever array still has elements.
        while (i < a.length) addIfNew(union, a[i++]);
        while (j < b.length) addIfNew(union, b[j++]);

        return union;
    }

    // Sorted input means a duplicate can only equal the last appended value.
    private static void addIfNew(List<Integer> union, int value) {
        if (union.isEmpty() || union.get(union.size() - 1) != value) {
            union.add(value);
        }
    }

    public static void main(String[] args) {
        int[] a1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] b1 = {2, 3, 4, 4, 5, 11, 12};
        System.out.println("case 1 (typical):    " + findUnion(a1, b1)
                + "   expected [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]");

        int[] a2 = {};
        int[] b2 = {1, 1, 2};
        System.out.println("case 2 (empty a):    " + findUnion(a2, b2)
                + "   expected [1, 2]");

        int[] a3 = {1, 2};
        int[] b3 = {1, 2};
        System.out.println("case 3 (identical):  " + findUnion(a3, b3)
                + "   expected [1, 2]");
    }
}
