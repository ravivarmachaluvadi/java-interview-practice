import java.util.*;

class PanagramDetectorMain {

    private static class PanagramDetector {
        private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

        public String findMissingLetters(String input) {
            Set<Character> missingCharacters = new HashSet<>();
            for (char ch : ALPHABET.toCharArray()) {
                missingCharacters.add(ch);
            }

            for (char ch : input.toLowerCase().toCharArray()) {
                missingCharacters.remove(ch);
            }

            StringBuilder sb = new StringBuilder();
            for (char ch : missingCharacters) {
                sb.append(ch);
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        PanagramDetector pd = new PanagramDetector();
        boolean success = true;

        success = success && "".equals(pd.findMissingLetters("The quick brown fox jumps over the lazy dog"));
        success = success && "abcdefghijklmnopqrstuvwxyz".equals(pd.findMissingLetters(""));

        if (success) {
            System.out.println("Passed");
        } else {
            System.out.println("Failed");
        }
    }
}