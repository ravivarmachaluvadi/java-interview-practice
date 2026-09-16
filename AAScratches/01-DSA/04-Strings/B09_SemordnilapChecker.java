import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * A semordnilap is a word, phrase, or sentence that forms a
 * different valid word or phrase when spelled backward.
 * <p>
 * Examples of semordnilaps:
 * <p>
 * stressed ↔ desserts
 * <p>
 * deliver ↔ reviled
 */
class SemordnilapChecker {

    // Method to check for semordnilaps in the given list of words
    public static Set<String> findSemordnilaps(String[] words) {
        Set<String> semordnilaps = new HashSet<>();
        // Add all words to a set for quick lookup
        Set<String> wordSet = new HashSet<>(Arrays.asList(words));

        // Check each word to see if its reverse is in the set and is different
        for (String word : words) {
            String reversed = new StringBuilder(word).reverse().toString();
            if (!word.equals(reversed) && wordSet.contains(reversed)) {
                semordnilaps.add(word); // Add the original word
                semordnilaps.add(reversed); // Add the reversed word
            }
        }
        return semordnilaps;
    }

    public static void main(String[] args) {
        String[] words = {
                "desserts", "stressed", "diaper", "repaid",
                "drawer", "reward", "gateman", "nametag",
                "deliver", "reviled"
        };

        Set<String> semordnilaps = findSemordnilaps(words);
        System.out.println("Semordnilaps found: " + semordnilaps);
    }
}

