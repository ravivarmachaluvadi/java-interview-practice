class FibonacciNumber {

    public static void main(String[] args) {
        FibonacciNumber solution = new FibonacciNumber();
        int n = 5; // Example input
        System.out.println("Fibonacci number at position " + n + " is " + solution.fib(n));
    }

    private int fib(int n) {
        if (n <= 1) {
            return n; // 5
        }

        return fib(n - 1) + fib(n - 2);
    }
}
