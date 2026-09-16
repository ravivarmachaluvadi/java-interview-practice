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