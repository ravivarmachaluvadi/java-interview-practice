class ArmstrongNumber {
    public boolean isArmstrong(int n) {
        int original = n, temp = n, k = 0;
        // Step 1: Count digits
        while (temp > 0) {
            k++;
            temp /= 10;
        }
        int sum = 0;
        temp = n;
        // Step 2: Compute sum of k-th power of digits
        while (temp > 0) {
            int digit = temp % 10;
            sum += Math.pow(digit, k);
            temp /= 10;
        }
        return sum == original;
    }

    public static void main(String[] args) {
        ArmstrongNumber armstrong = new ArmstrongNumber();
        int number = 153; // Example number
        if (armstrong.isArmstrong(number)) {
            System.out.println(number + " is an Armstrong number.");
        } else {
            System.out.println(number + " is not an Armstrong number.");
        }
    }
}
