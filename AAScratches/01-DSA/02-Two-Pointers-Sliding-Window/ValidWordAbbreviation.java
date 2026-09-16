// LeetCode Problem: https://leetcode.com/problems/valid-word-abbreviation/

class ValidWordAbbreviation {

    // two pointer solution
    public static boolean validWordAbbreviation(String word, String abbr) {
        int wordIndex = 0, abbrIndex = 0;

        while (wordIndex < word.length() && abbrIndex < abbr.length()) {
            if (Character.isDigit(abbr.charAt(abbrIndex))) {
                if (abbr.charAt(abbrIndex) == '0')
                    return false; // leading zeros are not allowed

                int num = 0;
                // remember use abbr.charAt(abbrIndex) as abbrIndex plus plusing
                while (abbrIndex < abbr.length() && Character.isDigit(abbr.charAt(abbrIndex))) {
                    // remember use abbr.charAt(abbrIndex) as abbrIndex plus plusing
                    num = num * 10 + (abbr.charAt(abbrIndex) - '0');
                    abbrIndex++;
                }
                wordIndex += num;
            } else {
                if (word.charAt(wordIndex) != abbr.charAt(abbrIndex)) {
                    return false;
                }
                wordIndex++;
                abbrIndex++;
            }
        }

        return wordIndex == word.length() && abbrIndex == abbr.length();
    }

    public static void main(String[] args) {
        String word = "internationalization";
        String abbr = "i12iz4n";

        boolean result = validWordAbbreviation(word, abbr);
        System.out.println("Is valid abbreviation: " + result);
        // Expected output: true
    }
}
