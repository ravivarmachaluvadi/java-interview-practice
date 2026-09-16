class IntegerToRoman {
    /**
     * Symbol	Value
     * I	      1
     * V	      5
     * X	      10
     * L	      50
     * C	      100
     * D	      500
     * M	      1000
     */
    public static String intToRoman(int num) {
        // Define Roman numeral symbols and their corresponding values
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder roman = new StringBuilder();

        // Iterate over the values and construct the Roman numeral
        // from large values to small values
        for (int i = 0; i < values.length && num > 0; i++) {
            while (num >= values[i]) {
                roman.append(symbols[i]);
                num -= values[i];
            }
        }

        return roman.toString();
    }

    public static void main(String[] args) {
        // Example test cases
        // 3999->MMMCMXCIX
        int[] testCases = {3, 58, 1994, 9, 44, 3999};

        for (int testCase : testCases) {
            System.out.println("Integer: " + testCase + " -> Roman: " + intToRoman(testCase));
        }
    }
}
