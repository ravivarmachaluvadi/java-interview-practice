class LCMOfTwoNumbers {
    private int GCD(int n1, int n2) {
        while (n1 > 0 && n2 > 0) {
            if (n1 > n2) {
                n1 = n1 % n2;
            } else {
                n2 = n2 % n1;
            }
        }
        if (n1 == 0) return n2;
        return n1;
    }

    public int LCM(int n1, int n2) {
        // Function call to find gcd
        int gcd = GCD(n1, n2);
        int lcm = (n1 * n2) / gcd;
        // Return the LCM
        return lcm;
    }

    public static void main(String[] args) {
        int n1 = 3, n2 = 5;
        LCMOfTwoNumbers sol = new LCMOfTwoNumbers();
        int ans = sol.LCM(n1, n2);
        System.out.println("The LCM of " + n1 + " and " + n2 + " is: " + ans);
    }
}
