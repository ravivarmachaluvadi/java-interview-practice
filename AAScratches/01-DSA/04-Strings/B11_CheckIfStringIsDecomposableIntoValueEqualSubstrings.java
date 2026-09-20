/*
 * =====================================================================
 *  Check if String Is Decomposable Into Value-Equal Substrings
 *                                                      LeetCode 1933 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   A value-equal substring is a run of the same digit, e.g. "000". Given a
 *   digit string, return true if it can be split into runs where every run has
 *   length 3 except exactly ONE run of length 2. Runs of length 1 are never
 *   allowed, and there must be exactly one run of length 2 (not zero, not two).
 *
 * EXAMPLE
 *   "000111000"    ->  false   three runs of length 3, no run of length 2
 *   "00011111222"  ->  true    "000" + "111" + "11" + "222"
 *   "011100022233" ->  false   "0" is a run of length 1
 *   "00"           ->  true    a single run of length 2
 *   "0000022"      ->  false   two runs of length 2: "00" and "22"
 *   ""             ->  false   no run of length 2 exists
 *
 * APPROACH  (run-length scan with a mod-3 rule)
 *   1. Walk the string; for each maximal run of one digit count its length.
 *   2. A run of length L can be cut into 3s with a remainder of L % 3:
 *        L % 3 == 0  ->  fine, all 3s
 *        L % 3 == 1  ->  impossible (a leftover 1, or 4 = 2 + 2 which uses
 *                        the single allowed 2 twice) -> return false
 *        L % 3 == 2  ->  this run supplies the one allowed 2; if a previous
 *                        run already did, return false
 *   3. At the end return whether exactly one run supplied a 2.
 *
 * KEY INSIGHT
 *   Reduce a run's length to a remainder mod 3, then track a single boolean
 *   "already used the 2". The run scan (A07_RunLengthEncodingW4A3-length encoding) here feeds a
 *   constraint instead of building an output string.
 *
 * COMPLEXITY
 *   Time  O(n)  each character is consumed once by the inner loop
 *   Space O(1)  one counter and one flag
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does L % 3 == 1 fail even when L = 4? Because 4 = 2 + 2 needs two
 *     runs of 2, and only one is allowed in the whole string.
 *   - Generalise to "all runs of length k except one of length k - 1".
 *   - Return the actual split instead of a boolean (build the substrings).
 *
 * RUN
 *   main() runs 6 cases (typical, edge, tricky) and prints actual vs expected.
 */
class Solution {
    public boolean isDecomposable(String s) {
        boolean usedTheTwo = false; // has some run already supplied the single length-2 piece
        int n = s.length();
        int i = 0;

        while (i < n) {
            char runChar = s.charAt(i);
            int runLength = 0;
            while (i < n && s.charAt(i) == runChar) {
                runLength++;
                i++;
            }

            int remainder = runLength % 3;
            if (remainder == 1) {
                return false; // leftover 1, or 4 = 2 + 2, both impossible
            }
            if (remainder == 2) {
                if (usedTheTwo) return false; // a second run of 2 is not allowed
                usedTheTwo = true;
            }
        }
        return usedTheTwo; // exactly one run of 2 is required
    }
}

class CheckIfStringIsDecomposableIntoValueEqualSubstrings {

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        print("case 1 \"000111000\" (no run of 2)", solution.isDecomposable("000111000"), false);
        print("case 2 \"00011111222\"", solution.isDecomposable("00011111222"), true);
        print("case 3 \"011100022233\" (run of 1)", solution.isDecomposable("011100022233"), false);
        print("case 4 \"00\" (single run of 2)", solution.isDecomposable("00"), true);
        print("case 5 \"0000022\" (two runs of 2)", solution.isDecomposable("0000022"), false);
        print("case 6 \"\" (empty)", solution.isDecomposable(""), false);
    }
}
