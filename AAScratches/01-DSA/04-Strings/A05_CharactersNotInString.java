/*
 * =====================================================================
 *  Letters Missing From a String (Pangram Check)         Building block | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a string, return every lowercase letter a-z that does NOT appear in it, in
 *   alphabetical order. Case is ignored; digits, spaces and punctuation are skipped.
 *   An empty result means the string is a pangram (LeetCode 1832 asks only for that boolean).
 *
 * EXAMPLE
 *   "the sun rises from the east"                   ->  "bcdgjklpqvwxyz"
 *   "The quick brown fox jumps over the lazy dog"   ->  ""      pangram
 *   ""                                              ->  "abcdefghijklmnopqrstuvwxyz"
 *   "Hello, World! 123"                             ->  "abcfgijkmnpqstuvxyz"   no crash on '!'
 *
 * APPROACH  (frequency array read-back)
 *   1. Lower-case the input, count each letter into int[26]; skip anything outside a-z,
 *      because ('!' - 'a') is a negative index and would throw.
 *   2. Walk the 26 slots in order; every slot still at 0 is a missing letter. Convert the
 *      index back with (char) (j + 'a').
 *
 *   Second method, missingLettersByAlphabetSet: seed a LinkedHashSet with a-z (insertion order
 *   keeps the output alphabetical), remove each seen char, and whatever is left is the answer.
 *   Removing a non-letter is a harmless no-op, so no guard is needed.
 *
 * KEY INSIGHT
 *   The same int[26] as the anagram check, but READ the other way: a count of zero is itself
 *   the answer. Frequency arrays answer "what is present" and "what is absent" equally well.
 *
 * COMPLEXITY
 *   Time  O(n)   one pass over the input plus a fixed 26-slot scan
 *   Space O(1)   26 ints, or a set of at most 26 chars
 *
 * INTERVIEW FOLLOW-UPS
 *   - Only need the boolean? A bitmask int (set bit ch-'a', check mask == (1<<26)-1) or a
 *     HashSet size == 26 check.
 *   - Unicode letters: Character.isLetter plus a HashMap, no fixed-size array.
 *   - Return the FIRST missing letter only: stop at the first zero slot.
 *
 * RUN
 *   main() runs 4 cases (typical, pangram, empty, punctuation) and prints actual vs expected
 *   for both methods plus the isPangram flag.
 */
import java.util.LinkedHashSet;
import java.util.Set;

class CharactersNotInString {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    /** Approach 1: fixed-size frequency array, emit the indexes whose count is 0. */
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

    /** Approach 2: seed a set with the alphabet, subtract what we see. */
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

    private static void check(String label, String input, String expected) {
        System.out.println(label + " input \"" + input + "\"");
        System.out.println("   frequencyArray: \"" + missingLettersByFrequencyArray(input)
                + "\"   expected \"" + expected + "\"");
        System.out.println("   alphabetSet   : \"" + missingLettersByAlphabetSet(input)
                + "\"   expected \"" + expected + "\"");
        System.out.println("   isPangram     : " + isPangram(input) + "   expected " + expected.isEmpty());
    }

    public static void main(String[] args) {
        check("case 1 (typical)    ", "the sun rises from the east", "bcdgjklpqvwxyz");
        check("case 2 (pangram)    ", "The quick brown fox jumps over the lazy dog", "");
        check("case 3 (empty)      ", "", ALPHABET);
        check("case 4 (punctuation)", "Hello, World! 123", "abcfgijkmnpqstuvxyz");
    }
}
