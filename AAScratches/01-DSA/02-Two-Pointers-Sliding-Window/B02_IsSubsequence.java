/**
 * Example 1:
 * <p>
 * Input: s = "abc", t = "ahbgdc"
 * <p>
 * Output: true
 * <p>
 * Example 2:
 * <p>
 * Input: s = "axc", t = "ahbgdc"
 * <p>
 * Output: false
 */
class IsSubsequence {
    public static boolean isSubsequence(String s, String t) {
        int start = 0;
        for (char ch : s.toCharArray()) {
// indexOf(ch,fromIndex) -> gives first index of given character from given index
            int index = t.indexOf(ch, start);
            if (index == -1) {
                return false;
            } else {
                start = index + 1;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        System.out.println(isSubsequence("abc", "ahbgdc"));
        System.out.println(isSubsequence("axc", "xahbgdc"));
    }
}
