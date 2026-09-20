/*
 * =====================================================================
 *  Next Palindrome Using Same Digits            Educative / GfG | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given a numeric string that is a palindrome, return the smallest palindrome that is
 *   strictly greater and uses exactly the same digits (a rearrangement). Return "" when none
 *   exists. For odd length the middle digit stays where it is.
 *
 * EXAMPLE
 *   "1221"      -> "2112"        left half "12" -> next permutation "21", mirror it
 *   "12321"     -> "21312"       left "12" -> "21", keep the middle '3', mirror
 *   "123454321" -> "124353421"   left "1234" -> "1243"
 *   "54345"     -> ""            left "54" is already its largest arrangement
 *   "999", "9"  -> ""            all digits equal / single digit: nothing greater exists
 *
 * APPROACH  (next permutation on the left half, then mirror)
 *   1. Copy the first n/2 characters into leftHalf (the middle digit, if any, is excluded).
 *   2. nextPermutation(leftHalf), the standard LeetCode 31 routine:
 *        a. from the right find the pivot i with a[i] < a[i+1]; none -> return false
 *        b. from the right find the first j with a[j] > a[i]; swap them
 *        c. reverse the suffix after i (it was descending, so reversing makes it minimal)
 *   3. If there is no next permutation, return "".
 *   4. Rebuild: new left half + (middle digit for odd n) + reverse of the new left half.
 *
 * KEY INSIGHT
 *   A palindrome is fully determined by its left half, so "next palindrome with the same
 *   digits" is exactly "next permutation of the left half". The middle digit is frozen (moving
 *   it would change the digit multiset of the halves) and the right half is never free. Once
 *   the left half strictly increases the mirrored string is guaranteed larger, so no final
 *   comparison against the input is needed.
 *
 * Fixed: empty input threw IndexOutOfBoundsException (pivot check tested i == -1, but the
 *        scan starts at size - 2 = -2); the guard is now i < 0.
 *
 * COMPLEXITY
 *   Time  O(n)  next permutation and the mirroring are both linear
 *   Space O(n)  the left-half copy and the output builder
 *
 * INTERVIEW FOLLOW-UPS
 *   - Next Permutation (LeetCode 31) on its own, in place on an int[].
 *   - Next greater palindrome NOT restricted to the same digits: increment the left half.
 *   - Smallest palindrome with the same digits: sort the left half ascending and mirror.
 *   - Why can the middle digit never move? Try to build a counter-example and show it fails.
 *
 * RUN
 *   main() runs 8 cases and prints actual vs expected.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class NextPermutationUsingSameDigits {

    /** Standard next-permutation (LeetCode 31) on a list; false when already the largest. */
    static boolean nextPermutation(List<Character> digits) {
        int i = digits.size() - 2;
        while (i >= 0 && digits.get(i) >= digits.get(i + 1)) {
            i--;                                    // walk left past the descending suffix
        }
        if (i < 0) return false;                    // whole list descending: no next one

        int j = digits.size() - 1;
        while (digits.get(j) <= digits.get(i)) {
            j--;                                    // rightmost digit greater than the pivot
        }
        Collections.swap(digits, i, j);
        Collections.reverse(digits.subList(i + 1, digits.size()));   // smallest suffix
        return true;
    }

    public static String nextPalindrome(String palindrome) {
        int n = palindrome.length();
        int halfLength = n / 2;

        List<Character> leftHalf = new ArrayList<>();
        for (int i = 0; i < halfLength; i++) {
            leftHalf.add(palindrome.charAt(i));
        }
        if (!nextPermutation(leftHalf)) {
            return "";                              // covers n <= 1, "54345", "999"
        }

        StringBuilder left = new StringBuilder();
        for (char c : leftHalf) left.append(c);

        StringBuilder result = new StringBuilder(left);
        if (n % 2 == 1) {
            result.append(palindrome.charAt(halfLength));   // middle digit is frozen
        }
        result.append(left.reverse());
        return result.toString();
    }

    private static void check(String palindrome, String expected) {
        System.out.println("\"" + palindrome + "\" -> \"" + nextPalindrome(palindrome)
                + "\"   expected \"" + expected + "\"");
    }

    public static void main(String[] args) {
        // typical
        check("1221", "2112");
        check("12321", "21312");
        check("89798", "98789");
        // edge
        check("999", "");
        check("9", "");
        check("", "");
        // tricky: no next arrangement, and a 4-digit left half
        check("54345", "");
        check("123454321", "124353421");
    }
}
