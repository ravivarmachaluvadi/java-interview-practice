/*
 * =====================================================================
 *  Find Most Frequent Vowel and Consonant             LeetCode 3541 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a string s of lowercase English letters, find the vowel with the highest
 *   frequency and the consonant with the highest frequency, and return the sum of
 *   those two frequencies. If s has no vowels (or no consonants) that side counts 0.
 *
 * EXAMPLE
 *   "successes"  ->  6   's' appears 4 times (max consonant), 'e' twice (max vowel)
 *   "aeiaeia"    ->  3   'a' appears 3 times; no consonants, so 3 + 0
 *   "bbbb"       ->  4   only consonants: 0 + 4
 *   "aeiou"      ->  1   every vowel appears once, so max vowel freq is 1, not 5
 *
 * APPROACH  (frequency array plus classification)
 *   1. Count each letter into int[26] with cnt[c - 'a']++.
 *   2. Walk the 26 slots; classify each letter as vowel or consonant.
 *   3. Keep the max count seen on each side.
 *   4. Return maxVowel + maxConsonant.
 *
 * KEY INSIGHT
 *   Counting and classifying are two separate passes: the frequency array is the
 *   reusable primitive, the vowel predicate is just a partition layered on top.
 *   The trap is reading "most frequent vowel" as "number of vowels": for "aeiou"
 *   the answer is 1 (the busiest single vowel), not 5.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass over the string plus a fixed 26-slot scan
 *   Space O(1)  the 26-int array does not grow with the input
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the characters themselves, not just counts? Track argmax per side.
 *   - Unicode or mixed-case input? Use a HashMap<Character,Integer> or normalise first.
 *   - Ties among vowels? The sum is the same either way; only matters if you return
 *     the letter.
 *
 * RUN
 *   main() runs 4 cases (typical, vowels only, consonants only, all-distinct vowels)
 *   and prints actual vs expected.
 */
class MostFrequentVowelAndConsonant {

    public static int maxFreqSum(String s) {
        int[] cnt = new int[26];
        for (char c : s.toCharArray()) {
            cnt[c - 'a']++;
        }

        int maxVowel = 0;
        int maxConsonant = 0;
        for (int i = 0; i < 26; i++) {
            char c = (char) ('a' + i);
            if (isVowel(c)) {
                maxVowel = Math.max(maxVowel, cnt[i]);
            } else {
                maxConsonant = Math.max(maxConsonant, cnt[i]);
            }
        }
        return maxVowel + maxConsonant;
    }

    private static boolean isVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 \"successes\"", maxFreqSum("successes"), 6);
        print("case 2 \"aeiaeia\"  ", maxFreqSum("aeiaeia"), 3);
        print("case 3 \"bbbb\"     ", maxFreqSum("bbbb"), 4);
        // each vowel once: the busiest vowel has frequency 1, consonants 0
        print("case 4 \"aeiou\"    ", maxFreqSum("aeiou"), 1);
    }
}
