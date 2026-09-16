
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
