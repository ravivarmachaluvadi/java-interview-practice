
class BinaryExponentiation {

    // Function to calculate b^p using binary exponentiation
    public static long binaryExponentiation(long b, long p) {
        long result = 1;
        long base = b;
        while (p > 0) {
            // If p is odd, multiply the current base to the result
            if ((p & 1) == 1) {
                result *= base;
            }
            // Square the base and halve the exponent
            base *= base;
            p >>= 1;  // equivalent to p = p / 2
        }
        return result;
    }

    public static void main(String[] args) {
        long base = 2;
        long exponent = 6;

        long result = binaryExponentiation(base, exponent);
        System.out.println(base + "^" + exponent + " = " + result);
    }
}
