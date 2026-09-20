/*
 * =====================================================================
 *  Find and Replace Pattern                       LeetCode 890 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a list of words and a pattern of the same length, return every word that
 *   matches the pattern. A word matches when there is a one-to-one letter mapping
 *   (a bijection) that turns the pattern into the word: same letters in the pattern must
 *   map to the same letter in the word, and different letters must map to different ones.
 *
 * EXAMPLE
 *   words = [xyz, mee, deq, abc, aqq, dkd, ccc], pattern = "abb"  ->  [mee, aqq]
 *       "ccc" fails: a and b would both map to c (not one-to-one)
 *   words = [a, b], pattern = "a"                                  ->  [a, b]
 *   words = [aab, xyz, zzz, abb], pattern = "abc"                  ->  [xyz]
 *
 * APPROACH  (first-index normalisation)
 *   1. Two strings have the same "shape" when, for every position i, the first index at
 *      which word[i] appears in word equals the first index at which pattern[i] appears
 *      in pattern. "mee" -> [0,1,1], "abb" -> [0,1,1]; "ccc" -> [0,0,0] differs.
 *   2. check(word, pattern) compares word.indexOf(word[i]) with pattern.indexOf(pattern[i])
 *      at each i and rejects on the first mismatch.
 *   3. Collect every word that passes.
 *
 *   Alternative in this file (two maps): keep pattern->word and word->pattern maps and
 *   reject if either direction already holds a different letter. This is the explicit
 *   bijection check; the indexOf trick is the same test in disguise.
 *
 * KEY INSIGHT
 *   Replacing each letter with the index of its first occurrence turns any string into a
 *   canonical fingerprint that is the same for all strings of the same letter-shape.
 *   That fingerprint is the isomorphism class, so equality of fingerprints is exactly the
 *   bijection test, and it checks both directions at once.
 *
 * COMPLEXITY
 *   Time  O(n * k^2)  indexOf is O(k) per position; the two-map version is O(n * k)
 *   Space O(1)        beyond the result (two-map version: O(26) per word)
 *
 * INTERVIEW FOLLOW-UPS
 *   - LC 205 Isomorphic Strings is the two-string base case of the same test.
 *   - Why check both directions? A one-way map accepts "ccc" for "abb".
 *   - Make it O(n * k): precompute the pattern fingerprint once, compare int[] per word.
 *
 * RUN
 *   main() runs 3 cases (typical, single-letter, mismatch) through both methods and
 *   prints actual vs expected.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FindAndReplacePattern {

    /** Author's approach: compare first-occurrence indexes position by position. */
    public List<String> findAndReplacePattern(String[] words, String pattern) {
        List<String> result = new ArrayList<>();
        for (String word : words) {
            if (sameShape(word, pattern)) result.add(word);
        }
        return result;
    }

    private static boolean sameShape(String a, String b) {
        if (a.length() != b.length()) return false;
        for (int i = 0; i < a.length(); i++) {
            // first index of this char in its own string is the char's "shape id"
            if (a.indexOf(a.charAt(i)) != b.indexOf(b.charAt(i))) return false;
        }
        return true;
    }

    /** Alternative: explicit bijection check with a map in each direction. */
    public List<String> findAndReplacePatternTwoMaps(String[] words, String pattern) {
        List<String> result = new ArrayList<>();
        for (String word : words) {
            if (isBijection(word, pattern)) result.add(word);
        }
        return result;
    }

    private static boolean isBijection(String word, String pattern) {
        if (word.length() != pattern.length()) return false;
        Map<Character, Character> patternToWord = new HashMap<>();
        Map<Character, Character> wordToPattern = new HashMap<>();
        for (int i = 0; i < word.length(); i++) {
            char p = pattern.charAt(i);
            char w = word.charAt(i);
            // each direction must either be unseen or agree with the earlier mapping
            if (patternToWord.getOrDefault(p, w) != w) return false;
            if (wordToPattern.getOrDefault(w, p) != p) return false;
            patternToWord.put(p, w);
            wordToPattern.put(w, p);
        }
        return true;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        FindAndReplacePattern solution = new FindAndReplacePattern();
        String[] typical = {"xyz", "mee", "deq", "abc", "aqq", "dkd", "ccc"};
        String[] singleLetter = {"a", "b"};
        String[] mismatch = {"aab", "xyz", "zzz", "abb"};

        print("case 1 indexOf  ", solution.findAndReplacePattern(typical, "abb"), "[mee, aqq]");
        print("case 1 two-maps ", solution.findAndReplacePatternTwoMaps(typical, "abb"),
                "[mee, aqq]");
        print("case 2 indexOf  ", solution.findAndReplacePattern(singleLetter, "a"), "[a, b]");
        print("case 2 two-maps ", solution.findAndReplacePatternTwoMaps(singleLetter, "a"),
                "[a, b]");
        print("case 3 indexOf  ", solution.findAndReplacePattern(mismatch, "abc"), "[xyz]");
        print("case 3 two-maps ", solution.findAndReplacePatternTwoMaps(mismatch, "abc"), "[xyz]");
    }
}
