/**
 * Problem: Find all elements that appear in three sorted integer arrays, without duplicates.
 *
 * Approach: Use a three‑pointer technique to traverse the arrays simultaneously.
 * At each step compare the current values; if they match, record it (ensuring no
 * repetition) and advance all pointers. Otherwise advance the pointer pointing
 * to the smallest value to catch up.
 *
 * Time Complexity: O(n + m + p), where n, m, p are lengths of the three arrays.
 * Space Complexity: O(k), where k is the number of common elements (output size).
 */
import java.util.*;
class IntersectionOfThreeSortedArrays {
    public static void main(String[] args) {
        int[] A = {1, 5, 10, 20, 30};
        int[] B = {5, 13, 15, 20};
        int[] C = {5, 20};
        List<Integer> ans = getCommonElements(A, B, C);
        System.out.println(ans);
    }

    // with no duplicates three pointer solution
    private static List<Integer> getCommonElements(int[] a, int[] b, int[] c) {
        int i = 0, j = 0, k = 0;
        List<Integer> ans = new ArrayList<>();
        // Using Integer to allow null as initial value
        Integer prevVal = null;
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
                i++;
            } else if (b[j] < c[k]) {
                j++;
            } else {
                k++;
            }
        }
        return ans;
    }
}
