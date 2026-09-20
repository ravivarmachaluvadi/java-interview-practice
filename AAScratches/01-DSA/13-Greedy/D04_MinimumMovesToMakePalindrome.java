/*
 * =====================================================================
 *  Minimum Number of Moves to Make Palindrome          LeetCode 2193 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given a lowercase string s that can be rearranged into a palindrome, return the
 *   minimum number of ADJACENT swaps needed to turn s into a palindrome.
 *   LeetCode guarantees the input is feasible; this file adds its own feasibility
 *   guard so an impossible string returns -1 instead of looping or lying.
 *
 * EXAMPLE
 *   "zzazz"  -> 0    already a palindrome
 *   "letelt" -> 2    two adjacent swaps reach "lettel"
 *   "aabb"   -> 2    aabb -> abab -> abba
 *   "aab"    -> 1    aab  -> aba          ('b' is the odd char, nudged to the middle)
 *   "mamad"  -> 3    the classic case that a careless two-sided scan gets wrong
 *   "mbadm"  -> -1   three letters with odd counts, so no palindrome exists
 *
 * APPROACH  (adjacent-swap matching with two pointers)
 *   1. Feasibility first: count letters; a palindrome allows at most one odd count.
 *   2. Walk two pointers i (left) and j (right). If arr[i] == arr[j] the pair is
 *      already settled, so shrink both and continue.
 *   3. Otherwise scan k from j leftwards for a partner equal to arr[i]. Bubble that
 *      partner right to position j, one adjacent swap at a time, adding (j - k) moves.
 *      Then shrink both pointers.
 *   4. If the scan reaches k == i there is no partner: arr[i] is the odd middle char.
 *      Swap it one step inward (1 move) and retry the same i without shrinking.
 *   twoSidedFixed() is the same idea that also searches for a partner for arr[j] and
 *   takes whichever side is cheaper. It returns identical answers - shown so you can
 *   see that the choice of side does not change the cost, only the path.
 *
 * KEY INSIGHT
 *   Fix the outermost pair first and never revisit it. Bubbling the partner inward
 *   never disturbs the already-fixed outside, and any other pairing of arr[i] costs at
 *   least as much (exchange argument). The one case that breaks naive code is the odd
 *   middle character: it has no partner, so it must be walked to the centre by itself.
 *   Pattern to recognise: "minimum adjacent swaps" is always an inversion count in
 *   disguise - here, the inversions of the target pairing.
 *
 * COMPLEXITY
 *   Time  O(n^2)  every pair may scan and bubble across the whole remaining window
 *   Space O(n)    one char[] copy of the input
 *
 * INTERVIEW FOLLOW-UPS
 *   - Get it to O(n log n): record each char's target index, then count inversions
 *     with a Fenwick tree instead of physically bubbling.
 *   - What if swaps are between ANY two positions, not just adjacent ones?
 *   - What if s may contain a character with odd count more than once - detect and
 *     return -1 (the canBePalindrome guard below).
 *   - Related: minimum adjacent swaps to sort an array = number of inversions.
 *
 * RUN
 *   main() runs 6 feasible cases, 2 impossible cases, and one deliberately buggy
 *   variant kept as a teaching contrast. Every line prints actual vs expected.
 */

class MinimumMovesToMakePalindrome {

    // ---------- Approach 1: greedy, partner always searched from the right ----------
    public static int greedyFromRight(String s) {
        char[] arr = s.toCharArray();
        if (!canBePalindrome(arr)) return -1;

        int i = 0, j = arr.length - 1, moves = 0;
        while (i < j) {
            if (arr[i] == arr[j]) {   // outer pair already matches, nothing to pay
                i++;
                j--;
                continue;
            }
            int k = j;
            while (k > i && arr[k] != arr[i]) k--;   // nearest partner for arr[i]

            if (k == i) {
                // No partner anywhere: arr[i] is the single odd char. Walk it one step
                // toward the centre and retry the same i on the next iteration.
                swap(arr, i, i + 1);
                moves++;
            } else {
                // Bubble the partner from k up to j; each adjacent swap costs 1 move.
                for (int t = k; t < j; t++) {
                    swap(arr, t, t + 1);
                    moves++;
                }
                i++;
                j--;
            }
        }
        return moves;
    }

    // ---------- Approach 2: search both sides, bubble whichever is cheaper ----------
    // Same answers as approach 1, different swap path. Kept to show that the greedy
    // cost is independent of which end you move.
    public static int twoSidedFixed(String s) {
        char[] arr = s.toCharArray();
        // Without this guard an impossible input like "mbadm" loops forever: both ends
        // stay unpaired, the nudge swaps back and forth, and the pointers never advance.
        if (!canBePalindrome(arr)) return -1;

        int left = 0, right = arr.length - 1, moves = 0;
        while (left < right) {
            if (arr[left] == arr[right]) {
                left++;
                right--;
                continue;
            }
            int k = right;                                        // partner for arr[left]
            while (k > left && arr[k] != arr[left]) k--;
            int m = left;                                         // partner for arr[right]
            while (m < right && arr[m] != arr[right]) m++;

            int costRight = (k == left) ? Integer.MAX_VALUE : right - k;  // bubble k -> right
            int costLeft = (m == right) ? Integer.MAX_VALUE : m - left;   // bubble m -> left

            if (costRight == Integer.MAX_VALUE && costLeft == Integer.MAX_VALUE) {
                swap(arr, left, left + 1);   // arr[left] is the odd middle char
                moves++;
            } else if (costRight <= costLeft) {
                for (int t = k; t < right; t++) swap(arr, t, t + 1);
                moves += costRight;
                left++;
                right--;
            } else {
                for (int t = m; t > left; t--) swap(arr, t, t - 1);
                moves += costLeft;
                left++;
                right--;
            }
        }
        return moves;
    }

    // ---------- The common wrong attempt, kept as a contrast ----------
    // Two bugs, both caused by sharing ONE tempLeft/tempRight pair across two searches:
    //  1. Odd middle char: neither while-loop finds a partner, the chosen for-loop runs
    //     zero times, yet the pointers advance as if the pair matched.   "aab" -> 0
    //  2. The second search starts where the first stopped, so it can walk past a real
    //     partner and undercount.                            "aabb" -> 1, "mamad" -> 0
    // It also has no feasibility guard, so impossible inputs return a meaningless number.
    public static int naiveTwoSided(String s) {
        char[] chars = s.toCharArray();
        int left = 0, right = chars.length - 1, moves = 0;

        while (left < right) {
            if (chars[left] == chars[right]) {
                left++;
                right--;
                continue;
            }
            int tempLeft = left, tempRight = right;
            while (tempLeft < tempRight && chars[tempLeft] != chars[right]) tempLeft++;
            while (tempLeft < tempRight && chars[left] != chars[tempRight]) tempRight--;

            if (tempLeft - left <= right - tempRight) {
                for (int i = tempLeft; i > left; i--) { swap(chars, i, i - 1); moves++; }
            } else {
                for (int i = tempRight; i < right; i++) { swap(chars, i, i + 1); moves++; }
            }
            left++;
            right--;
        }
        return moves;
    }

    /** A palindrome allows at most one character with an odd count. */
    private static boolean canBePalindrome(char[] arr) {
        int[] freq = new int[26];
        for (char c : arr) freq[c - 'a']++;
        int oddCount = 0;
        for (int f : freq) if ((f & 1) != 0) oddCount++;
        return oddCount <= 1;
    }

    private static void swap(char[] a, int x, int y) {
        char tmp = a[x];
        a[x] = a[y];
        a[y] = tmp;
    }

    private static void print(String label, Object actual, Object expected) {
        print(label, actual, expected, "");
    }

    private static void print(String label, Object actual, Object expected, String note) {
        boolean ok = String.valueOf(actual).equals(String.valueOf(expected));
        System.out.println(label + ": " + actual + "   expected " + expected
                + (note.isEmpty() ? "" : "   " + note) + (ok ? "" : "   <-- MISMATCH"));
    }

    public static void main(String[] args) {
        String[] inputs = {"zzazz", "letelt", "aabb", "aab", "abcba", "mamad"};
        int[] expected = {0, 2, 2, 1, 0, 3};

        System.out.println("--- feasible inputs (both correct methods must agree) ---");
        for (int i = 0; i < inputs.length; i++) {
            print("greedyFromRight(\"" + inputs[i] + "\")",
                    greedyFromRight(inputs[i]), expected[i]);
            print("twoSidedFixed  (\"" + inputs[i] + "\")",
                    twoSidedFixed(inputs[i]), expected[i]);
        }

        System.out.println("--- infeasible inputs (more than one odd-count letter) ---");
        print("greedyFromRight(\"mbadm\")", greedyFromRight("mbadm"), -1);
        print("twoSidedFixed  (\"mbadm\")", twoSidedFixed("mbadm"), -1);
        print("greedyFromRight(\"leetcode\")", greedyFromRight("leetcode"), -1);
        print("twoSidedFixed  (\"leetcode\")", twoSidedFixed("leetcode"), -1);

        System.out.println("--- the buggy variant: expected value is its KNOWN WRONG output ---");
        print("naiveTwoSided(\"aab\")", naiveTwoSided("aab"), 0, "(bug: true answer is 1)");
        print("naiveTwoSided(\"aabb\")", naiveTwoSided("aabb"), 1, "(bug: true answer is 2)");
        print("naiveTwoSided(\"mamad\")", naiveTwoSided("mamad"), 0, "(bug: true answer is 3)");
        print("naiveTwoSided(\"mbadm\")", naiveTwoSided("mbadm"), 0, "(no guard: true is -1)");
    }
}
