/*
 * =====================================================================
 *  P039 Monotonic Stack: Greedy Removal (Smallest Result)   Canonical LC 402 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 402, Remove K Digits)
 *   num is a non-negative integer as a string. Remove exactly k digits so the remaining
 *   number is as small as possible. Return it without leading zeros ("0" if empty).
 *
 * EXAMPLE
 *   "1432219", k = 3  ->  "1219"
 *   "10200",   k = 1  ->  "200"     leading zero dropped
 *   "10",      k = 2  ->  "0"       everything removed
 *
 * RECOGNIZE WHEN
 *   - Choose a SUBSEQUENCE (keep order) of a given length that is lexicographically
 *     smallest or largest: "remove k digits", "most competitive", "smallest subsequence of
 *     distinct characters".
 *   - A digit earlier in the result matters more than everything after it.
 *   Not this if: the order may change -> sort; you want max sum, not lexicographic order ->
 *   P043_TopK.
 *
 * TEMPLATE
 *   stack = []
 *   for x in seq:
 *       while stack and budget > 0 and stack.top > x:   // a smaller x should come earlier
 *           stack.pop(); budget--
 *       stack.push(x)
 *   drop extra items from the END if budget remains; strip leading zeros if numeric
 *
 * APPROACH
 *   1. Walk the digits. While the previous kept digit is bigger than the current one and
 *      removals remain, remove the previous digit.
 *   2. If removals remain at the end, the kept digits are non-decreasing: cut from the end.
 *
 * KEY INSIGHT
 *   The leftmost position where the result can get smaller dominates everything after it,
 *   so greedily make each position as small as possible. A bigger digit followed by a
 *   smaller one is a "peak", and deleting the first peak is always optimal.
 *
 * COMPLEXITY
 *   Time O(n), space O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 316  Remove Duplicate Letters pop only if the top appears AGAIN later
 *                                            (last index > i); skip letters already kept
 *   [coded] LC 1673 Most Competitive Subseq  keep exactly k: pop while
 *                                            (kept - 1) + (n - i) >= k
 *           LC 1081 Smallest Subsequence     identical to LC 316
 *           LC 321  Create Maximum Number    LC 1673 (max version) on both arrays + merge
 *           Largest number after k removals  flip the comparison (pop smaller tops)
 *
 * PITFALLS
 *   - Leading zeros: strip them at the end, and return "0" for an empty result.
 *   - LC 316: a letter already in the stack must be skipped, not pushed again.
 *   - Remaining budget after the loop: remove from the END, not the front.
 *
 * DEEP DIVE
 *   C07_RemoveKdigits (07-Stack-Queue-Monotonic)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

class GreedyRemovalStack {

    // Canonical LC 402.
    static String removeKdigits(String num, int k) {
        StringBuilder stack = new StringBuilder();       // used as a stack of chars
        for (char c : num.toCharArray()) {
            while (k > 0 && stack.length() > 0 && stack.charAt(stack.length() - 1) > c) {
                stack.deleteCharAt(stack.length() - 1);
                k--;
            }
            stack.append(c);
        }
        stack.setLength(stack.length() - k);             // leftover removals from the end
        int start = 0;
        while (start < stack.length() - 1 && stack.charAt(start) == '0') {
            start++;
        }
        String result = stack.substring(start);
        return result.isEmpty() ? "0" : result;
    }

    // LC 316: smallest result that contains every distinct letter exactly once.
    static String removeDuplicateLetters(String s) {
        int[] last = new int[26];
        for (int i = 0; i < s.length(); i++) {
            last[s.charAt(i) - 'a'] = i;
        }
        boolean[] kept = new boolean[26];
        Deque<Character> stack = new ArrayDeque<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (kept[c - 'a']) {
                continue;
            }
            while (!stack.isEmpty() && stack.peek() > c && last[stack.peek() - 'a'] > i) {
                kept[stack.pop() - 'a'] = false;        // it can be taken again later
            }
            stack.push(c);
            kept[c - 'a'] = true;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : stack) {
            sb.append(c);
        }
        return sb.reverse().toString();
    }

    // LC 1673: lexicographically smallest subsequence of length k.
    static int[] mostCompetitive(int[] nums, int k) {
        int[] stack = new int[k];
        int size = 0;
        for (int i = 0; i < nums.length; i++) {
            while (size > 0 && stack[size - 1] > nums[i] && size - 1 + (nums.length - i) >= k) {
                size--;
            }
            if (size < k) {
                stack[size++] = nums[i];
            }
        }
        return stack;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 402 1432219 k=3", removeKdigits("1432219", 3), "1219");
        check("LC 402 10200 k=1", removeKdigits("10200", 1), "200");
        check("LC 402 10 k=2", removeKdigits("10", 2), "0");
        check("LC 402 112 k=1 non-decreasing", removeKdigits("112", 1), "11");

        check("LC 316 bcabc", removeDuplicateLetters("bcabc"), "abc");
        check("LC 316 cbacdcbc", removeDuplicateLetters("cbacdcbc"), "acdb");

        check("LC 1673 [3,5,2,6] k=2",
                Arrays.toString(mostCompetitive(new int[]{3, 5, 2, 6}, 2)), "[2, 6]");
        check("LC 1673 [2,4,3,3,5,4,9,6] k=4",
                Arrays.toString(mostCompetitive(new int[]{2, 4, 3, 3, 5, 4, 9, 6}, 4)),
                "[2, 3, 3, 4]");
    }
}
