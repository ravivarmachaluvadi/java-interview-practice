import java.util.Arrays;

class ClassPhotos {
    public static boolean canTakeClassPhoto(int[] redShirtHeights, int[] blueShirtHeights) {
        // Sort both arrays in ascending order
        Arrays.sort(redShirtHeights);
        Arrays.sort(blueShirtHeights);

        // Determine which row will be in the back
        boolean redInBack = redShirtHeights[redShirtHeights.length - 1] > blueShirtHeights[blueShirtHeights.length - 1];

        for (int i = 0; i < redShirtHeights.length; i++) {
            // If red is in the back row, each red shirt
            // should be taller than the corresponding blue shirt
            if (redInBack && redShirtHeights[i] <= blueShirtHeights[i])
                return false;

            // If blue is in the back row, each blue shirt
            // should be taller than the corresponding red shirt
            if (!redInBack && blueShirtHeights[i] <= redShirtHeights[i])
                return false;
        }
        return true;
    }

    public static void main(String[] args) {
        int[] redShirtHeights = {5, 8, 1, 3, 4};
        int[] blueShirtHeights = {6, 9, 2, 4, 5};

        boolean result = canTakeClassPhoto(redShirtHeights, blueShirtHeights);
        System.out.println("Can take class photo: " + result);
    }
}
