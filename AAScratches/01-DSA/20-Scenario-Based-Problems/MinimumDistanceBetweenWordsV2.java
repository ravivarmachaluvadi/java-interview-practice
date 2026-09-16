class MinimumDistanceBetweenWordsV2 {
    public static int findMinDistance(String[] words, String word1, String word2) {
        int minDistance = Integer.MAX_VALUE;
        int lastPositionWord1 = -1;
        int lastPositionWord2 = -1;

        for (int i = 0; i < words.length; i++) {
            if (words[i].equals(word1)) {
                lastPositionWord1 = i;
                if (lastPositionWord2 != -1) {
                    minDistance = Math.min(minDistance, Math.abs(lastPositionWord1 - lastPositionWord2));
                }
            } else if (words[i].equals(word2)) {
                lastPositionWord2 = i;
                if (lastPositionWord1 != -1) {
                    minDistance = Math.min(minDistance, Math.abs(lastPositionWord1 - lastPositionWord2));
                }
            }
        }
        return (minDistance == Integer.MAX_VALUE) ? -1 : minDistance;
    }

    public static void main(String[] args) {

        // Example list of words
        String[] words = {"the", "quick", "brown", "fox", "quick", "jumps", "over", "the", "lazy", "dog"};
        String word1 = "quick";
        String word2 = "the";

        int distance = findMinDistance(words, word1, word2);

        System.out.println("The minimum distance between '" + word1 + "' and '" + word2 + "' is: " + distance);
    }
}
