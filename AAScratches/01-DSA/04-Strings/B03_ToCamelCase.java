class ToCamelCase {
    public static void main(String[] args) {
        String sentence = "convert this sentence to camel case";

        // Remove extra spaces and split the sentence into words
        String[] words = sentence.trim().split("\\s+");

        // Use StringBuilder to build the CamelCase result
        // First word remains lowercase
        StringBuilder camelCase = new StringBuilder(words[0].toLowerCase());
        // Capitalize the first letter of each subsequent word and append
        for (int i = 1; i < words.length; i++) {
            String word = words[i];
            camelCase.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1).toLowerCase());
        }

        // Output the result
        System.out.println(camelCase.toString());
    }
}
