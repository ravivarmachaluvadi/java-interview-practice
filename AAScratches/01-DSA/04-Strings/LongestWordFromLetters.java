import java.util.*;

class LongestWordFromLetters {
    // Function to find the longest word(s) that can be formed from the given letters
    public static Set<String> longestWord(String letters, Set<String> dictionary) {
        // Track the longest words
        Set<String> longestWords = new HashSet<>();
        int maxLength = 0;

        // Create a frequency map for the available letters
        Map<Character, Integer> letterCount = getLetterCount(letters);

        // Iterate through the dictionary
        for (String word : dictionary) {
            if (canFormWord(word, letterCount)) {
                // If the word can be formed, check its length
                if (word.length() > maxLength) {
                    maxLength = word.length();
                    longestWords.clear(); // Clear previous longest words
                    longestWords.add(word);
                } else if (word.length() == maxLength) {
                    longestWords.add(word); // Add the word if it's equal to the longest found
                }
            }
        }

        return longestWords;
    }

    // Function to create a frequency map of characters
    private static Map<Character, Integer> getLetterCount(String letters) {
        Map<Character, Integer> countMap = new HashMap<>();
        for (char c : letters.toCharArray()) {
            countMap.put(c, countMap.getOrDefault(c, 0) + 1);
        }
        return countMap;
    }

    // Function to check if the word can be formed from the available letters
    private static boolean canFormWord(String word, Map<Character, Integer> letterCount) {
        Map<Character, Integer> wordCount = new HashMap<>();
        for (char c : word.toCharArray()) {
            wordCount.put(c, wordCount.getOrDefault(c, 0) + 1);
            // If the word requires more of a letter than available, return false
            if (wordCount.get(c) > letterCount.getOrDefault(c, 0)) {
                return false;
            }
        }
        return true;
    }
    public static void main(String[] args) {
        Set<String> dictionary = new HashSet<>(Arrays.asList("to", "toe", "toet", "toes"));
        String letters = "oet";

        Set<String> result = longestWord(letters, dictionary);
        System.out.println(result); // Expected Output: {"toe"}
    }
}
