/**
 * Problem: Given a string, find all lowercase alphabetic characters that do not appear in the string.
 *
 * Approach: Convert the string to lowercase and count occurrences of each letter using an array of size 26.
 * Then iterate over the counts; any zero count indicates the corresponding character is missing,
 * which is appended to the result string.
 *
 * Time Complexity: O(n + 26) ≈ O(n), where n is the length of the input string.
 * Space Complexity: O(1) additional space (fixed-size array and output builder).
 */
class CharactersNotInString {
    public static void main(String[] args) {
        String str = "the sun rises from the east";
        System.out.println(getCharactersNotInString(str));
    }

    private static String getCharactersNotInString(String str) {
        if (str == null || str.isEmpty())
            return "abcdefghijkl";

        str = str.toLowerCase();
        int[] charsCount = new int[26];
        StringBuilder ans = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != ' ')
                charsCount[str.charAt(i) - 'a']++;
        }

        for (int j = 0; j < charsCount.length; j++) {
            if (charsCount[j] == 0)
                ans.append((char) (j + 'a'));
        }
        return ans.toString();
    }
}
