class AddTwoFractions {

    // Function to compute the greatest
    // common divisor (GCD) of two numbers
    // abba bab
    public static int gcd(int a, int b) {
        if (b == 0) return a;
        return gcd(b, a % b);
    }

    // Function to add two fractions
    public static String addFractions(int num1, int den1, int num2, int den2) {
        // Find a common denominator
        int commonDenominator = den1 * den2;

        // Add the numerators
        int resultNumerator = den2 * num1 + den1 * num2;

        // Simplify the fraction by dividing by the GCD of the numerator and denominator
        int gcd = gcd(resultNumerator, commonDenominator);
        resultNumerator /= gcd;
        commonDenominator /= gcd;

        return resultNumerator + "/" + commonDenominator;
    }

    public static void main(String[] args) {
        int num1 = 1, den1 = 3;  // First fraction: 1/3
        int num2 = 2, den2 = 5;  // Second fraction: 2/5
        String result = addFractions(num1, den1, num2, den2);
        System.out.println("The sum of the fractions is: " + result);
    }
}
