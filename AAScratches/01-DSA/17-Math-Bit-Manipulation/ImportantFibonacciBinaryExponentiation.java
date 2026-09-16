class ImportantFibonacciBinaryExponentiation {

    // Function to multiply two 2x2 matrices
    public static long[][] multiplyMatrices(long[][] A, long[][] B) {
        long[][] result = new long[2][2];

        result[0][0] = A[0][0] * B[0][0] + A[0][1] * B[1][0];
        result[0][1] = A[0][0] * B[0][1] + A[0][1] * B[1][1];
        result[1][0] = A[1][0] * B[0][0] + A[1][1] * B[1][0];
        result[1][1] = A[1][0] * B[0][1] + A[1][1] * B[1][1];

        return result;
    }

    // Function to perform matrix exponentiation
    public static long[][] matrixPower(long[][] base, long exp) {
        long[][] result = {{1, 0}, {0, 1}}; // Identity matrix

        while (exp > 0) {
            // If exp is odd, multiply base with result
            if ((exp & 1) == 1)
                result = multiplyMatrices(result, base);

            base = multiplyMatrices(base, base); // Square the base
            exp >>= 1; // Equivalent to exp = exp / 2
        }
        return result;
    }

    // Function to find the nth Fibonacci number
    public static long nthFibonacci(long n) {
        if (n == 0) return 0;
        if (n == 1) return 1;

        long[][] baseMatrix = {{1, 1}, {1, 0}};
        long[][] resultMatrix = matrixPower(baseMatrix, n - 1);

        // The nth Fibonacci number is stored in resultMatrix[0][0]
        return resultMatrix[0][0];
    }

    public static void main(String[] args) {
        long n = 10; // Example: find the 10th Fibonacci number
        long fib = nthFibonacci(n);

        System.out.println("Fibonacci number F(" + n + ") = " + fib);
    }
}
