class ValidNumber {
    // if else if else if solution
    public static boolean isNumber(String s) {
        s = s.trim(); // Remove leading and trailing spaces
        if (s.isEmpty()) return false;

        // Flags to track the status of the string
        boolean seenDigit = false, seenDot = false, seenE = false;

        // Traverse the string character by character
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            // Check for digits
            if (Character.isDigit(c)) {
                seenDigit = true;
            }
            // Check for decimal point
            else if (c == '.') {
                // Can't have multiple dots or a dot after 'e'
                if (seenDot || seenE) return false; //
                seenDot = true;
            }
            // Check for 'e' or 'E' (exponent part)
            else if (c == 'e' || c == 'E') {
                // 'e' cannot appear before a digit or twice
                if (!seenDigit || seenE) return false; //
                seenE = true;
                // After 'e', we expect a number (reset seenDigit)
                seenDigit = false;
            }
            // Check for signs '+' or '-' (at the beginning or after 'e')
            else if (c == '+' || c == '-') {
                if (i > 0 && (s.charAt(i - 1) != 'e' && s.charAt(i - 1) != 'E')) {
                    // '+' or '-' can only appear at the start or after 'e'
                    return false;
                }
            } else {
                return false;
            }
        }

        return seenDigit;
    }

    public static void main(String[] args) {
        // Example input:
        String s = "0.1";
        // Expected output: true (0.1 is a valid number)

        boolean result = isNumber(s);
        System.out.println("Is the string a valid number? " + result);
    }
}
