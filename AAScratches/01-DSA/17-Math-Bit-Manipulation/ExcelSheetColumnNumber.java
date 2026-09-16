/**
 * Converts an Excel column title (e.g., "A", "AB") into its corresponding 1‑based integer index.
 *
 * The algorithm treats the title as a base‑26 number where 'A' = 1, 'B' = 2, … , 'Z' = 26.
 * It iterates over each character, multiplies the accumulated result by 26,
 * and adds the current letter’s value.
 *
 * Time Complexity: O(n) – one pass through the string of length n.
 * Space Complexity: O(1) – only a few integer variables are used regardless of input size.
 */
class ExcelSheetColumnNumber {

    public static int titleToNumber(String columnTitle) {
        int result = 0;
        for (char ch : columnTitle.toCharArray()) {
            int value = ch - 'A' + 1;
            result = result * 26 + value;
        }
        return result;
    }

    public static void main(String[] args) {
        // Test examples
        // 1, 2
        // 1
        // 12
        String columnTitle1 = "A";
        String columnTitle2 = "AB";// A*(26)+ (B-A)+1;
        String columnTitle3 = "ZY";

        System.out.println(columnTitle1 + ": " + titleToNumber(columnTitle1)); // Output: 1
        System.out.println(columnTitle2 + ": " + titleToNumber(columnTitle2)); // Output: 28
        System.out.println(columnTitle3 + ": " + titleToNumber(columnTitle3)); // Output: 701
    }
}
