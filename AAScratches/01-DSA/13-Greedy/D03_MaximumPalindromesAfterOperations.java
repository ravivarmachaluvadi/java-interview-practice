/*
 * =====================================================================
 *  Maximum Palindromes After Operations            LeetCode 3035 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of lowercase words, you may repeatedly pick any two indices
 *   i, j and any positions x, y and swap words[i][x] with words[j][y] - any
 *   character may be moved to any slot, any number of times. Word LENGTHS never
 *   change. Return the maximum number of words that can be palindromes at once.
 *
 * EXAMPLE
 *   ["abbb","ba","aa"]  ->  3   all letters are in even supply, every word fits
 *   ["abc","ab"]        ->  2   4 paired chars cover the even part of both words
 *   ["cd","ef","a"]     ->  1   every letter appears once, so no pair exists
 *   ["a"]               ->  1   a length-1 word is a palindrome for free
 *
 * APPROACH  (global parity pool, then fill shortest words first)
 *   1. Because any character can travel anywhere, the words are just LENGTH
 *      SLOTS and the letters are one shared pool. Only the pool matters.
 *   2. A palindrome of length L needs floor(L/2) mirrored PAIRS; if L is odd the
 *      middle slot accepts any leftover single. So count how many characters are
 *      available in pairs: total characters minus the number of letters with an
 *      odd count. Track odd counts with a 26-bit XOR mask - bitCount(mask) is
 *      exactly how many letters have an odd total.
 *   3. Sort the words by length ascending and greedily take each one while the
 *      paired pool still covers its even part (L/2)*2. Break at the first
 *      failure: every later word is at least as long, so it fails too.
 *
 * KEY INSIGHT
 *   Two non-obvious steps stacked. First, unlimited swaps destroy all per-word
 *   identity - only the global multiset and the list of lengths survive, and
 *   from the multiset only its PARITY matters. Second, once you hold a pool of
 *   pairs, the greedy is the easy half: making a short word a palindrome is
 *   never more expensive than making a long one, so spending the pool on the
 *   shortest words maximises the count (classic exchange argument). The odd
 *   leftovers are free - they can only ever land in odd-length middles, and
 *   there is always a middle waiting for them.
 *   Pattern: "resources are fungible" -> reduce to counts/parity, then a sort.
 *
 * COMPLEXITY
 *   Time  O(S + w log w)  S = total characters, w = number of words (the sort)
 *   Space O(w)  a copy of the array so the caller's order is not disturbed
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is bitCount of the XOR mask the count of odd-frequency letters?
 *   - Why sort ascending and not by any other key? State the exchange argument.
 *   - What breaks if swaps were only allowed WITHIN a word?
 *   - What if the alphabet were Unicode instead of 26 letters? (HashMap parity)
 *
 * RUN
 *   main() runs 5 cases (typical, no pairs available, single word, and a mixed
 *   case that exhausts the pool exactly) and prints actual vs expected.
 *
 * Fixed: the method used to sort the caller's array in place, so the input was
 * reordered as a side effect. It now sorts a local copy.
 */

import java.util.Arrays;

class MaximumPalindromesAfterOperations {

    /**
     * Maximum number of words that can simultaneously be palindromes.
     */
    public static int maxPalindromesAfterOperations(String[] words) {
        int totalChars = 0;
        int oddParityMask = 0;   // bit c set  <=>  letter c appears an odd number of times

        for (String w : words) {
            totalChars += w.length();
            for (char c : w.toCharArray()) {
                oddParityMask ^= 1 << (c - 'a');
            }
        }

        // Every odd-count letter contributes one unpairable leftover character.
        int lettersWithOddCount = Integer.bitCount(oddParityMask);
        int pairedChars = totalChars - lettersWithOddCount;

        // Sort a copy: the caller's array order is not ours to change.
        String[] byLength = words.clone();
        Arrays.sort(byLength, (a, b) -> Integer.compare(a.length(), b.length()));

        int palindromes = 0;
        for (String w : byLength) {
            int pairedSlotsNeeded = (w.length() / 2) * 2;   // odd middle slot is free
            if (pairedChars < pairedSlotsNeeded) {
                break;   // sorted ascending, so nothing after this one can fit either
            }
            pairedChars -= pairedSlotsNeeded;
            palindromes++;
        }
        return palindromes;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // typical: every letter is in even supply, so all three words fit
        String[] w1 = {"abbb", "ba", "aa"};
        print("case 1 " + Arrays.toString(w1), maxPalindromesAfterOperations(w1), 3);

        // the odd middle slot is what makes the length-3 word free after "ab"
        String[] w2 = {"abc", "ab"};
        print("case 2 " + Arrays.toString(w2), maxPalindromesAfterOperations(w2), 2);

        // edge: every letter appears once, so there is not a single pair
        String[] w3 = {"cd", "ef", "a"};
        print("case 3 " + Arrays.toString(w3), maxPalindromesAfterOperations(w3), 1);

        // edge: one word of length 1 is always a palindrome
        String[] w4 = {"a"};
        print("case 4 " + Arrays.toString(w4), maxPalindromesAfterOperations(w4), 1);

        // tricky: 3 pairs and 3 leftovers cover lengths 1, 2, 3, 3 exactly
        String[] w5 = {"aaa", "bbb", "cc", "d"};
        print("case 5 " + Arrays.toString(w5), maxPalindromesAfterOperations(w5), 4);

        // the input array must come back in its original order
        print("case 6 input unchanged", Arrays.toString(w5), "[aaa, bbb, cc, d]");
    }
}
