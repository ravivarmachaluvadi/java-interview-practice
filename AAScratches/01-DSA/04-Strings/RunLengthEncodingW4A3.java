class RunLengthEncodingW4A3 {
    // Driver code
    public static void main(String[] args) {
        String str = "wwwwaaadexxxxxxywww";
        System.out.println(encoding(str));
    }

    private static String encoding(String str) {
        int length = str.length();
        if (str == null || length == 0) return "";
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
