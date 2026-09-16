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