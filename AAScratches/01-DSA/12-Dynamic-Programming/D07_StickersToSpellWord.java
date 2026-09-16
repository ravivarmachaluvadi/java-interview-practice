import java.util.*;

class StickersToSpellWord {
    // Function to find the minimum number of stickers to spell the word
    public static int minStickers(String[] stickers, String target) {
        // Map to store the frequency of characters in target string
        int[] targetCount = new int[26];
        for (char c : target.toCharArray()) {
            targetCount[c - 'a']++;
        }
        // dp array to store the minimum number of stickers for each state of target
        Map<String, Integer> memo = new HashMap<>();

        // Helper function for memoization
        return helper(stickers, targetCount, memo);
    }

    // Helper function to calculate minimum stickers needed using memoization
    private static int helper(String[] stickers, int[] targetCount, Map<String, Integer> memo) {
        String targetKey = Arrays.toString(targetCount); // Create a key for memoization
        if (memo.containsKey(targetKey)) {
            return memo.get(targetKey);
        }

        int result = Integer.MAX_VALUE;
        for (String sticker : stickers) {
            // Create a new target count for each sticker
            int[] newTargetCount = targetCount.clone();
            boolean usedSticker = false;

            // Reduce the target count based on the sticker
            for (char c : sticker.toCharArray()) {
                if (newTargetCount[c - 'a'] > 0) {
                    newTargetCount[c - 'a']--;
                    usedSticker = true;
                }
            }

            // If the sticker could not be used, continue with the next sticker
            if (!usedSticker) {
                continue;
            }
            // Recursively call the helper function with the updated target count
            int nextResult = helper(stickers, newTargetCount, memo);
            if (nextResult != Integer.MAX_VALUE) {
                result = Math.min(result, nextResult + 1); // Add 1 for the sticker used
            }
        }
        // If no solution found, store and return the result
        result = (result == Integer.MAX_VALUE) ? -1 : result;
        memo.put(targetKey, result);
        return result;
    }
    public static void main(String[] args) {
        // Example input:
        String[] stickers = {"with", "example", "science"};
        String target = "thehat";
        // Expected output: 3
        // Explanation: The answer is 3 because we need 3 stickers: "with", "example", and "hat".

        int result = minStickers(stickers, target);
        System.out.println("Minimum number of stickers to spell word: " + result);
    }
}
