// 796. Rotate String
// https://leetcode.com/problems/rotate-string/description/
class RotateString {
    public static boolean rotateString(String s, String goal) {
        if (s.length() != goal.length()) return false;
        return (s + s).contains(goal);
    }

    public static void main(String[] args) {
        boolean rotated = rotateString("abcde", "cdeab");

        System.out.println("is rotated : " + rotated);
    }
}
