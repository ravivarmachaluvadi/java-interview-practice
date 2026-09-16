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
