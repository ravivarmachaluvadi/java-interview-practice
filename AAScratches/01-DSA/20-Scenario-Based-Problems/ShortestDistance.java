
class ShortestDistance {
    public static int shortestDistance(String paragraph, String wordOne, String wordTwo) {
        // Split paragraph into words, handling punctuation properly
        String[] words = paragraph.split("\\W+");

        int wordOneIndex = -1;
        int wordTwoIndex = -1;
        int shortestDistance = Integer.MAX_VALUE;

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (word.equalsIgnoreCase(wordOne)) {
                wordOneIndex = i;
            } else if (word.equalsIgnoreCase(wordTwo)) {
                wordTwoIndex = i;
            }

            if (wordOneIndex != -1 && wordTwoIndex != -1) {
                // Calculate the distance between the two words
                int currentDistance = Math.abs(wordOneIndex - wordTwoIndex);
                shortestDistance = Math.min(shortestDistance, currentDistance);
            }
        }

        // If either word is not found, return -1
        if (wordOneIndex == -1 || wordTwoIndex == -1) {
            return -1;
        }

        return shortestDistance;
    }

    /**
     * Returns true if the tests pass. Otherwise, false.
     */
    public static boolean doTestsPass() {
        // todo: implement more tests if you'd like
        return shortestDistance(_paragraph, "and", "graphic") == 6 &&
                shortestDistance(_paragraph, "transfer", "it") == 14 &&
                shortestDistance(_paragraph, "Design", "filler") == 25;
    }

    /**
     * Execution entry point.
     */
    public static void main(String[] args) {
        // Run the tests
        if (doTestsPass()) {
            System.out.println("All tests pass");
        } else {
            System.out.println("There are test failures");
        }
    }

    private static final String _paragraph;

    static {
        StringBuffer sb = new StringBuffer();
        sb.append("In publishing and graphic design, lorem ipsum is a filler text commonly used to demonstrate the graphic elements");
        sb.append(" lorem ipsum text has been used in typesetting since the 1960s or earlier, when it was popularized by advertisements");
        sb.append(" for Letraset transfer sheets. It was introduced to the Information Age in the mid-1980s by Aldus Corporation, which");

        _paragraph = sb.toString();
    }
}
