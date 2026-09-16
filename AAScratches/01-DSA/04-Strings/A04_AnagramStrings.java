/**
 * Problem: Determine whether two input strings are anagrams of each other.
 *
 * Approach: Count the frequency of each lowercase letter in both strings using a 26‑element array.
 * Increment counts for characters from the first string, decrement for the second,
 * then verify all counts return to zero. If any count differs, the strings are not anagrams.
 *
 * Time Complexity: O(n), where n is the length of the strings (both must be equal).
 * Space Complexity: O(1) – constant 26‑element array regardless of input size.
 */
import java.util.*;

class AnagramStrings {
    public boolean anagramStrings(String s, String t) {
        if (s.length() != t.length()) return false;
        int[] count = new int[26];

        for (char c : s.toCharArray()) count[c - 'a']++;
        for (char c : t.toCharArray()) count[c - 'a']--;
        for (int i : count) {
            if (i != 0) return false;
        }
        return true;
    }
}

class Main {
    public static void main(String[] args) {
        String str1 = "integer";
        String str2 = "tegerni";

        AnagramStrings sol = new AnagramStrings();

        boolean result = sol.anagramStrings(str1, str2);

        if (result) System.out.println("The given strings are anagrams.");
        else System.out.println("The given strings are not anagrams.");
    }
}