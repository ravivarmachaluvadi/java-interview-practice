/**
 * Problem:
 *   Add two non‑negative integers represented as decimal strings without using
 *   built‑in big integer libraries.
 *
 * Approach:
 *   Process both strings from least significant digit to most, summing digits
 *   with carry. Append each result digit to a StringBuilder and reverse at the end.
 *
 * Complexity:
 *   Time:  O(max(n, m)) where n and m are lengths of the input strings.
 *   Space: O(max(n, m)) for the resulting string (plus constant auxiliary space).
 */
class AddStrings {
    public static String addStrings(String num1, String num2) {
        StringBuilder result = new StringBuilder();
        int carry = 0;
        int i = num1.length() - 1;
        int j = num2.length() - 1;
        // Process both strings from the end to the beginning
        while (i >= 0 || j >= 0 || carry != 0) {
            int digit1 = i >= 0 ? num1.charAt(i) - '0' : 0;
            int digit2 = j >= 0 ? num2.charAt(j) - '0' : 0;
            int sum = digit1 + digit2 + carry;

            // Compute new digit and carry
            result.append(sum % 10);
            carry = sum / 10;
            // Move to the next digits
            i--;
            j--;
        }
        // Reverse result since we were adding digits from the end
        return result.reverse().toString();
    }

    public static void main(String[] args) {
        // Test example
        String num1 = "456";
        String num2 = "77";
        String result = addStrings(num1, num2);
        System.out.println("The sum of " + num1 + " and " + num2 + " is: " + result);

        // Additional test case
        num1 = "123";
        num2 = "987";
        result = addStrings(num1, num2);
        System.out.println("The sum of " + num1 + " and " + num2 + " is: " + result);
    }
}
