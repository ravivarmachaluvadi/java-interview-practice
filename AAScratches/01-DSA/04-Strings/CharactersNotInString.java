class CharactersNotInString {
    public static void main(String[] args) {
        String str = "the sun rises from the east";
        System.out.println(getCharactersNotInString(str));
    }

    private static String getCharactersNotInString(String str) {
        if (str == null || str.isEmpty())
            return "abcdefghijkl";

        str = str.toLowerCase();
        int[] charsCount = new int[26];
        StringBuilder ans = new StringBuilder();

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != ' ')
                charsCount[str.charAt(i) - 'a']++;
        }

        for (int j = 0; j < charsCount.length; j++) {
            if (charsCount[j] == 0)
                ans.append((char) (j + 'a'));
        }
        return ans.toString();
    }
}
