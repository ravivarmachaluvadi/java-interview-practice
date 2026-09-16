class SingleLoopSubstringCheckKMP {

    public static boolean isSubstring(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();

        if (m == 0) return true;
        if (n < m) return false;

        int i = 0; // index for text
        int j = 0; // index for pattern

        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    return true; // found match
                }
            } else {
                // reset pattern pointer and shift window
                j = 0;
                // Just move text window by 1 from the last starting point
                i = i - j + 1;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String text = "abxabcabcaby";
        String pattern = "abcaby";

        if (isSubstring(text, pattern)) {
            System.out.println("Yes, substring exists.");
        } else {
            System.out.println("No, substring does not exist.");
        }
    }
}
