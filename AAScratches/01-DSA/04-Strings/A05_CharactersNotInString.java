/**
 * Problem: Given a string, return every lowercase letter a-z that does NOT appear in it
 *          (empty result == the string is a pangram).
 *
 * Approaches:
 *  1. Frequency array int[26]  - count each letter, emit indexes whose count is 0.   O(n) time, O(1) space.
 *  2. Alphabet set             - seed a set with a-z, remove each seen letter, emit what's left. O(n) time, O(26) space.
 *
 * Notes:
 *  - Both must skip non-letters (spaces, digits, punctuation): with the array approach a stray '!'
 *    would compute a negative index and throw ArrayIndexOutOfBoundsException.
 *  - Set approach uses LinkedHashSet seeded in a-z order so the output is alphabetical;
 *    a plain HashSet would give an unspecified order.
 */
import java.util.LinkedHashSet;
import java.util.Set;

class CharactersNotInString {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    // Approach 1: fixed-size frequency array
    static String missingLettersByFrequencyArray(String str) {
        if (str == null || str.isEmpty())
            return ALPHABET;                      // nothing seen -> everything missing

        int[] charsCount = new int[26];
        for (char ch : str.toLowerCase().toCharArray()) {
            if (ch >= 'a' && ch <= 'z')           // guard: non-letters have no slot in the array
                charsCount[ch - 'a']++;
        }

        StringBuilder ans = new StringBuilder();
        for (int j = 0; j < 26; j++) {
            if (charsCount[j] == 0)
                ans.append((char) (j + 'a'));     // index -> letter
        }
        return ans.toString();
    }

    // Approach 2: seed a set with the alphabet, subtract what we see
    static String missingLettersByAlphabetSet(String str) {
        Set<Character> missing = new LinkedHashSet<>();   // insertion order == alphabetical
        for (char ch : ALPHABET.toCharArray())
            missing.add(ch);

        if (str != null) {
            for (char ch : str.toLowerCase().toCharArray())
                missing.remove(ch);               // removing a non-letter is a harmless no-op
        }

        StringBuilder sb = new StringBuilder();
        for (char ch : missing)
            sb.append(ch);
        return sb.toString();
    }

    static boolean isPangram(String str) {
        return missingLettersByFrequencyArray(str).isEmpty();
    }

    public static void main(String[] args) {
        String[] inputs = {
                "the sun rises from the east",
                "The quick brown fox jumps over the lazy dog",   // pangram -> ""
                "",                                              // -> full alphabet
                "Hello, World! 123"                              // non-letters must not crash
        };

        for (String s : inputs) {
            String byArray = missingLettersByFrequencyArray(s);
            String bySet = missingLettersByAlphabetSet(s);
            System.out.println("Input          : \"" + s + "\"");
            System.out.println("  frequencyArray: \"" + byArray + "\"");
            System.out.println("  alphabetSet   : \"" + bySet + "\"");
            System.out.println("  isPangram     : " + isPangram(s)
                    + (byArray.equals(bySet) ? "" : "   <-- MISMATCH"));
        }
    }
}
