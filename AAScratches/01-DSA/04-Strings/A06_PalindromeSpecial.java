/*
 * =====================================================================
 *  Valid Palindrome (letters only)          LeetCode 125 variant | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a sentence, decide whether it reads the same forwards and backwards when case
 *   and every non-letter character (spaces, punctuation, digits) are ignored.
 *   The author's version keeps only a-z. LeetCode 125 keeps letters AND digits; that
 *   exact version is included as a second method so the difference is visible.
 *
 * EXAMPLE
 *   "Was it a car or a cat I saw?"  ->  true    letters read "wasitacaroracatisaw"
 *   "race a car"                    ->  false   "raceacar" is not a palindrome
 *   ""                              ->  true    nothing to compare
 *   "0P"                            ->  true (letters only) / false (alphanumeric, LC 125)
 *
 * APPROACH  (two pointers with skips)
 *   1. Lower-case the string once so 'W' and 'w' compare equal.
 *   2. left = 0, right = n - 1. While left <= right:
 *      a. advance left while it sits on a non-letter (guard left < right so it cannot
 *         run past right);
 *      b. retreat right the same way;
 *      c. if the two letters differ, answer false; else step both pointers inwards.
 *   3. If the pointers cross without a mismatch, answer true.
 *
 * KEY INSIGHT
 *   Converging pointers do the palindrome check in one pass with O(1) extra space; the
 *   only subtlety is the two inner "skip" loops, which must be bounded by left < right or
 *   they walk off the end on inputs like "!!!". The same skeleton with a different
 *   "keep this char?" predicate solves every "ignore X" palindrome variant.
 *
 * COMPLEXITY
 *   Time  O(n)  each pointer moves at most n steps in total
 *   Space O(1)  beyond the lower-cased copy (avoidable with toLowerCase per char)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Keep digits too (LeetCode 125 exact): swap the predicate for Character.isLetterOrDigit.
 *   - Valid Palindrome II (LeetCode 680): allow deleting at most one char; on a mismatch
 *     try skipping either side and check the rest with the same helper.
 *   - Do it without lower-casing the whole string: Character.toLowerCase on each compare.
 *
 * RUN
 *   main() runs 7 checks (typical, false, empty, punctuation-only, digit tricky) and prints
 *   actual vs expected.
 */
class PalindromeSpecial {

    /** Author's version: only a-z letters count, everything else is skipped. */
    static boolean isPalindromeLettersOnly(String input) {
        String str = input.toLowerCase();
        int left = 0, right = str.length() - 1;
        while (left <= right) {
            // skip forward over non-letters; left < right stops the walk at the boundary
            while (left < right && !isLowerLetter(str.charAt(left))) left++;
            // skip backward over non-letters
            while (left < right && !isLowerLetter(str.charAt(right))) right--;
            if (str.charAt(left) != str.charAt(right)) return false;
            left++;
            right--;
        }
        return true;
    }

    /** LeetCode 125 exact version: letters and digits both count. */
    static boolean isPalindromeAlphanumeric(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) left++;
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) right--;
            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    private static boolean isLowerLetter(char c) {
        return c >= 'a' && c <= 'z';
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        String classic = "Was it a car or a cat I saw?";
        print("case 1 letters-only  " + classic, isPalindromeLettersOnly(classic), true);
        print("case 2 letters-only  \"race a car\"", isPalindromeLettersOnly("race a car"), false);
        print("case 3 letters-only  \"\"", isPalindromeLettersOnly(""), true);
        print("case 4 letters-only  \"!!!\"", isPalindromeLettersOnly("!!!"), true);
        // tricky: digits are ignored by the letters-only version but not by LeetCode 125
        print("case 5 letters-only  \"0P\"", isPalindromeLettersOnly("0P"), true);
        print("case 5 alphanumeric  \"0P\"", isPalindromeAlphanumeric("0P"), false);
        print("case 6 alphanumeric  " + classic, isPalindromeAlphanumeric(classic), true);
    }
}
