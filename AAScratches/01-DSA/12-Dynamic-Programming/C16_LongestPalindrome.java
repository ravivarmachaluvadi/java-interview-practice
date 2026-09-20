/*
 * =====================================================================
 *  Longest Palindromic Substring        LeetCode 5 | Medium | MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a string s, return the longest contiguous substring of s that reads
 *   the same forwards and backwards. Substring, not subsequence - the
 *   characters must be adjacent. If several are tied for longest, any one of
 *   them is accepted. A single character is a palindrome, so the answer is
 *   never empty for a non-empty input.
 *
 * EXAMPLE
 *   "babad"  ->  "bab"   ("aba" is also correct; both methods return "bab")
 *   "cbbd"   ->  "bb"    even-length palindrome, the case that breaks naive code
 *   "a"      ->  "a"     single character (edge case main() runs)
 *   "abcd"   ->  "a"     no repeats, so every palindrome has length 1
 *
 * APPROACH  (two methods: expand-around-centre, then Manacher's algorithm)
 *
 *   METHOD 1 - expand around centre, O(n^2). Say this one first in an interview.
 *   1. Every palindrome has a centre. There are 2n - 1 of them: n characters
 *      (odd-length palindromes) and n - 1 gaps between characters (even-length).
 *   2. For each centre, push two pointers outwards while the characters match.
 *   3. Remember the longest span seen and slice it out at the end.
 *
 *   METHOD 2 - Manacher's algorithm, O(n). This is the follow-up.
 *   1. Interleave the string with '#': "cbb" becomes "#c#b#b#". Now every
 *      palindrome in the transformed string has odd length, so the even/odd
 *      split from method 1 disappears - one loop handles both.
 *   2. Walk left to right keeping (center, radius) = the palindrome found so
 *      far that reaches furthest right. radius is its right edge index.
 *   3. THE MIRROR TRICK. For the current index i inside that right edge, its
 *      reflection is mirror = 2 * center - i. Because the span from center
 *      outwards is a palindrome, the text around mirror is the text around i
 *      reversed, so the palindrome at i is at least as long as the one at
 *      mirror - it is already known, for free, with no comparisons. The catch
 *      is that the guarantee only holds inside the right edge, so the known
 *      value is capped: palindromeLength[i] = min(radius - i,
 *      palindromeLength[mirror]).
 *   4. Only then expand past that free prefix character by character.
 *   5. If the palindrome at i now reaches further right than radius, it becomes
 *      the new (center, radius).
 *   6. The widest palindromeLength value is the answer's length; in the
 *      transformed string it maps back to the original with
 *      start = (centerIndex - maxLength) / 2.
 *
 * KEY INSIGHT
 *   Manacher's is expand-around-centre with the redundant comparisons deleted.
 *   Work already done on the left half of a known palindrome predicts the right
 *   half, so each centre starts from a free head start instead of from zero.
 *   The min(...) cap is the whole algorithm: past the right edge nothing has
 *   been verified yet, so that is exactly where honest expansion must resume.
 *   Recognise this "reuse the mirror inside a known-good window" pattern - it
 *   is the same idea as the KMP failure function and Z-algorithm boxes.
 *
 * COMPLEXITY
 *   Method 1  Time O(n^2)  2n - 1 centres, each expanding up to O(n) steps
 *             Space O(1)   only indices are stored
 *   Method 2  Time O(n)    radius never decreases and never passes n, so the
 *                          total number of expansion steps across all i is O(n);
 *                          every other step per i is constant time
 *             Space O(n)   the transformed string plus the radius array
 *
 * INTERVIEW FOLLOW-UPS
 *   - Count all palindromic substrings (LC 647): same scan, sum the radii.
 *   - Return the longest palindromic SUBSEQUENCE (LC 516) - different problem,
 *     needs O(n^2) interval DP, centres do not apply.
 *   - Why '#' separators? They force odd length and they also stop a real
 *     character from ever matching a separator, so no boundary special cases.
 *   - Shortest palindrome (LC 214): prepend the fewest chars - KMP on s + rev(s).
 *
 * RUN
 *   main() runs 4 cases (typical "babad", even-length "cbbd", single character,
 *   no-repeat string) and prints both methods' actual answers next to expected.
 */
class LongestPalindrome {

    /**
     * Method 1: expand around every centre. O(n^2) time, O(1) extra space.
     * This is the answer an interviewer expects before Manacher's.
     */
    public static String longestPalindromeExpand(String s) {
        if (s == null || s.length() < 2) {
            return s; // "" and single characters are already palindromes
        }
        int start = 0;
        int maxLength = 1;
        for (int i = 0; i < s.length(); i++) {
            int odd = expandFrom(s, i, i);          // centre on the character
            int even = expandFrom(s, i, i + 1);     // centre on the gap after it
            int best = Math.max(odd, even);
            if (best > maxLength) {
                maxLength = best;
                start = i - (best - 1) / 2;         // walk back to the left end
            }
        }
        return s.substring(start, start + maxLength);
    }

    /** Pushes outwards while the two ends match; returns the palindrome length. */
    private static int expandFrom(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1; // both pointers overshot by one
    }

    /**
     * Method 2: Manacher's algorithm. O(n) time, O(n) space.
     * The mirror trick means each centre starts from a free, already-proven head
     * start instead of expanding from zero.
     */
    public static String longestPalindromeManacher(String s) {
        StringBuilder sPrime = new StringBuilder("#");
        for (char c : s.toCharArray()) {
            sPrime.append(c).append("#");
        }
        int n = sPrime.length();
        int[] palindromeLength = new int[n];
        int center = 0;
        int radius = 0;
        for (int i = 0; i < n; i++) {
            // center: The current center of the palindrome you are examining.
            //
            // i: The index on the right side of the center for which you are
            // calculating the mirror position.
            //
            // mirror: This is the index on the left side of the center,
            // symmetric to i.
            int mirror = 2 * center - i;
            if (i < radius) {
                // Free head start from the mirror, capped at the right edge -
                // beyond radius nothing has been verified yet.
                palindromeLength[i] = Math.min(
                        radius - i,
                        palindromeLength[mirror]
                );
            }

            // Honest expansion resumes only past whatever the mirror gave away.
            while (i + 1 + palindromeLength[i] < n && i - 1 - palindromeLength[i] >= 0
                    && sPrime.charAt(i + 1 + palindromeLength[i])
                            == sPrime.charAt(i - 1 - palindromeLength[i])) {
                palindromeLength[i]++;
            }

            if (i + palindromeLength[i] > radius) {
                center = i; // this palindrome reaches furthest right, so it wins
                radius = i + palindromeLength[i];
            }
        }

        int maxLength = 0;
        int centerIndex = 0;
        for (int i = 0; i < n; i++) {
            if (palindromeLength[i] > maxLength) {
                maxLength = palindromeLength[i];
                centerIndex = i;
            }
        }

        // Map the transformed index back onto the original string.
        int startIndex = (centerIndex - maxLength) / 2;
        return s.substring(
                startIndex,
                startIndex + maxLength
        );
    }

    private static void print(String label, String s, String expected) {
        System.out.println(label + " \"" + s + "\" -> expand \""
                + longestPalindromeExpand(s) + "\", manacher \""
                + longestPalindromeManacher(s) + "\"   expected \"" + expected + "\"");
    }

    public static void main(String[] args) {
        print("case 1 (typical):  ", "babad", "bab");
        print("case 2 (even):     ", "cbbd", "bb");
        print("case 3 (single):   ", "a", "a");
        print("case 4 (no repeat):", "abcd", "a");
    }
}
