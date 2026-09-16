/**
 * Problem: Sort an array of jobs where each job is represented as [id, deadline, profit].
 * The goal is to order the jobs primarily by decreasing profit and secondarily by
 * increasing deadline when profits are equal.
 *
 * Approach: Use Arrays.sort with a custom comparator that compares the third element
 * (profit) in descending order; if profits tie, compare the second element
 * (deadline) in ascending order. This yields the desired ordering in O(n log n).
 *
 * Time Complexity: O(n log n) due to sorting.
 * Space Complexity: O(1) auxiliary space (in-place sort). */
import java.util.Arrays;

class JobSequencing {
    public static void main(String[] args) {
        int[][] arr = {{1, 4, 50}, {2, 1, 10}, {3, 1, 40}, {4, 1, 30}};

        // Sort the array in descending order based on
        // the third element and then the second element
        Arrays.sort(arr, (a, b) -> {
            if (a[2] != b[2]) {
                // Sort by the third element in descending order
                return Integer.compare(b[2], a[2]);
            } else {
                // If the third elements are equal, sort
                // by the second element in ascending order
                return Integer.compare(a[1], b[1]);
            }
        });
    }
}
