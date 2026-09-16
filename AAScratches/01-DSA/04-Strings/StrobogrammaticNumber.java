/**
 * Determines whether a given numeric string is strobogrammatic,
 * i.e., it looks the same when rotated 180 degrees.
 *
 * The algorithm checks characters from both ends moving inward.
 * For each pair, it verifies that the left character has a valid
 * mapping (0↔0, 1↔1, 6↔9, 8↔8, 9↔6) and that this mapped value equals
 * the right character. If any check fails, the string is not strobogrammatic.
 *
 * Time Complexity: O(n), where n is the length of the input string,
 * since each character is examined at most once.
 * Space Complexity: O(1), aside from the constant‑size mapping table.
 */
import java.util.*;

class StrobogrammaticNumber {

    private static final Map<Character, Character> MAP = new HashMap<>();

    static {
        MAP.put('0', '0');
        MAP.put('1', '1');
        MAP.put('6', '9');
        MAP.put('8', '8');
        MAP.put('9', '6');
    }

    public static boolean isStrobogrammatic(String num) {
        int left = 0;
        int right = num.length() - 1;

        while (left <= right) {
            char cLeft = num.charAt(left);
            char cRight = num.charAt(right);

            // If left character has no valid mapping, or
            // its mapping doesn't match the right character, fail
            if (!MAP.containsKey(cLeft) || MAP.get(cLeft) != cRight) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }

    public static void main(String[] args) {
        String[] testCases = {
                "69",
                "88",
                "962",
                "818",
                "2",
                "619",
                "906"
        };

        for (String tc : testCases) {
            System.out.printf("isStrobogrammatic(\"%s\") = %s%n", tc, isStrobogrammatic(tc));
        }
    }
}
