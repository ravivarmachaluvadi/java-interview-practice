class PowerOfThree {
    // 3^ 3-> by 3 to 3^2 , 3 then 3/3 =1
    public boolean isPowerOfThree(int n) {
        if (n == 0)
            return false;
        while (n % 3 == 0) {
            n = n / 3;
        }
        return n == 1;
    }

    public static void main(String[] args) {
        System.out.println(isPowerOfThree(27));
        System.out.println(isPowerOfThree(36));
    }
}
