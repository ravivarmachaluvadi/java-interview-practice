import java.util.*;

// https://leetcode.com/problems/longest-string-chain/description/
// 1048. Longest String Chain
class LongestStringChain {
    public static int longestStrChain(String[] words) {
        // Sort words by length
        Arrays.sort(words, Comparator.comparingInt(String::length));

        Map<String, Integer> dp = new HashMap<>();
        int maxChainLength = 0;

        for (String w : words) {
            int best = 1;  // chain length ending at w
            // Try removing one char at every position
            for (int i = 0; i < w.length(); i++) {
                String pred = w.substring(0, i) + w.substring(i + 1);
                Integer prevChain = dp.get(pred);
                if (prevChain != null) {
                    best = Math.max(best, prevChain + 1);
                }
            }
            dp.put(w, best);
            maxChainLength = Math.max(maxChainLength, best);
        }

        return maxChainLength;
    }

    public static void main(String[] args) {
        // Example input
        String[] words = {"a", "b", "ba", "bca", "bda", "bdca"};
        int result = longestStrChain(words);
        System.out.println("Longest string chain length = " + result);
        // Expected output: 4
    }
}
