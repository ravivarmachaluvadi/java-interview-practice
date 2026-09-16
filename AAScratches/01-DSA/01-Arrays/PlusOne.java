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
