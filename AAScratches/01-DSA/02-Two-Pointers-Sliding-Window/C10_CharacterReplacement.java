/**
 * Input: s = "AABABBA", k = 1
 * <p>
 * Output: 4
 * <p>
 * Explanation: Replace the one 'A' in the middle with 'B' and form "AABBBBA".
 * <p>
 * The substring "BBBB" has the longest repeating letters, which is 4.
 * <p>
 * There may exists other ways to achieve this answer too.
 */
// https://leetcode.com/problems/longest-repeating-character-replacement/
// 424. Longest Repeating Character Replacement
class CharacterReplacement {
    // two pinter solution
    public static int characterReplacement(String s, int k) {
        int n = s.length();
        int[] frequencyMap = new int[25];
        int left = 0, right = 0, maxfreq = 0, maxLength = 0;

        while (right < n) {
            char currentChar = s.charAt(right);
            if (maxfreq < ++frequencyMap[currentChar - 'A'])
                maxfreq = frequencyMap[currentChar - 'A'];

            int currentWindowLength = right - left + 1;
            // remember currentWindowLength - maxfreq <= k
            // if currChar is maxFrequency char from currWindowLen-maxFreq <=k
            // means we can replace compliment value with max freq char as count <=k
            // so by doing this we may get maxLength
            if (currentWindowLength - maxfreq <= k) {
                maxLength = Math.max(maxLength, currentWindowLength);
            } else {
                // If the window is not valid, shrink the window by moving left
                frequencyMap[s.charAt(left++) - 'A']--;
            }
            right++;
        }
        return maxLength;
    }

    public static void main(String[] args) {
        System.out.println(characterReplacement("AABABBA", 1));
    }
}
