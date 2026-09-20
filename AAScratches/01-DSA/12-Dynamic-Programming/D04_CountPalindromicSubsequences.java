/*
 * =====================================================================
 *  Count Palindromic Subsequences                 LeetCode 2484 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given a string s of digits, count how many of its subsequences of length exactly 5
 *   are palindromes. Subsequences are counted by the positions they use, so two picks
 *   that spell the same digits but use different indices count twice. Return the count
 *   modulo 1e9+7.
 *
 * EXAMPLE
 *   s = "103301"     ->  2   "10301" and "10301" from two different position sets
 *   s = "0000000"    ->  21  every choice of 5 of the 7 zeros works: C(7,5) = 21
 *   s = "9999900000" ->  2   "99999" and "00000"
 *   s = "12321"      ->  1   edge: the whole string is the only length-5 subsequence
 *   s = "123"        ->  0   edge: too short to hold a length-5 subsequence
 *
 * APPROACH  (take / skip recursion with a fixed-length window as the state)
 *   1. A length-5 palindrome looks like a b c b a. Only the first two picked digits
 *      ever need to be remembered - the middle is free, and positions 4 and 5 just have
 *      to repeat the second and first digit.
 *   2. State is (index, first, second, length): how far we are in s, the first digit
 *      picked, the second digit picked, and how many digits are in the pattern so far.
 *   3. At each index, try to TAKE s[index] into the pattern:
 *        length 0 -> record it as first
 *        length 1 -> record it as second
 *        length 2 -> the free middle digit, record nothing
 *        length 3 -> allowed only if the digit equals second
 *        length 4 -> allowed only if the digit equals first
 *      and always also SKIP s[index] and move on with the state unchanged.
 *   4. Base cases: length == 5 is one completed palindrome -> 1. Running off the end of
 *      s with length < 5 -> 0.
 *   5. Memoise on the whole state. It is bounded: index < n, first and second are one of
 *      10 digits, length is 0..4, so the table is n * 10 * 10 * 5.
 *
 * KEY INSIGHT
 *   The modelling leap is refusing to make the state a range. Because the length is
 *   pinned at 5, the only history that matters is the two digits that still need to be
 *   mirrored - so the "shape" of the palindrome is compressed into two small integers
 *   and a counter. Whenever a problem fixes the size of the thing being built, look for
 *   a state made of that fixed, tiny amount of history instead of an interval.
 *
 * COMPLEXITY
 *   Time  O(n * 10 * 10 * 5) = O(500n)  each state solved once with O(1) work.
 *   Space O(n * 10 * 10 * 5)            the memo table, plus O(n) recursion depth.
 *
 * INTERVIEW FOLLOW-UPS
 *   - The O(100n) counting solution: for each middle index, combine prefix and suffix
 *     counts of every two-digit pair - no recursion at all.
 *   - Generalise to length k. Why does the state blow up past k = 5 or 6?
 *   - Count DISTINCT palindromic subsequences instead (LC 730) - what changes?
 *   - Why is the alphabet being digits (10 symbols), not letters, load bearing here?
 *
 * RUN
 *   main() runs 5 cases (two typical, all-equal, exact-length edge, too-short edge) and
 *   prints actual vs expected.
 */

class CountPalindromicSubsequences {

    private static final int MOD = 1_000_000_007;
    private static final int TARGET_LENGTH = 5; // the problem fixes the palindrome length

    public int countPalindromes(String s) {
        // index x first digit (0-9) x second digit (0-9) x how many picked so far (0-4)
        Integer[][][][] memo = new Integer[s.length()][10][10][TARGET_LENGTH];
        return solve(s, 0, 0, 0, 0, memo);
    }

    /**
     * Number of ways to finish a length-5 palindrome using s[index..], given that
     * `length` digits are already picked and the first two of them were `first`/`second`.
     */
    private int solve(String s, int index, int first, int second, int length,
                      Integer[][][][] memo) {
        if (length == TARGET_LENGTH) return 1; // a b c b a completed
        if (index == s.length()) return 0;     // ran out of digits before finishing

        if (memo[index][first][second][length] != null) {
            return memo[index][first][second][length];
        }

        int value = s.charAt(index) - '0';

        // Branch 1: take s[index] as the next digit of the pattern, when it is legal.
        int include = 0;
        if (length == 0) {
            include = solve(s, index + 1, value, second, length + 1, memo); // mirrors slot 5
        } else if (length == 1) {
            include = solve(s, index + 1, first, value, length + 1, memo);  // mirrors slot 4
        } else if (length == 2) {
            include = solve(s, index + 1, first, second, length + 1, memo); // free middle
        } else if (length == 3 && value == second) {
            include = solve(s, index + 1, first, second, length + 1, memo); // must equal 2nd
        } else if (length == 4 && value == first) {
            include = solve(s, index + 1, first, second, length + 1, memo); // must equal 1st
        }

        // Branch 2: skip s[index] entirely and keep the state as it is.
        int exclude = solve(s, index + 1, first, second, length, memo);

        int answer = (include + exclude) % MOD;
        return memo[index][first][second][length] = answer;
    }

    private static void check(CountPalindromicSubsequences sol, String s, int expected) {
        System.out.println("s = \"" + s + "\""
                + "   actual = " + sol.countPalindromes(s)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        CountPalindromicSubsequences sol = new CountPalindromicSubsequences();

        check(sol, "103301", 2);     // typical: LeetCode sample
        check(sol, "9999900000", 2); // typical: two distinct palindromes
        check(sol, "0000000", 21);   // all equal: C(7,5) = 21
        check(sol, "12321", 1);      // edge: string is exactly length 5
        check(sol, "123", 0);        // edge: shorter than 5, nothing to count
    }
}
