class InfosysGrumpyOwnerOptimised {

    public static int maxSatisfied(int[] customers, int[] grumpy, int minutes) {
        // Customers already satisfied when owner is not grumpy
        int satisfied = 0;

        for (int i = 0; i < customers.length; i++) {
            if (grumpy[i] == 0) {
                satisfied += customers[i];
            }
        }

        // Extra customers that can be satisfied using the secret technique
        int extra = 0;

        // First window
        for (int i = 0; i < minutes; i++) {
            if (grumpy[i] == 1) {
                extra += customers[i];
            }
        }

        int maxExtra = extra;

        // Sliding window
        for (int i = minutes; i < customers.length; i++) {
            if (grumpy[i] == 1) extra += customers[i];
            if (grumpy[i - minutes] == 1) extra -= customers[i - minutes];
            maxExtra = Math.max(maxExtra, extra);
        }
        return satisfied + maxExtra;
    }

    public static void main(String[] args) {

        int[] customers = {3, 2, 5, 10, 20, 5};
        int[] grumpy = {1, 1, 1, 0, 0, 1};
        int minutes = 3;

        int result = maxSatisfied(customers, grumpy, minutes);

        System.out.println("Maximum satisfied customers = " + result);
    }
}
