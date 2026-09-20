/*
 * =====================================================================
 *  Max Difference You Can Get From Changing an Integer  LeetCode 1432 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Start from an integer num. One operation: pick a digit x and a digit y, and replace
 *   EVERY occurrence of x in num with y. Apply one operation to get a, and (starting
 *   from num again) one operation to get b. Return the largest possible a - b.
 *   Neither a nor b may have a leading zero, and b may not be 0.
 *
 * EXAMPLE
 *   num = 555     ->  888     a = 999 (5->9), b = 111 (5->1)
 *   num = 9       ->  8       a = 9   (nothing to raise), b = 1 (9->1)
 *   num = 123456  ->  820000  a = 923456 (1->9), b = 103456 (2->0)
 *   num = 1010    ->  8080    a = 9090 (1->9), b = 1010 (no legal shrink)
 *
 * APPROACH  (two independent digit-level greedies)
 *   Maximise a:
 *     1. Scan left to right for the first digit that is not '9'.
 *     2. Replace all copies of that digit with '9'. The leftmost raisable digit gains
 *        the most place value, and raising it costs nothing elsewhere.
 *   Minimise b:
 *     3. If the first digit is not '1', replace all copies of it with '1'. It cannot go
 *        to '0' (leading zero), and '1' is the smallest legal value.
 *     4. If the first digit IS '1', it is already minimal, so scan from index 1 for the
 *        first digit that is neither '0' nor '1' and replace all its copies with '0'.
 *        Skipping '1' matters: turning the other '1's into '0' would zero the lead digit.
 *     5. If no such digit exists, b = num unchanged.
 *   Answer is a - b.
 *
 * KEY INSIGHT
 *   The two halves never interact, so solve them separately instead of searching all
 *   81 (x, y) pairs. Within each half, place value means the LEFTMOST digit you are
 *   allowed to move dominates every digit to its right, so one commit ends the search.
 *   The whole difficulty of the problem lives in the leading-zero constraint: it is what
 *   forces the '1' special case in the minimisation. Pattern: when a score splits into
 *   independent terms, maximise each term on its own and enumerate the constraint's
 *   edge cases on paper before writing the loop.
 *
 * COMPLEXITY
 *   Time  O(n)   n = number of digits; each scan and each replace is linear in n
 *   Space O(n)   the string copies a and b
 *
 * INTERVIEW FOLLOW-UPS
 *   - What changes if you are allowed two operations per number?
 *   - Why is brute-forcing all 100 (x, y) pairs also acceptable here, and when is it not?
 *   - Show a case where replacing the first non-'9' digit with '9' is NOT optimal (none
 *     exists: argue why).
 *   - Handle a num too large for int, supplied as a String.
 *
 * RUN
 *   main() runs 4 cases: typical repeated digit, single digit, mixed digits,
 *   and the leading-'1' case where b cannot shrink at all.
 */
class MaxDifferenceChangingInteger {

    public static int maxDiff(int num) {
        String s = String.valueOf(num);

        // ---- maximum: raise the leftmost non-'9' digit (all its copies) to '9' ----
        String a = s;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '9') {
                a = s.replace(c, '9');   // char replace hits EVERY occurrence, as required
                break;
            }
        }

        // ---- minimum: drop the leftmost digit we are allowed to drop ----
        String b = s;
        char first = s.charAt(0);
        if (first != '1') {
            b = s.replace(first, '1');   // '0' is illegal for a leading digit, so '1'
        } else {
            for (int i = 1; i < s.length(); i++) {
                char c = s.charAt(i);
                // skip '0' (already minimal) and '1' (zeroing it would kill the lead digit)
                if (c != '0' && c != '1') {
                    b = s.replace(c, '0');
                    break;
                }
            }
        }

        return Integer.parseInt(a) - Integer.parseInt(b);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (repeated digit) num=555   ", maxDiff(555), 888);
        print("case 2 (single digit)   num=9     ", maxDiff(9), 8);
        print("case 3 (mixed digits)   num=123456", maxDiff(123456), 820000);
        print("case 4 (lead 1, no cut) num=1010  ", maxDiff(1010), 8080);
    }
}
