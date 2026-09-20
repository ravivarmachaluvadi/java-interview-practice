/*
 * =====================================================================
 *  First Unique Character in a String       LeetCode 387 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a string s, return the index of the first character that appears exactly
 *   once. If every character repeats, return -1. LeetCode restricts s to lowercase
 *   letters; the map version here works for any characters.
 *
 * EXAMPLE
 *   "leetcode"      ->  0    'l' appears once and is first
 *   "loveleetcode"  ->  2    'l','o','e' repeat; 'v' at index 2 is the first unique
 *   "aabb"          ->  -1   everything repeats
 *   ""              ->  -1   nothing to find
 *
 * APPROACH  (order-preserving frequency map, then a second pass)
 *   Version A, firstUniqueChar (author's, Java Streams):
 *   1. Group characters into a LinkedHashMap<Character, Long> of counts, so keys
 *      keep first-seen order.
 *   2. Stream the entries, keep those with count == 1, take the first.
 *   3. Map that character back to its index with s.indexOf(ch); -1 if none.
 *
 *   Version B, firstUniqueCharArray (the interview answer):
 *   1. Count into int[26] in one pass.
 *   2. Walk the string again left to right; the first char with count 1 is the answer.
 *
 * KEY INSIGHT
 *   Two passes: count first, then re-scan in original order. The "first" in the
 *   question is about string order, so either the count structure must remember
 *   insertion order (LinkedHashMap) or you re-walk the string (int[26]). Plain
 *   HashMap iteration order is not string order and is the classic bug here.
 *
 * COMPLEXITY
 *   Time  O(n)  two passes; map operations are O(1) average
 *   Space O(k)  k distinct characters (O(1) for the int[26] version)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Stream of characters (online), return first unique so far after each char?
 *     Keep a queue of candidates plus a count map; pop repeated heads lazily.
 *   - Return the character rather than the index? Same scan, different return.
 *   - Unicode input? Use the map version; the int[26] shortcut assumes a-z.
 *
 * RUN
 *   main() runs 4 cases (typical, tricky, all repeating, empty) through both
 *   versions and prints actual vs expected.
 */
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

class FirstNonRepeatingChar {

    /** Version A: LinkedHashMap keeps first-seen order so findFirst() is the answer. */
    public static int firstUniqueChar(String s) {
        Map<Character, Long> counts = s.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        LinkedHashMap::new,      // insertion order matters here
                        Collectors.counting()));

        return counts.entrySet().stream()
                .filter(entry -> entry.getValue() == 1)
                .findFirst()
                .map(entry -> s.indexOf(entry.getKey()))
                .orElse(-1);
    }

    /** Version B: count into int[26], then re-scan the string in order. */
    public static int firstUniqueCharArray(String s) {
        int[] count = new int[26];
        for (char c : s.toCharArray()) {
            count[c - 'a']++;
        }
        for (int i = 0; i < s.length(); i++) {
            if (count[s.charAt(i) - 'a'] == 1) {
                return i;
            }
        }
        return -1;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        String[] inputs = {"leetcode", "loveleetcode", "aabb", ""};
        int[] expected = {0, 2, -1, -1};

        for (int i = 0; i < inputs.length; i++) {
            String s = inputs[i];
            print("case " + (i + 1) + " map   \"" + s + "\"", firstUniqueChar(s), expected[i]);
            print("case " + (i + 1) + " array \"" + s + "\"", firstUniqueCharArray(s), expected[i]);
        }
    }
}
