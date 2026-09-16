import java.util.ArrayList;
import java.util.List;
// https://leetcode.com/problems/text-justification/description/
// 68. Text Justification
class TextJustification {

    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        List<String> currentLine = new ArrayList<>();
        int currentLineLength = 0;

        for (String word : words) {
            // Check if adding this word exceeds the maxWidth
            if (currentLineLength + word.length() + currentLine.size() > maxWidth) {
                // Justify the current line
                result.add(justifyLine(currentLine, currentLineLength, maxWidth));
                currentLine.clear();
                currentLineLength = 0;
            }

            // Add the current word to the line
            currentLine.add(word);
            currentLineLength += word.length();
        }

        // Handle the last line (left-aligned)
        result.add(lastJustifyLine(currentLine, maxWidth));

        return result;
    }

    // Helper method to justify a line with multiple words
    private String justifyLine(List<String> line, int lineLength, int maxWidth) {
        if (line.size() == 1) {
            return line.get(0) + " ".repeat(maxWidth - lineLength);
        }

        int totalSpaces = maxWidth - lineLength;
        int spaceBetweenWords = totalSpaces / (line.size() - 1);
        int extraSpaces = totalSpaces % (line.size() - 1);

        StringBuilder justifiedLine = new StringBuilder();
        for (int i = 0; i < line.size(); i++) {
            justifiedLine.append(line.get(i));
            if (i < line.size() - 1) {
                justifiedLine.append(" ".repeat(spaceBetweenWords));
                if (i < extraSpaces) {
                    justifiedLine.append(" ");
                }
            }
        }
        return justifiedLine.toString();
    }

    // Helper method to justify the last line (left-aligned)
    private String lastJustifyLine(List<String> line, int maxWidth) {
        StringBuilder lastLine = new StringBuilder();
        for (int i = 0; i < line.size(); i++) {
            lastLine.append(line.get(i));
            if (i < line.size() - 1) {
                lastLine.append(" ");
            }
        }
        lastLine.append(" ".repeat(maxWidth - lastLine.length()));
        return lastLine.toString();
    }

    // Main method to test the functionality
    public static void main(String[] args) {
        TextJustification solution = new TextJustification();

        // Example 1:
        String[] words1 = {"This", "is", "an", "example", "of", "text", "justification."};
        List<String> result1 = solution.fullJustify(words1, 16);
        System.out.println("Justified Text 1:");
        for (String line : result1) {
            System.out.println("\"" + line + "\"");
        }

        // Example 2:
        String[] words2 = {"What", "must", "be", "acknowledgment", "shall", "be."};
        List<String> result2 = solution.fullJustify(words2, 16);
        System.out.println("\nJustified Text 2:");
        for (String line : result2) {
            System.out.println("\"" + line + "\"");
        }
    }
}
