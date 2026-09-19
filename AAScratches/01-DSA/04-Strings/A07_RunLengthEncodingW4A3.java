/**
 * Run-length encoding of a string.
 * Input : wwwwaaadexxxxxxywww
 * Output: w4a3d1e1x6y1w3
 * Time O(n), space O(n) for the result.
 */
class RunLengthEncodingW4A3 {
    public static void main(String[] args) {
        String str = "wwwwaaadexxxxxxywww";
        System.out.println(encoding(str)); // w4a3d1e1x6y1w3
    }

    private static String encoding(String str) {
        if (str == null || str.isEmpty()) return ""; // null check must come before str.length()
        int length = str.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int count = 1;
            while (i < length - 1 && str.charAt(i) == str.charAt(i + 1)) {
                i++;
                count++;
            }
            sb.append(str.charAt(i)).append(count);
        }
        return sb.toString();
    }
}
