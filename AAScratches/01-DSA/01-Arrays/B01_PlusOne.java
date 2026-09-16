/**
 * Problem: Increment a non‑negative integer represented as an array of its decimal digits.
 *
 * Approach: Scan the array from least significant digit to most. If a digit is less than 9,
 * increment it and return immediately. Otherwise set it to 0 and continue. If all digits were
 * 9, create a new array with one extra slot, set the leading digit to 1, and return.
 *
 * Time Complexity: O(n) – each digit examined at most once.
 * Space Complexity: O(1) auxiliary (excluding the output array).
 */
public static int[] plusOne(int[] digits) {
    int n = digits.length;
    for (int i = n - 1; i >= 0; i--) {
        if (digits[i] < 9) {
            digits[i]++;
            return digits;
        }
        // remember
        digits[i] = 0;
    }
    int[] newNumber = new int[n + 1];
    newNumber[0] = 1;
    return newNumber;
}

void main() {
    int[] plussed = plusOne(new int[]{9, 9, 9});
    String string = Arrays.toString(plussed);
    IO.println(string);
}
