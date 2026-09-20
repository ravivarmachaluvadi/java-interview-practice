/*
 * =====================================================================
 *  Longest Substring with At Least K Repeating Characters    LeetCode 395 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a lowercase string s and an int k, return the length of the longest substring in
 *   which every character that appears does so at least k times. Return 0 if none exists.
 *   |s| <= 10^4, 1 <= k <= 10^5.
 *
 * EXAMPLE
 *   s = "aaabb",   k = 3  ->  3   ("aaa")
 *   s = "ababbc",  k = 2  ->  5   ("ababb": a x2, b x3; the lone c is excluded)
 *   s = "a",       k = 2  ->  0   (k longer than the string)
 *   s = "ababacb", k = 3  ->  0   (splitting on c then on b leaves only single a's)
 *
 * APPROACH  (divide and conquer on disqualified characters)
 *   1. Count the frequency of each char in s[start, end).
 *   2. Scan for the first char whose total count is < k. That char can never be inside a
 *      valid substring of this range, so the answer lies entirely to its left or right.
 *   3. Skip past any run of further bad chars, then recurse on the left piece and on the
 *      right piece and return the larger answer.
 *   4. If no char is bad, the whole range is valid: return end - start.
 *
 * KEY INSIGHT
 *   The condition "every char appears >= k times" is NOT monotonic: growing a window can
 *   make it valid, invalid, then valid again (add one 'c' to "aabb" and it breaks; add a
 *   second 'c' and it heals). A sliding window relies on monotonicity to know which
 *   pointer to move, so it cannot be applied directly. The escape is to find something
 *   that IS certain: a char with fewer than k copies in the whole range can never be in
 *   the answer, so split on it. Recognise this shape next time: non-monotonic validity
 *   means split on the disqualifier, not slide.
 *
 * COMPLEXITY
 *   Time  O(26 * n)  each recursion level removes at least one distinct letter, so depth
 *                    is at most 26, and each level scans the string once
 *   Space O(26)      the frequency array, plus recursion depth <= 26
 *
 * INTERVIEW FOLLOW-UPS
 *   - Sliding window version: fix the number of distinct chars d = 1..26, then for each d
 *     slide a window with at most d distinct chars and check all have count >= k. O(26 n).
 *   - Why is depth bounded by 26 and not n? Each split removes one letter from the alphabet.
 *   - Return the substring itself, not only its length.
 *
 * RUN
 *   main() runs 4 cases (typical, split once, k too large, no valid answer) and prints
 *   actual vs expected.
 */
class LongestSubstringWithKRepeatingChars {

    public static int longestSubstring(String s, int k) {
        if (s == null || s.isEmpty() || k > s.length())
            return 0;
        return longestIn(s, k, 0, s.length());
    }

    /** Longest valid substring inside the half-open range s[start, end). */
    private static int longestIn(String s, int k, int start, int end) {
        if (end - start < k)
            return 0; // too short to hold k copies of anything

        int[] freq = new int[26];
        for (int i = start; i < end; i++)
            freq[s.charAt(i) - 'a']++;

        for (int i = start; i < end; i++) {
            if (freq[s.charAt(i) - 'a'] >= k)
                continue;

            // s[i] has fewer than k copies in this range, so no valid answer contains it.
            // Skip the whole run of bad chars so the right piece starts on a good one.
            int next = i + 1;
            while (next < end && freq[s.charAt(next) - 'a'] < k)
                next++;

            return Math.max(longestIn(s, k, start, i), longestIn(s, k, next, end));
        }
        return end - start; // every char in the range appears at least k times
    }

    private static void print(String label, int actual, int expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical      ", longestSubstring("aaabb", 3), 3);
        print("case 2 split once   ", longestSubstring("ababbc", 2), 5);
        print("case 3 k too large  ", longestSubstring("a", 2), 0);
        print("case 4 nothing valid", longestSubstring("ababacb", 3), 0);
    }
}
