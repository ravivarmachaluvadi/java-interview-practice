class ReverseWords {

    public static String reverseWords(String str) {
        String[] words = str.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            String reversedWord = new StringBuilder(word).reverse().toString();
            result.append(reversedWord).append(" ");
        }
        return result.toString().trim();
    }

    public static void main(String[] args) {
        String str = "Hello World from Java"; // olleH dlroW morf avaJ

        String reversedWords = reverseWords(str);
        System.out.println(reversedWords);
    }
}
