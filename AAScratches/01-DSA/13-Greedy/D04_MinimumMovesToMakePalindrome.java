import java.util.*;

// https://leetcode.com/problems/minimum-number-of-moves-to-make-palindrome/description/
// 2193. Minimum Number of Moves to Make Palindrome
//
// Problem: minimum adjacent swaps to turn s into a palindrome (LeetCode guarantees it is possible).
// Approaches:
//   1. greedyFromRight   - two pointers; for each left char find its partner from the right and bubble
//                          it to position j; no partner => it is the odd middle char, nudge it inward.
//                          Includes an odd-frequency feasibility check (returns -1 if impossible).
//   2. naiveTwoSided     - searches both directions and picks the cheaper side, but the two searches
//                          share one pointer pair and there is no "odd middle" handling: "aab" -> 0
//                          (expected 1), "aabb" -> 1 (expected 2). Kept to show the bugs.
//   3. twoSidedFixed     - the two-sided idea done right: if neither side finds a partner, the left char
//                          is the odd one -> swap it one step toward the middle and retry.
class MinimumMovesToMakePalindrome {

    // ---------- Approach 1: greedy, partner searched from the right (correct) ----------
    public static int greedyFromRight(String s) {
        int n = s.length();
        char[] arr = s.toCharArray();

        // 1. Feasibility: a palindrome allows at most one char with an odd count
        if (!canBePalindrome(arr)) return -1;

        // 2. Greedy pairing from both ends
        int i = 0, j = n - 1, moves = 0;
        while (i < j) {
            if (arr[i] == arr[j]) {
                i++;
                j--;
                continue;
            }
            int k = j;
            // find matching partner for arr[i] from the right side
            while (k > i && arr[k] != arr[i]) k--;

            if (k == i) {
                // no partner: arr[i] is the odd middle char, push it one step toward the middle
                swap(arr, i, i + 1);
                moves++;
            } else {
                // partner at k < j: bubble it right until it sits at j
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

    // ---------- Approach 2: naive two-sided (BUGGY, kept for contrast) ----------
    // Two bugs, both from sharing one tempLeft/tempRight pair across the two searches:
    //  1. Odd middle char: neither while finds a partner, the chosen for-loop runs zero times,
    //     and the pointers advance as if the pair matched. "aab" -> 0, correct is 1.
    //  2. The second search starts where the first stopped (tempLeft < tempRight), so it can
    //     miss a real partner and undercount. "aabb" -> 1 (correct 2), "mamad" -> 0 (correct 3).
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

            // Try to move the left character towards the right (find a match for chars[right])
            while (tempLeft < tempRight && chars[tempLeft] != chars[right]) tempLeft++;
            // Try to move the right character towards the left (find a match for chars[left])
            while (tempLeft < tempRight && chars[left] != chars[tempRight]) tempRight--;

            // Choose the direction with fewer moves
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

    // ---------- Approach 3: two-sided, with the odd-middle case handled ----------
    // Same "pick the cheaper side" idea, but each search is independent and a miss is detected.
    public static int twoSidedFixed(String s) {
        char[] arr = s.toCharArray();
        // Without this guard an impossible input like "mbadm" loops forever: both ends stay
        // unpaired, the nudge swaps b<->a back and forth, and the pointers never advance.
        if (!canBePalindrome(arr)) return -1;
        int left = 0, right = arr.length - 1, moves = 0;

        while (left < right) {
            if (arr[left] == arr[right]) {
                left++;
                right--;
                continue;
            }
            // partner for arr[left], searched from the right
            int k = right;
            while (k > left && arr[k] != arr[left]) k--;
            // partner for arr[right], searched from the left
            int m = left;
            while (m < right && arr[m] != arr[right]) m++;

            int costRight = (k == left) ? Integer.MAX_VALUE : right - k;   // bubble arr[k] -> right
            int costLeft  = (m == right) ? Integer.MAX_VALUE : m - left;   // bubble arr[m] -> left

            if (costRight == Integer.MAX_VALUE && costLeft == Integer.MAX_VALUE) {
                // arr[left] and arr[right] are both unpaired: impossible unless one is the odd middle char
                // (guaranteed by the problem). Nudge the left one inward and retry.
                swap(arr, left, left + 1);
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

    // A palindrome allows at most one character with an odd count
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

    private static String mark(int actual, int expected) {
        return actual + (actual == expected ? "" : "   <-- WRONG");
    }

    public static void main(String[] args) {
        String[] inputs = {"zzazz", "letelt", "aabb", "aab", "abcba", "mamad"};
        int[] expected  = {0, 2, 2, 1, 0, 3};
        for (int i = 0; i < inputs.length; i++) {
            String s = inputs[i];
            System.out.println("Input: " + s + "  (expected " + expected[i] + ")");
            System.out.println("  greedyFromRight : " + mark(greedyFromRight(s), expected[i]));
            System.out.println("  naiveTwoSided   : " + mark(naiveTwoSided(s), expected[i]));
            System.out.println("  twoSidedFixed   : " + mark(twoSidedFixed(s), expected[i]));
        }
        // Impossible inputs (more than one odd-count letter) are not valid LeetCode input.
        // "mbadm" (b, a, d once each) and "leetcode" -> -1 from the feasibility check;
        // the naive version has no check and just returns a meaningless number.
        for (String s : new String[]{"mbadm", "leetcode"}) {
            System.out.println("Input: " + s + "  (impossible, expected -1)");
            System.out.println("  greedyFromRight : " + greedyFromRight(s));
            System.out.println("  naiveTwoSided   : " + naiveTwoSided(s) + "   <-- no check, garbage");
            System.out.println("  twoSidedFixed   : " + twoSidedFixed(s));
        }
    }
}
