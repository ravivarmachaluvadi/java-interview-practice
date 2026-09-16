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
