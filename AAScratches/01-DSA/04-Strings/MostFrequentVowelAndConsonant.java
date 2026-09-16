
// https://leetcode.com/problems/find-most-frequent-vowel-and-consonant/description/
// 3541. Find Most Frequent Vowel and Consonant
class MostFrequentVowelAndConsonant {

    public static int maxFreqSum(String s) {
        int[] cnt = new int[26];
        for (char c : s.toCharArray()) {
            cnt[c - 'a']++;
        }

        int maxVowel = 0;
        int maxConsonant = 0;
        for (int i = 0; i < 26; i++) {
            char c = (char) ('a' + i);
            if (isVowel(c)) {
                maxVowel = Math.max(maxVowel, cnt[i]);
            } else {
                maxConsonant = Math.max(maxConsonant, cnt[i]);
            }
        }
        return maxVowel + maxConsonant;
    }

    private static boolean isVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u';
    }

    // Main to test examples
    public static void main(String[] args) {
        String s1 = "successes";
        System.out.println("Input: \"" + s1 + "\" → Output: " + maxFreqSum(s1));
        // Expected: 6

        String s2 = "aeiaeia";
        System.out.println("Input: \"" + s2 + "\" → Output: " + maxFreqSum(s2));
        // Expected: 3

        String s3 = "bbbb";
        System.out.println("Input: \"" + s3 + "\" → Output: " + maxFreqSum(s3));
        // Only consonants, expected: 0 (vowel) + 4 (consonant) = 4

        String s4 = "aeiou";
        System.out.println("Input: \"" + s4 + "\" → Output: " + maxFreqSum(s4));
        // Only vowels, expected: 5 (vowel) + 0 (consonant) = 5
    }
}
