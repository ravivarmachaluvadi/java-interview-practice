/**
 * Problem: Split a given string into an array of substrings using a specified delimiter.
 *
 * Approach: Convert the delimiter to a String and use Java's built-in {@code split} method
 * to divide the input string. The resulting array is then printed for verification.
 *
 * Time Complexity: O(n), where n is the length of the input string, since each character is examined once.
 * Space Complexity: O(k + m), where k is the number of substrings and m is the total length of those substrings,
 * due to storage of the resulting array and its elements. */
import java.util.Arrays;

class DelimiterSplit {
    public static void main(String[] args) {
        String input = "apple,banana,orange,grape";
        char delimiter = ',';
        // Split the string based on the delimiter
        String[] result = input.split(String.valueOf(delimiter));
        String[] result1 = input.split(",");
        // Print the list of resulting substrings
        System.out.println(Arrays.toString(result));
        System.out.println(Arrays.toString(result1));
    }
}
