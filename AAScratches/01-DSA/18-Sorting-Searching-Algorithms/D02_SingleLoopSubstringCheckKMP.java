/**
 * Problem: Determine whether a given pattern string occurs as a contiguous
 * substring within a larger text string.
 *
 * Approach: Use a single-pass linear scan with two indices (i for text,
 * j for pattern). Characters are compared sequentially; on mismatch the
 * pattern index resets to 0 and the text index is shifted back by the
 * number of matched characters plus one, effectively sliding the window
 * forward by one. A match is found when j reaches the pattern length.
 *
 * Time Complexity: O(n + m) in the worst case (each character of text
 * may be examined a constant number of times).  
 * Space Complexity: O(1), only a few integer variables are used.
 */
class SingleLoopSubstringCheckKMP {

    public static boolean isSubstring(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();

        if (m == 0) return true;
        if (n < m) return false;

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    return true; // found match
                }
            } else {
                // reset pattern pointer and shift window
                j = 0;
                // Just move text window by 1 from the last starting point
                i = i - j + 1;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String text = "abxabcabcaby";
        String pattern = "abcaby";

        if (isSubstring(text, pattern)) {
            System.out.println("Yes, substring exists.");
        } else {
            System.out.println("No, substring does not exist.");
        }
    }
}
