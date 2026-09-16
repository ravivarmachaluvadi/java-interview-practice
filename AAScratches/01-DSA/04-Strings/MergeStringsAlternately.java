class MergeStringsAlternately {

    public static String mergeAlternately(String word1, String word2) {

        StringBuilder result = new StringBuilder();
        int i = 0, j = 0;

        while (i < word1.length() && j < word2.length()) {
            result.append(word1.charAt(i++));
            result.append(word2.charAt(j++));
        }

        while (i < word1.length()) {
            result.append(word1.charAt(i++));
        }

        while (j < word2.length()) {
            result.append(word2.charAt(j++));
        }

        return result.toString();
    }

    public static void main(String[] args) {
        // Test example 1
        String word1 = "abc";
        String word2 = "pqr";
        System.out.println("Merged String (Example 1): " + mergeAlternately(word1, word2)); // Output: "apbqcr"

        // Test example 2
        word1 = "ab";
        word2 = "pqrs";
        System.out.println("Merged String (Example 2): " + mergeAlternately(word1, word2)); // Output: "apbqrs"

        // Test example 3
        word1 = "abcd";
        word2 = "pq";
        System.out.println("Merged String (Example 3): " + mergeAlternately(word1, word2)); // Output: "apbqcd"
    }
}
