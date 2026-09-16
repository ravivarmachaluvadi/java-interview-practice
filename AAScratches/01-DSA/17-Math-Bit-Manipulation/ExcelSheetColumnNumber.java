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
