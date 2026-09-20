/*
 * =====================================================================
 *  Armstrong Number (Narcissistic Number)            Building block | Easy
 * =====================================================================
 *
 * PROBLEM
 *   An Armstrong number equals the sum of its own digits, each raised
 *   to the power of the digit count. Given a non-negative integer n,
 *   say whether it is one. Negative inputs are rejected.
 *
 * EXAMPLE
 *   153      ->  true    3 digits: 1^3 + 5^3 + 3^3 = 1 + 125 + 27 = 153
 *   9474     ->  true    4 digits: 9^4 + 4^4 + 7^4 + 4^4 = 9474
 *   154      ->  false   1 + 125 + 64 = 190, not 154
 *   5        ->  true    every single-digit number is trivially Armstrong
 *   0        ->  true    0^1 = 0
 *   -153     ->  false   negatives are not Armstrong numbers
 *
 * APPROACH  (digit extraction loop, run twice)
 *   1. Reject negatives up front.
 *   2. First pass: count the digits k by dividing a copy by 10 until 0.
 *      Treat 0 as having one digit.
 *   3. Second pass: peel digits off with % 10 and /= 10, adding
 *      digit^k to a running long total.
 *   4. The number is Armstrong when that total equals the original.
 *
 * KEY INSIGHT
 *   %10 and /=10 are the decimal twin of "test the low bit, then shift".
 *   That two-line loop is the reusable part - digit sums, reversing a
 *   number, Happy Number and base conversion are all the same skeleton
 *   with a different accumulator.
 *   Note the power must be integer arithmetic. Math.pow returns a double
 *   and accumulating into an int silently saturates at Integer.MAX_VALUE
 *   for 9- and 10-digit inputs; this version uses a long and an integer
 *   power helper instead.
 *
 * COMPLEXITY
 *   Time  O(d * log k) where d is the digit count (at most 10 for an
 *         int) and the log k comes from fast exponentiation
 *   Space O(1)  a handful of scalars, no digit array
 *
 * INTERVIEW FOLLOW-UPS
 *   - Print every Armstrong number below a limit (loop plus this test)
 *   - Same idea in base b instead of base 10 - what changes?
 *   - Why can an int Armstrong check overflow, and what does a long buy?
 *
 * RUN
 *   main() runs 6 cases (typical, 4-digit, non-Armstrong, single digit,
 *   zero, negative) and prints actual vs expected.
 */
class ArmstrongNumber {

    public boolean isArmstrong(int n) {
        if (n < 0) return false;          // sign has no digit representation here

        int k = countDigits(n);
        long sum = 0;
        int temp = n;
        while (temp > 0) {
            int digit = temp % 10;        // peel the last digit
            sum += power(digit, k);
            temp /= 10;                   // drop it and continue
            if (sum > n) return false;    // early exit: it can only grow
        }
        return sum == n;
    }

    /** Digit count in base 10; 0 counts as one digit. */
    private int countDigits(int n) {
        if (n == 0) return 1;
        int k = 0;
        while (n > 0) {
            k++;
            n /= 10;
        }
        return k;
    }

    /** Integer power by squaring - exact, unlike Math.pow's double result. */
    private long power(long base, int exp) {
        long result = 1;
        while (exp > 0) {
            if ((exp & 1) == 1) result *= base;
            base *= base;
            exp >>= 1;
        }
        return result;
    }

    public static void main(String[] args) {
        ArmstrongNumber armstrong = new ArmstrongNumber();
        int[] inputs = {153, 9474, 154, 5, 0, -153};
        boolean[] expected = {true, true, false, true, true, false};

        for (int i = 0; i < inputs.length; i++) {
            System.out.println("case " + (i + 1) + ": n=" + inputs[i]
                    + " -> " + armstrong.isArmstrong(inputs[i])
                    + "   expected " + expected[i]);
        }
    }
}
