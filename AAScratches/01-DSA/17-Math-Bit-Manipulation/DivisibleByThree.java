class DivisibleByThree {
    public static boolean canFormDivisibleBy3(int[] digits) {
        int sum = 0;
        for (int digit : digits) sum += digit;
        return sum % 3 == 0;
    }

    public static void main(String[] args) {
        int[] digits1 = {1, 4, 1, 6};
        int[] digits2 = {1, 4, 5};

        System.out.println("Can form divisible by 3 (digits1): " + canFormDivisibleBy3(digits1)); // true
        System.out.println("Can form divisible by 3 (digits2): " + canFormDivisibleBy3(digits2)); // false
    }
}

