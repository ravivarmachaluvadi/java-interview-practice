/**
 *
 * Given two strings ransomNote and magazine, return true if
 * <p>
 * ransomNote can be constructed by using the letters from magazine and false otherwise.
 * <p>
 * Each letter in magazine can only be used once in ransomNote.
 * <p>
 * Input: ransomNote = "aa", magazine = "aab"
 * <p>
 * Output: true
 */
class RansomNote {

    public static boolean canConstruct(String ransomNote, String magazine) {
        int[] count = new int[26];
        for (char c : magazine.toCharArray()) {
            count[c - 'a']++;
        }
        for (char c : ransomNote.toCharArray()) {
            if (--count[c - 'a'] < 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        // Example 1:
        String ransomNote1 = "a";
        String magazine1 = "b";
        System.out.println("Example 1 -> ransomNote: " + ransomNote1 + ", magazine: " + magazine1
                + " => " + canConstruct(ransomNote1, magazine1));
        // Expected: false

        // Example 2:
        String ransomNote2 = "aa";
        String magazine2 = "ab";
        System.out.println("Example 2 -> ransomNote: " + ransomNote2 + ", magazine: " + magazine2
                + " => " + canConstruct(ransomNote2, magazine2));
        // Expected: false

        // Example 3:
        String ransomNote3 = "aa";
        String magazine3 = "aab";
        System.out.println("Example 3 -> ransomNote: " + ransomNote3 + ", magazine: " + magazine3
                + " => " + canConstruct(ransomNote3, magazine3));
        // Expected: true
    }
}
