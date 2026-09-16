class RecursiveFactorial {
    public long factorial(int n) {
        // Base case: factorial of 0 or 1 is 1
        if (n <= 1) return 1;
        // Recursive case: n * factorial of n-1
        return n * factorial(n - 1);
    }

    public static void main(String[] args) {
        RecursiveFactorial rf = new RecursiveFactorial();
        int input = 5;
        long result = rf.factorial(input);
        System.out.println("Input: " + input);
        System.out.println("Output (factorial): " + result);
    }
}
