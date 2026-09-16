class MinimumMovesToPalindrome {

    // Method to find the minimum number of moves to make the string a palindrome
    public static int minMovesToMakePalindrome(String s) {
        char[] chars = s.toCharArray();
        int left = 0, right = chars.length - 1;
        int moves = 0;

        while (left < right) {
            if (chars[left] == chars[right]) {
                left++;
                right--;
            } else {
                int tempLeft = left, tempRight = right;

                // Try to move the left character towards the right
                while (tempLeft < tempRight && chars[tempLeft] != chars[right]) {
                    tempLeft++;
                }

                // Try to move the right character towards the left
                while (tempLeft < tempRight && chars[left] != chars[tempRight]) {
                    tempRight--;
                }

                // Choose the direction with fewer moves
                if (tempLeft - left <= right - tempRight) {
                    // Move left character towards the right
                    for (int i = tempLeft; i > left; i--) {
                        char temp = chars[i];
                        chars[i] = chars[i - 1];
                        chars[i - 1] = temp;
                        moves++;
                    }
                    left++;
                    right--;
                } else {
                    // Move right character towards the left
                    for (int i = tempRight; i < right; i++) {
                        char temp = chars[i];
                        chars[i] = chars[i + 1];
                        chars[i + 1] = temp;
                        moves++;
                    }
                    left++;
                    right--;
                }
            }
        }
        return moves;
    }

    // Main method to test the solution
    public static void main(String[] args) {
        String s1 = "aabb";
        System.out.println(minMovesToMakePalindrome(s1));//output: 2

        String s2 = "aab";
        System.out.println(minMovesToMakePalindrome(s2));//output: 1

        String s3 = "abcba";
        System.out.println(minMovesToMakePalindrome(s3));//output: 0
    }
}
