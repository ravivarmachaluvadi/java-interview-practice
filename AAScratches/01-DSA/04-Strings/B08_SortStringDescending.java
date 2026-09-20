/*
 * =====================================================================
 *  Sort String in Descending Order                       Practice | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a string, return a string with the same characters sorted from largest
 *   to smallest (by char value). Duplicates stay. The input here is lowercase
 *   letters; the comparator version handles any characters.
 *
 * EXAMPLE
 *   "mupursingh"  ->  "uusrpnmihg"
 *   "aaaa"        ->  "aaaa"     all equal, order unchanged
 *   ""            ->  ""         nothing to sort
 *   "abc"         ->  "cba"      already ascending, fully reversed
 *
 * APPROACH  (sort characters with a comparator)
 *   Version A, sortStringInDescendingOrder (author's):
 *   1. Copy the chars into a Character[] (boxed) because Arrays.sort with a
 *      Comparator only accepts object arrays, not char[].
 *   2. Arrays.sort(chars, Collections.reverseOrder()).
 *   3. Append the sorted chars into a StringBuilder and return it.
 *
 *   Version B, sortDescendingCountingSort (added, O(n) for a-z):
 *   1. Count each letter into int[26].
 *   2. Walk the counts from 'z' down to 'a', emitting each letter count times.
 *
 * KEY INSIGHT
 *   A sorted string is the canonical form of an anagram class, so "sort the chars"
 *   shows up inside group-anagrams and friends. The Java gotcha: char[] cannot take
 *   a Comparator, so either box to Character[], sort ascending and reverse the
 *   builder, or skip comparison sorting entirely with a counting sort when the
 *   alphabet is small.
 *
 * COMPLEXITY
 *   Time  O(n log n)  comparator sort (Version A); O(n + 26) counting sort (B)
 *   Space O(n)        the boxed copy and the result (A); O(26 + n) for B
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not Arrays.sort(char[]) then reverse? That works and avoids boxing;
 *     the comparator version only matters when the ordering is custom.
 *   - Sort by frequency instead of char value (LeetCode 451)? Count, then sort
 *     the distinct chars by count descending.
 *   - Case-insensitive descending? Use String.CASE_INSENSITIVE_ORDER reversed on
 *     String tokens, or map to lower case before comparing.
 *
 * RUN
 *   main() runs 4 cases (typical, all equal, empty, already ascending) through both
 *   versions and prints actual vs expected.
 */
import java.util.Arrays;
import java.util.Collections;

class SortStringDescending {

    /** Version A: box to Character[] so a Comparator can be used. */
    public static String sortStringInDescendingOrder(String input) {
        Character[] chars = new Character[input.length()];
        for (int i = 0; i < input.length(); i++) {
            chars[i] = input.charAt(i);
        }

        // Arrays.sort(char[]) has no comparator overload; the boxed array does
        Arrays.sort(chars, Collections.reverseOrder());

        StringBuilder sorted = new StringBuilder(chars.length);
        for (Character ch : chars) {
            sorted.append(ch);
        }
        return sorted.toString();
    }

    /** Version B: counting sort, valid because the input is lowercase a-z only. */
    public static String sortDescendingCountingSort(String input) {
        int[] count = new int[26];
        for (char c : input.toCharArray()) {
            count[c - 'a']++;
        }

        StringBuilder sorted = new StringBuilder(input.length());
        for (int i = 25; i >= 0; i--) {          // walk from 'z' down to 'a'
            for (int k = 0; k < count[i]; k++) {
                sorted.append((char) ('a' + i));
            }
        }
        return sorted.toString();
    }

    private static void print(String label, Object actual, Object expected) {
        // brackets make the empty-string case visible
        System.out.println(label + ": [" + actual + "]   expected [" + expected + "]");
    }

    public static void main(String[] args) {
        String[] inputs = {"mupursingh", "aaaa", "", "abc"};
        String[] expected = {"uusrpnmihg", "aaaa", "", "cba"};

        for (int i = 0; i < inputs.length; i++) {
            String s = inputs[i];
            print("case " + (i + 1) + " comparator \"" + s + "\"",
                    sortStringInDescendingOrder(s), expected[i]);
            print("case " + (i + 1) + " counting   \"" + s + "\"",
                    sortDescendingCountingSort(s), expected[i]);
        }
    }
}
