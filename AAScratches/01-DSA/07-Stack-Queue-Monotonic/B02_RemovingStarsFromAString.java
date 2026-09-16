/**
 * Input: s = "leet**cod*e"
 * <p>
 * Remove the closest non-star character to its left
 * <p>
 * Output: "lecoe"
 */
class RemovingStarsFromAString {
    public static String removeStars(String s) {
        char[] chars = s.toCharArray();
        int left = 0;
        for (int right = 0; right < chars.length; right++) {
            if (chars[right] == '*') {
// we are decreasing left to override so means works like deletion
                left--;
            } else {
                chars[left] = chars[right];
                left++;
            }
        }
        return new String(chars, 0, left);
    }

    public static void main(String[] args) {
        System.out.println(removeStars("leet**cod*e"));// lecoe
    }
}
