// 1754. Largest Merge Of Two Strings
// https://leetcode.com/problems/largest-merge-of-two-strings/description/
class LargestMergeOfTwoStrings {
    /**
     * Input: word1 = "cabaa", word2 = "bcaaa"
     * Output: "cbcabaaaaa"
     * Explanation: One way to get the lexicographically largest merge
     */
    // merge of two arrays in descending order
    public String largestMerge(String word1, String word2) {
        int i = 0, j = 0;
        int n = word1.length(), m = word2.length();
        StringBuilder sb = new StringBuilder();

        while (i < n && j < m) {
            // Compare the remaining suffixes lexicographically
            // If word1[i:] is greater, take from word1; else take from word2
            if (word1.substring(i).compareTo(word2.substring(j)) > 0) {
                sb.append(word1.charAt(i));
                i++;
            } else {
                sb.append(word2.charAt(j));
                j++;
            }
        }

        // append leftovers
        if (i < n) {
            sb.append(word1.substring(i));
        }
        if (j < m) {
            sb.append(word2.substring(j));
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        LargestMergeOfTwoStrings solver = new LargestMergeOfTwoStrings();
        String word1 = "cabaa";
        String word2 = "bcaaa";
        System.out.println("Input:");
        System.out.println("word1 = \"" + word1 + "\"");
        System.out.println("word2 = \"" + word2 + "\"");
        String result = solver.largestMerge(word1, word2);
        System.out.println("\nOutput:");
        System.out.println(result);
    }
}