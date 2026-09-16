/**
 * Converts an integer into its English word representation using the Indian numbering system.
 *
 * The method handles numbers up to 99,99,99,999 (hundreds of crores) by breaking them into
 * crore, lakh, thousand, hundred and the last two digits. Each segment is converted with
 * helper arrays for values below twenty and tens multiples.
 *
 * Approach:
 *   1. Decompose the number into crore, lakh, thousand, hundred and remainder parts.
 *   2. Convert each part to words using lookup tables.
 *   3. Concatenate segments, inserting "and" when appropriate, then trim and collapse spaces.
 *
 * Time Complexity: O(1) – constant work for a fixed-size integer.
 * Space Complexity: O(1) – only a few string buffers are used regardless of input size.
 */
class NumberToWordsConverter {

    private static final String[] lessThan20 = {
            "", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
            "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen",
            "seventeen", "eighteen", "nineteen"};

    private static final String[] tens = {
            "", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"};

    private static String getInWords(int i, String s) {
        if (i == 0) return "";
        if (i < 20) return lessThan20[i] + " " + s + " ";
        StringBuilder t = new StringBuilder();
        t.append(tens[i / 10]).append(" ");
        if (i % 10 > 0) t.append(lessThan20[i % 10]).append(" ");
        return t.append(s).append(" ").toString();
    }

    public static String convertToWords(int number) {
        if (number == 0) return "zero";

        String words =
                getInWords(number / 10000000, "crore") +
                        getInWords((number / 100000) % 100, "lakh") +
                        getInWords((number / 1000) % 100, "thousand") +
                        getInWords((number / 100) % 10, "hundred");

        if (number > 99 && number % 100 > 0)
            words += "and ";

        words += getInWords(number % 100, "");

        return words.trim().replaceAll("\\s+", " ");
    }

    public static void main(String[] args) {
        System.out.println(convertToWords(123456789));
        System.out.println(convertToWords(100000000));
        System.out.println(convertToWords(1234));
        System.out.println(convertToWords(0));
    }
}

// 1234/1000 -> 1
// 1234%1000 -> 234