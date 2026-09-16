class PowerCheck {
    public static boolean isPowerOf(int base, int power) {
        // Base cases
        if (base <= 0 || power <= 0)
            return false;

        if (power == 1)
            return base == 1;

        while (base % power == 0) {
            base /= power;
        }
        return base == 1;
    }

    public static void main(String[] args) {
        System.out.println(12 % 4);
        System.out.println();
        int x = 64, y = 4;
        boolean result = isPowerOf(x, y);
        if (result) {
            System.out.println(x + " is a power of " + y);
        } else {
            System.out.println(x + " is not a power of " + y);
        }
    }
}
