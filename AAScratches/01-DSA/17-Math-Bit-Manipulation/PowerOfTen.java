/**
 * Determines whether an integer is a positive power of ten.
 *
 * The method checks that the number is greater than zero and repeatedly
 * divides it by 10 while verifying each remainder is zero. If the loop
 * reaches one, the original number was exactly 10^k for some k ≥ 0.
 *
 * Time Complexity: O(log₁₀ n) – proportional to the number of digits.
 * Space Complexity: O(1) – only a few integer variables are used.
 */

class PowerOfTen {
    public static boolean isPowerOfTen(int n) {
        if (n <= 0) return false;
        while (n > 1) {
            if (n % 10 != 0) return false;
            n /= 10;
        }
        return true;
    }
    public static void main(String[] args) {
        int num = 1000; // Example number
        if (isPowerOfTen(num)) {
            System.out.println(num + " is a power of 10.");
        } else {
            System.out.println(num + " is not a power of 10.");
        }
    }
}
