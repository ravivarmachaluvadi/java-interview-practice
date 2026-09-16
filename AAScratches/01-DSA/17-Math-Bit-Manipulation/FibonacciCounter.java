
class FibonacciCounter {
    public static int countFibonacciInRange(int L, int R) {
        // Edge case
        if (R < 0) return 0; // No Fibonacci numbers below 0
        if (L <= 0) L = 1; // Start from the first Fibonacci number

        // Fibonacci initialization
        int a = 0; // F(0)
        int b = 1; // F(1)
        int count = 0;

        // Count Fibonacci numbers in the range [L, R]
        while (b <= R) {
            if (b >= L) {
                count++;
            }
            int temp = b;
            b = a + b; // Next Fibonacci number
            a = temp;
        }
        return count;
    }

    public static void main(String[] args) {
        int L = 5;
        int R = 21;
        System.out.println("Count of Fibonacci numbers in range [" + L + ", " + R + "] is: " + countFibonacciInRange(L, R));
    }
}
