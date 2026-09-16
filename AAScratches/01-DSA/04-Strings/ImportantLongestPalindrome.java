class ImportantLongestPalindrome {
    public static String longestPalindrome(String s) {
        StringBuilder sPrime = new StringBuilder("#");
        for (char c : s.toCharArray())
            sPrime.append(c).append("#");

        int n = sPrime.length();
        int[] palindromeLength = new int[n];
        int center = 0;
        int radius = 0;

        for (int i = 0; i < n; i++) {
            /**
             * center: The current center of the palindrome you are examining.
             *
             * i: The index on the right side of the center for which you are calculating the mirror position.
             *
             * mirror: This is the index on the left side of the center, symmetric to i.
             */
            int mirror = 2 * center - i;
            if (i < radius) {
                palindromeLength[i] = Math.min(radius - i, palindromeLength[mirror]);
            }

            while (
                    i + 1 + palindromeLength[i] < n &&
                            i - 1 - palindromeLength[i] >= 0 &&
                            sPrime.charAt(i + 1 + palindromeLength[i]) ==
                                    sPrime.charAt(i - 1 - palindromeLength[i])
            ) {
                palindromeLength[i]++;
            }

            if (i + palindromeLength[i] > radius) {
                center = i;
                radius = i + palindromeLength[i];
            }
        }

        int maxLength = 0;
        int centerIndex = 0;
        for (int i = 0; i < n; i++) {
            if (palindromeLength[i] > maxLength) {
                maxLength = palindromeLength[i];
                centerIndex = i;
            }
        }

        int startIndex = (centerIndex - maxLength) / 2;
        String longestPalindrome = s.substring(
                startIndex,
                startIndex + maxLength
        );

        return longestPalindrome;
    }

    public static void main(String[] args) {
        System.out.println(longestPalindrome("babad"));
    }
}
