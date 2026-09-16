class SweetAndSavory {
    public static void main(String[] args) {
        int[] sweetness = {1, 2, 3};
        int[] savoriness = {3, 4, 5};
        int targetK = 7;
        int[] result = findClosestCombination(sweetness, savoriness, targetK);
        System.out.println("Best combination: Sweet = " + result[0] + ", Savory = " + result[1]);
    }

    public static int[] findClosestCombination(int[] sweetness, int[] savoriness, int targetK) {
        int closestSum = Integer.MAX_VALUE;
        int bestSweet = 0;
        int bestSavory = 0;

        for (int sweet : sweetness) {
            for (int savory : savoriness) {
                int currentSum = sweet + savory;
                if (Math.abs(currentSum - targetK) < Math.abs(closestSum - targetK)) {
                    closestSum = currentSum;
                    bestSweet = sweet;
                    bestSavory = savory;
                }
            }
        }

        return new int[]{bestSweet, bestSavory};
    }
}
