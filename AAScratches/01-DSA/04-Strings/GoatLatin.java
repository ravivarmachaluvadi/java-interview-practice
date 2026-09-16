public class GoatLatin {

    public static String toGoatLatin ( String sentence ) {
        String[] words = sentence.split ( " " );
        StringBuilder result = new StringBuilder ( );

        // Define vowels for easy checking
        String vowels = "aeiouAEIOU";

        for ( int i = 0 ; i < words.length ; i++ ) {
            String word = words[ i ];
            StringBuilder goatLatinWord = new StringBuilder ( );

            // Check if the first letter is a vowel
            if ( vowels.contains ( String.valueOf ( word.charAt ( 0 ) ) ) ) {
                goatLatinWord.append ( word );
            } else {
                // Move the first character to the end and append the word
                goatLatinWord.append ( word.substring ( 1 ) ).append ( word.charAt ( 0 ) );
            }

            // Append "ma" to the word
            goatLatinWord.append ( "ma" );

            // Append the number of "a"s based on the word's position
            for ( int j = 0 ; j < i + 1 ; j++ ) {
                goatLatinWord.append ( "a" );
            }

            // Append the word to the result
            result.append ( goatLatinWord.toString ( ) ).append ( " " );
        }

        // Remove the trailing space and return the result
        return result.toString ( ).trim ( );
    }

    public static void main ( String[] args ) {
        String sentence = "I speak Goat Latin";
        String goatLatin = toGoatLatin ( sentence );

        System.out.println ( "Goat Latin: " + goatLatin );
    }
}
