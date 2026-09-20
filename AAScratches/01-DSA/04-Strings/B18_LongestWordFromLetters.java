/*
 * =====================================================================
 *  Longest Word From Letters                      Custom | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a string of available letters and a dictionary of words, return every
 *   dictionary word of maximum length that can be spelled using the letters, each
 *   letter used at most as many times as it appears. Return an empty set if nothing
 *   can be formed. Related to LeetCode 1160 (Find Words That Can Be Formed by Characters).
 *
 * EXAMPLE
 *   letters = "oet",  dict = {to, toe, toet, toes}   ->  [toe]     "toet" needs two t's
 *   letters = "aabb", dict = {ab, ba, aab, abb, bbb} ->  [aab, abb] both length 3 (tie kept)
 *   letters = "",     dict = {a, b}                   ->  []
 *   letters = "xyz",  dict = {abc}                    ->  []       no word formable
 *
 * APPROACH  (Can-form check via letter counts)
 *   1. Count each available letter once into a map (letter -> count).
 *   2. For each word, count its letters as you go; the moment any letter's count
 *      exceeds what is available, the word is not formable (canFormWord).
 *   3. Track maxLength: a longer formable word clears the result set and starts a
 *      new one; an equal-length word is added to it (ties are kept).
 *
 * KEY INSIGHT
 *   Frequency counting works as a feasibility test, not just a comparison: "can A be
 *   built from B" is "for every char, count_A <= count_B". The available-letter map
 *   is built once and read many times; only the per-word map is rebuilt.
 *
 * COMPLEXITY
 *   Time  O(L + W)  L = letters length, W = total characters across the dictionary
 *   Space O(L + K)  the letter map plus the per-word map (K = distinct chars in a word)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return only one word: keep a single String instead of a set (or pick the
 *     lexicographically smallest on ties).
 *   - Use int[26] instead of a HashMap when input is lowercase a-z: faster and simpler.
 *   - Return the sum of lengths of all formable words (that is LeetCode 1160).
 *   - Many queries against one dictionary: precompute each word's count array once.
 *
 * RUN
 *   main() runs 4 cases (typical, tie, empty letters, nothing formable) and prints
 *   actual vs expected. Results are printed sorted so the output is deterministic.
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

class LongestWordFromLetters {

    public static Set<String> longestWord(String letters, Set<String> dictionary) {
        Set<String> longestWords = new HashSet<>();
        int maxLength = 0;

        Map<Character, Integer> letterCount = getLetterCount(letters);

        for (String word : dictionary) {
            if (!canFormWord(word, letterCount)) continue;

            if (word.length() > maxLength) {
                maxLength = word.length();
                longestWords.clear(); // a strictly longer word invalidates earlier ties
                longestWords.add(word);
            } else if (word.length() == maxLength) {
                longestWords.add(word); // same length: keep the tie
            }
        }
        return longestWords;
    }

    private static Map<Character, Integer> getLetterCount(String letters) {
        Map<Character, Integer> countMap = new HashMap<>();
        for (char c : letters.toCharArray()) {
            countMap.put(c, countMap.getOrDefault(c, 0) + 1);
        }
        return countMap;
    }

    /** True when every letter in word is available at least as many times as needed. */
    private static boolean canFormWord(String word, Map<Character, Integer> letterCount) {
        Map<Character, Integer> wordCount = new HashMap<>();
        for (char c : word.toCharArray()) {
            wordCount.put(c, wordCount.getOrDefault(c, 0) + 1);
            if (wordCount.get(c) > letterCount.getOrDefault(c, 0)) {
                return false; // needs more of c than we have
            }
        }
        return true;
    }

    private static void print(String label, Set<String> actual, Set<String> expected) {
        // TreeSet gives a stable order so the two sides can be compared by eye
        System.out.println(label + ": " + new TreeSet<>(actual)
                + "   expected " + new TreeSet<>(expected));
    }

    private static Set<String> set(String... words) {
        return new HashSet<>(Arrays.asList(words));
    }

    public static void main(String[] args) {
        print("case 1 typical", longestWord("oet", set("to", "toe", "toet", "toes")), set("toe"));
        print("case 2 tie", longestWord("aabb", set("ab", "ba", "aab", "abb", "bbb")),
                set("aab", "abb"));
        print("case 3 empty letters", longestWord("", set("a", "b")), set());
        print("case 4 none formable", longestWord("xyz", set("abc")), set());
    }
}
