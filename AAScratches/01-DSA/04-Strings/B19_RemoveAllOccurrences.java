// https://leetcode.com/problems/remove-all-occurrences-of-a-substring/description/
// 1910. Remove All Occurrences of a Substring
class RemoveAllOccurrences {
    public static String removeOccurrences(String s, String part) {
        StringBuilder sb = new StringBuilder(s);
        int index = sb.indexOf(part); //find 1st occurrence
        while (index != -1) {
            //StringBuilder sb = new StringBuilder("RaviVarma");
            //  sb.delete(2, 5);
            //  System.out.println(sb);// Raarma
            sb.delete(index, index + part.length());
            index = sb.indexOf(part);
        }
        return sb.toString();
    }

    // Example main method to test
    public static void main(String[] args) {
        String s1 = "daabcbaabcbc";
        String part1 = "abc";
        System.out.println("Input: s = \"" + s1 + "\", part = \"" + part1 + "\"");
        System.out.println("Output: \"" + removeOccurrences(s1, part1) + "\"");
        // Expect: "dab"

        String s2 = "axxxxyyyyb";
        String part2 = "xy";
        System.out.println("Input: s = \"" + s2 + "\", part = \"" + part2 + "\"");
        System.out.println("Output: \"" + removeOccurrences(s2, part2) + "\"");
        // Expect: "ab"
    }
}
