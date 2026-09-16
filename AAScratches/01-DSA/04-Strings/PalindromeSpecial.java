/**
 * Determines whether a given string is a palindrome ignoring case and
 * non‑alphabetic characters (e.g., punctuation, spaces). The example checks
 * the classic phrase "Was it a car or a cat I saw?".
 *
 * Approach: Two pointers scan from both ends. Non‑letters are skipped,
 * remaining letters are compared in lower case until the pointers cross.
 *
 * Time Complexity: O(n), where n is the length of the input string.
 * Space Complexity: O(1) – only a few integer variables are used.
 */
public class PalindromeSpecial {
    public static void main(String[] args) {
        boolean isPalindrome = true;
        String str = "Was it a car or a cat I saw?";
        str = str.toLowerCase();
        int n = str.length();
        int left = 0, right = n - 1;
        while (left <= right) {
            while (left < right && (str.charAt(left) < 'a' || str.charAt(left) > 'z')) {
                left++;
            }
            while (left < right && (str.charAt(right) < 'a' || str.charAt(right) > 'z')) {
                right--;
            }
            System.out.println(left + " " + right);
            if (str.charAt(left) != str.charAt(right)) {
                isPalindrome = false;
                break;
            }
            left++;
            right--;

        }
        System.out.println("is palindrome : " + isPalindrome);
    }
}
