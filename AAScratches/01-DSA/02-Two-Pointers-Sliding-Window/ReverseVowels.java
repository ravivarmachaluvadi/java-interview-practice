class ReverseVowels {
    public static void main(String[] args) {
        String string = "hello world";  // Example input string
        String result = reverseVowels(string);
        System.out.println("Reversed vowels: " + result);
    }

    public static String reverseVowels(String string) {
        if (string == null || string.isEmpty())
            return string;

        int left = 0;
        int right = string.length() - 1;
        char[] chars = string.toCharArray();
        String vowels = "aeiouAEIOU";

        while (left < right) {
            // Move `left` forward until we find a vowel
            // remember left < right && vowels.indexOf(chars[left]) == -1
            while (left < right && vowels.indexOf(chars[left]) == -1) {
                left++;
            }

            // Move `right` backward until we find a vowel
            while (left < right && vowels.indexOf(chars[right]) == -1) {
                right--;
            }

            // Swap the vowels
            if (left < right) {
                char temp = chars[left];
                chars[left] = chars[right];
                chars[right] = temp;

                left++;
                right--;
            }
        }
        return new String(chars);
    }
}
