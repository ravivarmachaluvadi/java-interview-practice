/**
 * Demonstrates shallow cloning of a two‑dimensional array in Java.
 *
 * The program creates a jagged int[][], prints each subarray,
 * clones the outer array with {@code arr.clone()}, and then
 * prints the cloned subarrays to show that only the top‑level
 * reference array is duplicated while the inner arrays remain shared.
 *
 * Approach:
 * 1. Initialize a jagged two‑dimensional int array.
 * 2. Print each row using Arrays.toString().
 * 3. Clone the outer array with {@code clone()} (shallow copy).
 * 4. Print rows of the cloned array to illustrate shared references.
 *
 * Time Complexity: O(n) where n is the number of subarrays,
 *   since printing and cloning iterate over each row once.
 * Space Complexity: O(1) additional space beyond the new outer array
 *   reference; inner arrays are not duplicated.
 */
import java.util.Arrays;

class ArrayCloneExample {
    public static void main(String[] args) {
        int[][] arr = {{8, 9}, {7, 8, 6}};

        for (int[] ints : arr)
            System.out.println(Arrays.toString(ints));

        int[][] cloned = arr.clone();

        for (int[] ints : cloned)
            System.out.println(Arrays.toString(ints));
    }
}