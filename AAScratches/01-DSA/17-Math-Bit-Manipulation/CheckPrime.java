class PrimeCheck {

    public static boolean isPrime(int n) {
        if (n <= 1) return false;   // 0 and 1 are not prime

        for (int i = 2; i <= Math.sqrt(n); i++)
            if (n % i == 0) return false;  // found a divisor

        return true;
    }

    public static void main(String[] args) {
        int[] testNumbers = {1, 2, 3, 4, 5, 16, 17, 19, 20, 23};
        for (int num : testNumbers) {
            System.out.println(num + " is prime? " + isPrime(num));
        }
    }
}
