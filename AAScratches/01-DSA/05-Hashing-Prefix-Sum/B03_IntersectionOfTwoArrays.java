/*
 * =====================================================================
 *  Intersection of Two Sorted Arrays              LeetCode 349 (sorted variant) | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given two integer arrays that are already sorted ascending and contain no duplicates,
 *   return the values present in both, in ascending order. Either array may be empty.
 *   (LeetCode 349 hands you unsorted arrays with duplicates; this file is the sorted,
 *   distinct-element version where two pointers beat hashing.)
 *
 * EXAMPLE
 *   A = [1, 2, 3, 4, 5], B = [2, 3, 5, 6]  ->  [2, 3, 5]
 *   A = [],              B = [1, 2]        ->  []          one side empty, loop never runs
 *   A = [1, 3, 5],       B = [2, 4, 6]     ->  []          interleaved, nothing ever matches
 *
 * APPROACH  (two pointers on sorted input)
 *   1. Start i = 0 on A and j = 0 on B.
 *   2. If A[i] < B[j], A[i] can never match anything at or after j: advance i.
 *   3. If B[j] < A[i], the mirror argument: advance j.
 *   4. Equal: record the value, advance both.
 *   5. Stop when either pointer runs off its array; leftovers cannot match.
 *
 * KEY INSIGHT
 *   Sorted order lets you discard the smaller head with certainty, so every step retires
 *   one element and the walk is linear with O(1) extra space. Reach for a HashSet only when
 *   the input is unsorted and sorting it would cost more than the O(n) memory.
 *
 * COMPLEXITY
 *   Time  O(n + m)  each pointer only moves forward
 *   Space O(1)      beyond the output list
 *
 * INTERVIEW FOLLOW-UPS
 *   - Unsorted input: HashSet of the smaller array, scan the larger; O(n + m) time.
 *   - Duplicates must be kept: count map, or two pointers emitting on every match (see B04).
 *   - One array is tiny and the other is huge and sorted: binary-search each small element.
 *   - Union instead of intersection: same walk, but emit on every branch.
 *
 * RUN
 *   main() runs 3 cases (typical, empty side, disjoint) and prints actual vs expected.
 */
import java.util.ArrayList;
import java.util.List;

class IntersectionOfTwoArrays {

    /** Two-pointer merge walk; assumes both arrays are sorted ascending with distinct values. */
    public static List<Integer> findArrayIntersection(int[] a, int[] b) {
        List<Integer> common = new ArrayList<>();
        int i = 0, j = 0;
        while (i < a.length && j < b.length) {
            if (a[i] < b[j]) {
                i++;                    // a[i] is smaller than everything left in b
            } else if (b[j] < a[i]) {
                j++;                    // b[j] is smaller than everything left in a
            } else {
                common.add(a[i]);
                i++;
                j++;
            }
        }
        return common;
    }

    private static void print(String label, int[] a, int[] b, String expected) {
        System.out.println(label + ": " + findArrayIntersection(a, b) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (typical) ", new int[]{1, 2, 3, 4, 5}, new int[]{2, 3, 5, 6}, "[2, 3, 5]");
        print("case 2 (empty A) ", new int[]{}, new int[]{1, 2}, "[]");
        print("case 3 (disjoint)", new int[]{1, 3, 5}, new int[]{2, 4, 6}, "[]");
    }
}
