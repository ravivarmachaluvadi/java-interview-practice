/*
 * =====================================================================
 *  Run-Length Encoding                                 classic | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a string, replace every run of identical consecutive characters with the
 *   character followed by the run length. Runs of length 1 still get a "1", and the same
 *   character can appear in several separate runs (see the two "w" runs below).
 *
 * EXAMPLE
 *   "wwwwaaadexxxxxxywww"  ->  "w4a3d1e1x6y1w3"
 *   "a"                    ->  "a1"
 *   "abc"                  ->  "a1b1c1"     encoding can be LONGER than the input
 *   ""                     ->  ""
 *
 * APPROACH  (counting consecutive runs)
 *   1. Walk i from 0 to n-1 with an outer for loop.
 *   2. At position i start count = 1, then advance i while charAt(i) == charAt(i+1),
 *      incrementing count each step (guard i < n-1 so charAt(i+1) is in range).
 *   3. Append the run character and count, then let the for loop's i++ move to the
 *      first character of the next run.
 *
 * KEY INSIGHT
 *   The inner while loop consumes a whole run and leaves i on its LAST character, so the
 *   outer loop's own i++ lands exactly on the next run's first character. This
 *   "outer loop starts a run, inner loop eats it" shape is the run-scan primitive that
 *   string compression, decomposition and grouping problems all reuse.
 *
 * COMPLEXITY
 *   Time  O(n)  every character is looked at once by exactly one of the two loops
 *   Space O(n)  the StringBuilder holding the result (worst case 2n for "abc...")
 *
 * INTERVIEW FOLLOW-UPS
 *   - Only emit the count when it is greater than 1 (LeetCode 443 String Compression) and
 *     write the result in place using read/write pointers.
 *   - Decode "w4a3" back to the original: parse multi-digit counts, not just one digit.
 *   - Return the input unchanged when the encoding would not be shorter.
 *
 * RUN
 *   main() runs 4 cases (typical, single char, no repeats, empty) and prints actual vs
 *   expected.
 */
class RunLengthEncodingW4A3 {

    static String encode(String str) {
        if (str == null || str.isEmpty()) return ""; // guard before str.length()
        int n = str.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int count = 1;
            // eat the rest of this run; i ends on the run's last character
            while (i < n - 1 && str.charAt(i) == str.charAt(i + 1)) {
                i++;
                count++;
            }
            sb.append(str.charAt(i)).append(count);
        }
        return sb.toString();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 \"wwwwaaadexxxxxxywww\"", encode("wwwwaaadexxxxxxywww"), "w4a3d1e1x6y1w3");
        print("case 2 \"a\"", encode("a"), "a1");
        print("case 3 \"abc\"", encode("abc"), "a1b1c1");
        print("case 4 \"\"", encode(""), "");
    }
}
