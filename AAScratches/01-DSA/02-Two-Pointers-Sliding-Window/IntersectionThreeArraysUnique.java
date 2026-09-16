/**
 * Problem: Given three sorted integer arrays, return a list of all values that appear in
 * every array, with each value appearing only once in the result.
 *
 * Approach: Use three pointers to traverse the arrays simultaneously. When the current
 * elements are equal, add the value to the result and advance all pointers past any
 * duplicates. If they differ, advance the pointer(s) pointing to the smallest element,
 * skipping over duplicates as well. This ensures each array is scanned only once.
 *
 * Time Complexity: O(n + m + p), where n, m, p are the lengths of A, B, and C respectively.
 * Space Complexity: O(k), where k is the number of unique common elements (output size).
 */
import java.util.*;
class IntersectionThreeArraysUnique {
    public static List<Integer> intersectUnique(int[] A, int[] B, int[] C) {
        List<Integer> result = new ArrayList<>();
        int i = 0, j = 0, k = 0;
        while (i < A.length && j < B.length && k < C.length) {
            if (A[i] == B[j] && B[j] == C[k]) {
                result.add(A[i]);
                int val = A[i];
                // Skip duplicates in all three
                while (i < A.length && A[i] == val) i++;
                while (j < B.length && B[j] == val) j++;
                while (k < C.length && C[k] == val) k++;
            } else {
                int minVal = Math.min(A[i], Math.min(B[j], C[k]));
                if (A[i] == minVal) {
                    int val = A[i];
                    // make i,j,k less than constraint in place
                    while (i < A.length && A[i] == val) i++;
                }
                if (B[j] == minVal) {
                    int val = B[j];
                    while (j < B.length && B[j] == val) j++;
                }
                if (C[k] == minVal) {
                    int val = C[k];
                    while (k < C.length && C[k] == val) k++;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[] A = {1, 2, 2, 3, 5};
        int[] B = {2, 2, 3, 4};
        int[] C = {2, 2, 3, 5};

        List<Integer> ans = intersectUnique(A, B, C);
        System.out.println(ans); // [2, 3]
    }
}
M