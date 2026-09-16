/**
 * Problem: Given a string, return all letters of the alphabet that are not present in the string.
 *
 * Approach: 
 * 1. Initialize a set with every lowercase letter of the English alphabet.
 * 2. Iterate over the input string (converted to lower case) and remove any encountered letters from the set.
 * 3. Concatenate the remaining characters in the set into a result string.
 *
 * Time Complexity: O(n + m), where n is the length of the input string and m = 26 (size of alphabet).
 * Space Complexity: O(m) for the set, plus O(k) for the output string where k is the number of missing letters.
 */
import java.util.*;

class PanagramDetectorMain {

    private static class PanagramDetector {
        private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

        public String findMissingLetters(String input) {
            Set<Character> missingCharacters = new HashSet<>();
            for (char ch : ALPHABET.toCharArray()) {
                missingCharacters.add(ch);
            }

            for (char ch : input.toLowerCase().toCharArray()) {
                missingCharacters.remove(ch);
            }

            StringBuilder sb = new StringBuilder();
            for (char ch : missingCharacters) {
                sb.append(ch);
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        PanagramDetector pd = new PanagramDetector();
        boolean success = true;

        success = success && "".equals(pd.findMissingLetters("The quick brown fox jumps over the lazy dog"));
        success = success && "abcdefghijklmnopqrstuvwxyz".equals(pd.findMissingLetters(""));

        if (success) {
            System.out.println("Passed");
        } else {
            System.out.println("Failed");
        }
    }
}