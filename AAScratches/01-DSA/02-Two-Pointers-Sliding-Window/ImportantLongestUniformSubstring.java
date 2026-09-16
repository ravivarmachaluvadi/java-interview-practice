/**
 * Problem:
 *   Given a string, find the longest contiguous substring consisting of identical characters
 *   and return that substring.
 *
 * Approach:
 *   Scan the string once while tracking the current run length and character.
 *   When the run ends, update the maximum if needed.
 *   After the loop, perform one final check for a run ending at the string's end.
 *
 * Complexity:
 *   Time:  O(n) – single pass over the input string
 *   Space: O(1) – only constant auxiliary variables are used
 */
import java.util.*;

class ImportantLongestUniformSubstring {
    public static String longestUniformSubstring(String s) {
        if (s == null || s.isEmpty()) return "";

        int maxLen = 1;
        int currentLen = 1;
        char maxChar = s.charAt(0);
        char currentChar = s.charAt(0);

        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == currentChar)
                currentLen++;
            else {
                if (currentLen > maxLen) {
                    maxLen = currentLen;
                    maxChar = currentChar;
                }
                currentChar = s.charAt(i);
                currentLen = 1;
            }
        }

        // Check at the end of the loop in case the longest substring is at the end
        if (currentLen > maxLen) {
            maxLen = currentLen;
            maxChar = currentChar;
        }
        return String.valueOf(maxChar).repeat(maxLen);
    }

    public static void main(String[] args) {
        String input = "aabbbbbcddeeee";
        System.out.println("Longest Uniform Substring: " + longestUniformSubstring(input));
    }
}
