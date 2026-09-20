/*
 * =====================================================================
 *  Plus One                                          LeetCode 66 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   A non-negative integer is given as an int array of its decimal digits, most
 *   significant digit first, with no leading zeros (except the number 0 itself).
 *   Return the digits of that integer plus one.
 *
 * EXAMPLE
 *   [1, 2, 3]  ->  [1, 2, 4]       no carry
 *   [1, 2, 9]  ->  [1, 3, 0]       carry stops after one step
 *   [9, 9, 9]  ->  [1, 0, 0, 0]    every digit is 9, the result needs one more digit
 *   [0]        ->  [1]             single digit
 *
 * APPROACH  (Right-to-left carry propagation)
 *   1. Walk i from the last digit to the first.
 *   2. If digits[i] < 9: add one, there is no carry, return the array as is.
 *   3. Else the digit is 9: set it to 0 and keep walking (carry the one left).
 *   4. If the loop finishes, every digit was 9: build a new array of length
 *      n + 1 with a leading 1; the remaining slots are already 0.
 *
 * KEY INSIGHT
 *   Adding one can only ripple through a run of trailing 9s, so the scan stops
 *   at the first non-9 digit; the early return makes the common case O(1).
 *   The only real edge case is "all 9s", where the length grows by one.
 *   The same backwards-scan-with-carry shape is add-binary and add-strings.
 *
 * COMPLEXITY
 *   Time  O(n)  worst case all 9s; O(1) when the last digit is not 9
 *   Space O(1)  extra; only the all-9s case allocates the n + 1 output array
 *
 * INTERVIEW FOLLOW-UPS
 *   - Add k instead of 1: carry = (digit + carry) / 10 each step, loop while carry > 0.
 *   - Add two digit arrays (LeetCode 415, add strings): two pointers plus a carry.
 *   - Digits stored least significant first: same loop, walk left to right.
 *   - The method mutates its input; ask whether the caller is fine with that.
 *
 * RUN
 *   main() runs 4 cases (no carry, partial carry, all nines, single digit) and
 *   prints actual vs expected.
 */
import java.util.Arrays;

class PlusOne {

    public static int[] plusOne(int[] digits) {
        int n = digits.length;
        for (int i = n - 1; i >= 0; i--) {
            if (digits[i] < 9) {
                digits[i]++;        // no carry needed, done
                return digits;
            }
            digits[i] = 0;          // 9 + 1 = 10: write 0, carry the 1 to the left
        }
        int[] longer = new int[n + 1];  // all digits were 9: 999 + 1 = 1000
        longer[0] = 1;
        return longer;
    }

    private static void run(String label, int[] digits, String expected) {
        System.out.println(label + ": " + Arrays.toString(plusOne(digits))
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 no carry     ", new int[]{1, 2, 3}, "[1, 2, 4]");
        run("case 2 partial carry", new int[]{1, 2, 9}, "[1, 3, 0]");
        run("case 3 all nines    ", new int[]{9, 9, 9}, "[1, 0, 0, 0]");
        run("case 4 single digit ", new int[]{0}, "[1]");
    }
}
