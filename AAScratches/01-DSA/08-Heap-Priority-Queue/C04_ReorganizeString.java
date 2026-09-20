/*
 * =====================================================================
 *  Reorganize String                                 LeetCode 767 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a string s, rearrange its characters so that no two adjacent characters
 *   are equal. Return any valid arrangement, or "" if none exists.
 *   1 <= s.length <= 500, lowercase letters only.
 *
 * EXAMPLE
 *   s = "aab"    ->  "aba" s = "aaab"   ->  ""       'a' appears 3 times; length 4 allows at most 2
 *   s = "vvvlo"  ->  "vlvov"  (any other valid order is also accepted)
 *   s = "a"      ->  "a"      a single character is trivially valid
 *
 * APPROACH  (frequency max-heap, dual poll)
 *   1. Count every character into a map.
 *   2. Push each (char, count) entry into a max-heap ordered by count.
 *   3. Feasibility check: if the top count > (n + 1) / 2, no arrangement exists.
 *   4. While the heap holds at least two entries, poll the two most frequent
 *      characters, append both, decrement their counts, and push back any entry
 *      whose count is still > 0.
 *   5. If one entry is left, step 3 guarantees its count is 1; append it.
 *
 * KEY INSIGHT
 *   Always placing the two most frequent remaining characters next to each other
 *   stops the most frequent one from ever being forced beside itself. Because a
 *   character polled this round cannot be polled again until the next round, it
 *   gets a "cooldown" of exactly one slot, which is the no-adjacent-repeat rule.
 *   Pattern: "spread out the heavy hitters" == max-heap on frequency plus a
 *   cooldown. Task Scheduler (LC 621) is the same shape.
 *
 * COMPLEXITY
 *   Time  O(n log k)  n characters, k distinct chars (k <= 26, so effectively O(n))
 *   Space O(k)        the frequency map and the heap
 *
 * INTERVIEW FOLLOW-UPS
 *   - Rearrange String k Distance Apart (LC 358): poll k entries per round instead of 2.
 *   - Task Scheduler (LC 621): same heap, but idle slots are inserted when cooldown fails.
 *   - Without a heap: fill even indices with the most frequent char, then odd indices.
 *
 * RUN
 *   main() runs 4 cases and prints the result, whether it is a valid arrangement,
 *   and the expected outcome. Ties in the heap mean the exact string may differ
 *   between runs, so validity is what is checked.
 */

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class ReorganizeString {

    public static String reorganizeString(String s) {
        if (s == null || s.length() <= 1) {
            return s;
        }
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : s.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }

        // max-heap: the most frequent character sits on top
        PriorityQueue<Map.Entry<Character, Integer>> maxHeap =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());
        maxHeap.addAll(freq.entrySet());

        // more than half the slots (rounded up) for one char -> two of them must touch
        if (maxHeap.peek().getValue() > (s.length() + 1) / 2) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        while (maxHeap.size() > 1) {
            Map.Entry<Character, Integer> first = maxHeap.poll();
            Map.Entry<Character, Integer> second = maxHeap.poll();
            result.append(first.getKey()).append(second.getKey());
            offerIfRemaining(maxHeap, first);
            offerIfRemaining(maxHeap, second);
        }
        // at most one entry remains, and the feasibility check guarantees its count is 1
        if (!maxHeap.isEmpty()) {
            result.append(maxHeap.poll().getKey());
        }
        return result.toString();
    }

    /** Push the entry back with one fewer occurrence, unless it is used up. */
    private static void offerIfRemaining(PriorityQueue<Map.Entry<Character, Integer>> heap,
                                         Map.Entry<Character, Integer> used) {
        if (used.getValue() > 1) {
            heap.offer(new AbstractMap.SimpleEntry<>(used.getKey(), used.getValue() - 1));
        }
    }

    /** True when result is a permutation of original with no two equal neighbours. */
    private static boolean isValidArrangement(String original, String result) {
        if (result.length() != original.length()) {
            return false;
        }
        char[] a = original.toCharArray();
        char[] b = result.toCharArray();
        Arrays.sort(a);
        Arrays.sort(b);
        if (!Arrays.equals(a, b)) {
            return false;
        }
        for (int i = 1; i < result.length(); i++) {
            if (result.charAt(i) == result.charAt(i - 1)) {
                return false;
            }
        }
        return true;
    }

    private static void print(String label, String input, boolean expectPossible) {
        String actual = reorganizeString(input);
        String verdict;
        if (actual.isEmpty()) {
            verdict = "impossible";
        } else {
            verdict = isValidArrangement(input, actual) ? "valid" : "INVALID";
        }
        String expected = expectPossible ? "valid" : "impossible";
        System.out.println(label + ": \"" + input + "\" -> \"" + actual + "\" (" + verdict + ")"
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical", "aab", true);
        print("case 2 impossible", "aaab", false);
        print("case 3 tricky", "vvvlo", true);
        print("case 4 single char", "a", true);
    }
}
