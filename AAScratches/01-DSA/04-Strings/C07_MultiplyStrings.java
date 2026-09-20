/*
 * =====================================================================
 *  Multiply Strings                                    LeetCode 43 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given two non-negative integers num1 and num2 as decimal strings, return their
 *   product as a string. You may not convert the inputs to integers directly (they can
 *   be up to 200 digits) and must not produce leading zeros.
 *
 * EXAMPLE
 *   num1 = "123", num2 = "456"   ->  "56088"
 *   num1 = "2",   num2 = "3"     ->  "6"
 *   num1 = "0",   num2 = "12345" ->  "0"          (product is zero: must not print "00000")
 *   num1 = "999", num2 = "999"   ->  "998001"     (carries chain across several positions)
 *   num1 = "123456789", num2 = "987654321"  ->  "121932631112635269"  (beyond int range)
 *
 * APPROACH  (positional partial products)
 *   1. A product of an m-digit and an n-digit number has at most m + n digits, so allocate
 *      int[] pos of length m + n, index 0 = most significant.
 *   2. For every pair (i, j) walking both strings right to left, the digit product
 *      num1[i] * num2[j] lands at positions p1 = i + j (tens) and p2 = i + j + 1 (units).
 *   3. Add the product to whatever is already sitting at pos[p2], write the units digit
 *      back to pos[p2] and push the carry into pos[p1]. pos[p1] may temporarily exceed 9;
 *      it is normalised when a later (i, j) pair treats it as its own p2.
 *   4. Skip leading zeros while building the output; if nothing was appended, return "0".
 *
 * KEY INSIGHT
 *   The whole problem is the index mapping: digit i of num1 times digit j of num2
 *   contributes to result positions i + j and i + j + 1 (counting from the left in an
 *   array of length m + n). Everything else is the carry loop from AddStrings (A08).
 *   Recognise this shape whenever you must do arithmetic on numbers too big for a long:
 *   simulate the schoolbook method on a digit array.
 *
 * COMPLEXITY
 *   Time  O(m * n)   every digit pair is multiplied once
 *   Space O(m + n)   the position array (plus the output StringBuilder)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does pos[p1] never overflow an int even though it is not normalised at once?
 *   - Handle a sign or a decimal point in the inputs.
 *   - Faster than O(m * n)? Karatsuba is O(n^1.585); FFT multiplication is O(n log n).
 *   - Related warm-ups: Add Strings (LC 415) and Add Binary (LC 67) use the same carry loop.
 *
 * RUN
 *   main() runs 5 cases (typical, edge, tricky) and prints actual vs expected.
 */
class MultiplyStrings {

    public static String multiply(String num1, String num2) {
        int m = num1.length(), n = num2.length();
        int[] pos = new int[m + n];   // index 0 is the most significant digit

        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                int mul = (num1.charAt(i) - '0') * (num2.charAt(j) - '0');

                // The digit pair (i, j) lands on two adjacent result positions:
                // p1 = i + j takes the carry (left), p2 = i + j + 1 takes the units (right).
                int p1 = i + j, p2 = i + j + 1;

                // Whatever an earlier pair left at p2 is part of this position's value.
                int sum = mul + pos[p2];

                pos[p1] += sum / 10;  // carry; may exceed 9 for now, normalised later
                pos[p2] = sum % 10;   // units digit for this position is now final
            }
        }

        // Build the answer, dropping leading zeros only (a zero after a non-zero must stay).
        StringBuilder sb = new StringBuilder();
        for (int digit : pos) {
            if (!(sb.length() == 0 && digit == 0)) {
                sb.append(digit);
            }
        }

        // Every position was zero: the product is 0.
        return sb.length() == 0 ? "0" : sb.toString();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical       ", multiply("123", "456"), "56088");
        print("case 2 single digits ", multiply("2", "3"), "6");
        print("case 3 zero operand  ", multiply("0", "12345"), "0");
        print("case 4 chained carry ", multiply("999", "999"), "998001");
        print("case 5 beyond int    ", multiply("123456789", "987654321"), "121932631112635269");
    }
}
