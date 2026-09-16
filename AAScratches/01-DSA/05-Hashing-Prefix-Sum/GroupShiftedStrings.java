import java.util.*;

class GroupShiftedStrings {

    // Function to group shifted strings
    public static List<List<String>> groupStrings(String[] strings) {
        Map<String, List<String>> map = new HashMap<>();

        // Helper function to get the pattern for each string
        for (String str : strings) {
            StringBuilder pattern = new StringBuilder();

            // Calculate the difference between consecutive characters
            for (int i = 1; i < str.length(); i++) {
                int diff = str.charAt(i) - str.charAt(i - 1);
                // Normalize the difference to be within the range of 'a' to 'z'
                if (diff < 0) {
                    diff += 26;
                }
                pattern.append(diff).append(",");
            }

            // Add the string to the group identified by its pattern
            map.computeIfAbsent(pattern.toString(), k -> new ArrayList<>()).add(str);
        }

        return new ArrayList<>(map.values());
    }
    public static void main(String[] args) {
        // Example input:
        String[] strings = {"abc", "bcd", "acef", "aef", "xyz", "az", "ba", "a", "z"};
        // Expected output: A list of groups of strings that are shifted versions of each other.

        List<List<String>> result = groupStrings(strings);

        // Printing the result
        for (List<String> group : result) {
            System.out.println(group);
        }
    }
}
