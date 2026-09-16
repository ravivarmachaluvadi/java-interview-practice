import java.util.*;

// https://leetcode.com/problems/add-bold-tag-in-string/description/
// 616. Add Bold Tag in String
class AddBoldTagInString {

    /**
     * Example 1:
     * <p>
     * Input: s = "abcxyz123", words = ["abc","123"]
     * <p>
     * Output: "<b>abc</b>xyz<b>123</b>"
     * <p>
     * Example 2:
     * Input: s = "aaabbb", words = ["aa","b"]
     * <p>
     * Output: "<b>aaabbb</b>"
     */
    public static String addBoldTag(String s, List<String> dict) {
        boolean[] bold = new boolean[s.length()];

        for (String word : dict) {
            // start inside of dict iterator
            int start = 0;
            // get start index of word from given index point
            // s.indexOf(word, start) you may forget start
            while ((start = s.indexOf(word, start)) != -1) {
                // filling index spread positions of word as true
                Arrays.fill(bold, start, start + word.length(), true);
                // Move to the next position after the current match
                start += 1;
            }
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (bold[i] && (i == 0 || !bold[i - 1])) {
                result.append("<b>");
            }
            result.append(s.charAt(i));
            if (bold[i] && (i == s.length() - 1 || !bold[i + 1])) {
                result.append("</b>");
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        // Example input:
        String s = "abcabcxyz123";
        List<String> dict = Arrays.asList("abc", "xyz", "123");

        String result = addBoldTag(s, dict);
        System.out.println("String with bold tags: " + result);
    }
}
