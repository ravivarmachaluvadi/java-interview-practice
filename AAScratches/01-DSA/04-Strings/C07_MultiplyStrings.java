/**
 * Multiplies two non‑negative integer numbers represented as decimal strings and returns the product as a string.
 *
 * The algorithm simulates manual multiplication: for each digit of num1 (from least significant to most),
 * multiply it by every digit of num2, add the partial products into an array that stores each position
 * of the final result, handling carries on the fly. After processing all digits, leading zeros are skipped.
 *
 * Time Complexity: O(m × n), where m and n are the lengths of the input strings.
 * Space Complexity: O(m + n) for the integer array that holds intermediate results.
 */
class MultiplyStrings {
    public static String multiply(String num1, String num2) {
        int m = num1.length(), n = num2.length();
        int[] pos = new int[m + n];

        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                int mul = (num1.charAt(i) - '0') * (num2.charAt(j) - '0');
                // p1 as left , p2 as right -> add remainder at right
                // add carry at left means p1
                int p1 = i + j, p2 = i + j + 1;

                // PREV carryover is NOW base
                // BUT don't want to read pos[p2]
                int sum = mul + pos[p2];

                pos[p1] += sum / 10;
                pos[p2] = sum % 10;
            }
        }

        StringBuilder sb = new StringBuilder();

        // avoids leading zeros
        for (int p : pos)
            if (!(sb.length() == 0 && p == 0))
                sb.append(p);

        // 0 edge case
        return sb.length() == 0 ? "0" : sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(multiply("123", "456")); // 56088
    }
}