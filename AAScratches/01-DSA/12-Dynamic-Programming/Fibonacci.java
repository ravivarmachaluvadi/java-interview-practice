class Fibonacci {
    static int fibonacci(int N) {
        if (N <= 1)
            return N;

        int last = fibonacci(N - 1);
        int slast = fibonacci(N - 2);

        return last + slast;
    }

    public static void main(String[] args) {

        // Here, let’s take the value of N to be 4.
        int N = 4;
        System.out.println(fibonacci(N)); // 3
    }
}
