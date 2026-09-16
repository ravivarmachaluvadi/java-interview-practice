class MinimumDistanceBetweenWords {
    public static int findMinimumDistance(String word1, String word2) {
        // Find the middle index of both words
        int mid1 = (word1.length() - 1) / 2;
        int mid2 = (word2.length() - 1) / 2;

        // Calculate the absolute difference between the middle indices
        int distanceBetweenMiddles = Math.abs(mid1 - mid2);

        // Calculate the remaining characters from both sides of the words
        int remainingCharsWord1 = word1.length() - 1 - mid1;
        int remainingCharsWord2 = word2.length() - 1 - mid2;

        // Sum the distances
        return distanceBetweenMiddles + remainingCharsWord1 + remainingCharsWord2;
    }
    public static void main(String[] args) {
        String word1 = "cccccccc";
        String word2 = "fffffffffffff";

        int result = findMinimumDistance(word1, word2);
        System.out.println("Minimum distance between the words: " + result);
    }
}
