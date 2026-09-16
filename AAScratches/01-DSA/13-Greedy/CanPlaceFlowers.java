class CanPlaceFlowers {

    // return true if n new flowers can be planted in the flowerbed
    // without violating the no-adjacent-flowers rule and false otherwise.
    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        int count = 0;
        for (int i = 0; i < flowerbed.length; i++) {
//            [0 , 0 , 0]
            if (flowerbed[i] == 0
                    && (i == 0 || flowerbed[i - 1] == 0)
                    && (i == flowerbed.length - 1 || flowerbed[i + 1] == 0)) {
                flowerbed[i] = 1; // Place a flower here
                count++;

                if (count >= n) return true;
            }
        }
        return count >= n;
    }

    public static void main(String[] args) {
        CanPlaceFlowers solution = new CanPlaceFlowers();

        int[] flowerbed1 = {1, 0, 0, 0, 1};
        int n1 = 1;
        System.out.println("Example 1: " + solution.canPlaceFlowers(flowerbed1, n1)); // Expected output: true

        int[] flowerbed2 = {1, 0, 0, 0, 1};
        int n2 = 2;
        System.out.println("Example 2: " + solution.canPlaceFlowers(flowerbed2, n2)); // Expected output: false
    }
}
