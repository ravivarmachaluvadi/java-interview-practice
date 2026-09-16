import java.util.ArrayList;
import java.util.List;

class ShortestDistanceBetweenTwoWords {
    /**
     * Finds the shortest distance between
     * midpoints of two words in a given document.
     */
    public static double shortestDistance(String document, String word1, String word2) {
        // Convert document to lowercase and split it into individual words
        String[] words = document.toLowerCase().split("[,. ]+");  // Split by space, commas, and periods
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();

        // Store the positions of word1 and word2
        List<Integer> positionsWord1 = new ArrayList<>();
        List<Integer> positionsWord2 = new ArrayList<>();
        int index = 0;
        for (String word : words) {
            int wordLength = word.length();
            if (word.equals(word1)) {
                // bad -> (0+3)/2= 1
                positionsWord1.add(index + wordLength / 2);
            } else if (word.equals(word2)) {
                positionsWord2.add(index + wordLength / 2);
            }
            // Increment index by the length of the word + 1 (for the space)
            index += wordLength + 1;
        }

        // Find the shortest distance between the midpoints of word1 and word2
        double shortest = document.length();
        for (int pos1 : positionsWord1) {
            for (int pos2 : positionsWord2) {
                shortest = Math.min(shortest, Math.abs(pos1 - pos2));
            }
        }

        return shortest;
    }

    public static boolean pass() {
        return shortestDistance(document, "and", "graphic") == 6d &&
                shortestDistance(document, "transfer", "it") == 14d &&
                shortestDistance(document, "Design", "filler") == 25d;
    }

    public static void main(String[] args) {
        if (pass()) {
            System.out.println("Pass");
        } else {
            System.out.println("Some Fail");
        }
    }

    private static final String document;

    static {
        StringBuffer sb = new StringBuffer();
        sb.append("In publishing and graphic design, lorem ipsum is a filler text commonly used to demonstrate the graphic elements");
        sb.append(" lorem ipsum text has been used in typesetting since the 1960s or earlier, when it was popularized by advertisements");
        sb.append(" for Letraset transfer sheets. It was introduced to the Information Age in the mid-1980s by Aldus Corporation, which");
        document = sb.toString();
    }
}
