/*
 * =====================================================================
 *  Ransom Note                                          LeetCode 383 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given two lowercase strings ransomNote and magazine, return true if ransomNote
 *   can be built from the letters of magazine, using each magazine letter at most
 *   once. Both strings contain only 'a'..'z'.
 *
 * EXAMPLE
 *   ransomNote = "a",  magazine = "b"    ->  false   no 'a' available
 *   ransomNote = "aa", magazine = "ab"   ->  false   only one 'a', need two
 *   ransomNote = "aa", magazine = "aab"  ->  true
 *   ransomNote = "",   magazine = "abc"  ->  true    nothing needed
 *
 * APPROACH  (int[26] counting array)
 *   1. Build count[26] where count[c - 'a'] is how many times letter c occurs in
 *      magazine.
 *   2. Walk ransomNote and decrement count for each letter.
 *   3. If any count drops below zero, the note needs more of that letter than the
 *      magazine has: return false. Otherwise return true.
 *
 * KEY INSIGHT
 *   When the key space is small and fixed (26 letters), an int[26] beats a HashMap:
 *   no boxing, no hashing, cache friendly. The "decrement and check for negative"
 *   idiom folds the availability test into the same loop, so a single mismatch stops
 *   the scan early.
 *
 * COMPLEXITY
 *   Time  O(m + n)  one pass over each string
 *   Space O(1)      the 26-slot array does not grow with input
 *
 * INTERVIEW FOLLOW-UPS
 *   - Unicode / mixed case input? Fall back to HashMap<Character, Integer>.
 *   - Quick reject: if ransomNote.length() > magazine.length() return false up front.
 *   - Valid Anagram (LC 242) is the same counter with an equality check at the end.
 *   - Magazine arrives as a stream? Count magazine first, then the note works unchanged.
 *
 * RUN
 *   main() runs 4 cases (missing letter, not enough of a letter, success, empty note)
 *   and prints actual vs expected.
 */
class RansomNote {

    public static boolean canConstruct(String ransomNote, String magazine) {
        int[] count = new int[26]; // count[i] = available copies of letter ('a' + i)
        for (char c : magazine.toCharArray()) {
            count[c - 'a']++;
        }
        for (char c : ransomNote.toCharArray()) {
            if (--count[c - 'a'] < 0) {
                return false; // used more of this letter than the magazine offers
            }
        }
        return true;
    }

    private static void check(String label, String ransomNote, String magazine, boolean expected) {
        System.out.println(label + " note=\"" + ransomNote + "\", magazine=\"" + magazine
                + "\" -> " + canConstruct(ransomNote, magazine) + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("case 1 (letter missing):", "a", "b", false);
        check("case 2 (not enough):    ", "aa", "ab", false);
        check("case 3 (typical true):  ", "aa", "aab", true);
        check("case 4 (empty note):    ", "", "abc", true);
    }
}
