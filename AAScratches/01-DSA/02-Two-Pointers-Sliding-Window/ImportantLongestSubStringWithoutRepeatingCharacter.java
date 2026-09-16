import java.util.*;
// two pointer approach
// Hash Table, String, Sliding Window
// https://leetcode.com/problems/longest-substring-without-repeating-characters
class ImportantLongestSubStringWithoutRepeatingCharacter {
    public static void main(String[] args) {
        String string = "abcddcba";
        Map<Character, Integer> map = new HashMap<>();
        int maxLength = 0;

        for (int left = 0, right = 0; right < string.length(); right++) {
            char c = string.charAt(right);
            if (map.containsKey(c))
                left = Math.max(map.get(c) + 1, left);

            if (maxLength < right - left + 1) maxLength = right - left + 1;
            // Remember update or insert latest
            // index for current character in map
            map.put(c, right);
        }
        System.out.println(maxLength);
    }
}
