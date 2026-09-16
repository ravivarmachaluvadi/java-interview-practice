/**
 * Demonstrates the difference between prefix (++i) and postfix (i++) increment operators
 * when used in assignments. Two integer variables i and j are initialized to zero.
 * arr[0] receives the value of ++i, which increments i before assignment, yielding 1.
 * arr[1] receives the value of j++, which assigns j's current value (0) then increments it,
 * resulting in [1, 0]. The array is printed using Arrays.toString().
 *
 * Approach:
 *   - Initialize two counters i and j to zero.
 *   - Assign arr[0] with ++i (prefix increment).
 *   - Assign arr[1] with j++ (postfix increment).
 *   - Print the resulting array.
 *
 * Time Complexity: O(1) – constant time operations.
 * Space Complexity: O(1) – only a fixed-size array and two integers are used. 
 */
import java.util.Arrays;

class Tricky10 {
    public static void main(String[] args) {
        int[] arr = new int[2];
        int i = 0, j = 0;
        arr[0] = ++i;
        arr[1] = j++;
        System.out.println(Arrays.toString(arr)); // [1, 0]
    }
}
