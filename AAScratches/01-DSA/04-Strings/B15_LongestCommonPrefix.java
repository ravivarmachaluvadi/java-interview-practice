// https://leetcode.com/problems/longest-common-prefix/
class LongestCommonPrefix {
    public static String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) return "";
        String prefix = strs[0];

        for (int i = 1; i < strs.length; i++) {
            // if prefix not found consider keep reducing and update prefix
            // keep reducing length of prefix and keep assigning
            // != 0 means prefix not found
            while (strs[i].indexOf(prefix) != 0) {
                // System.out.println("Ravi".substring(0,"Ravi".length()-1));// Rav
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) return "";
            }
        }
        return prefix;
    }

    public static void main(String[] args) {
        System.out.println(longestCommonPrefix(new String[]{"flower", "flow", "flight"}));
    }
}
