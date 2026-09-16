import java.util.Arrays;
import java.util.HashSet;

class UniqueTuples {

    public static HashSet<String> uniqueTuples(String inputString, int length) {
        if (inputString == null) return null;
        int inputStringLen = inputString.length();
        HashSet<String> hs = new HashSet<>();
        for (int i = 0; i < (inputStringLen - length + 1); i++)
            hs.add(inputString.substring(i, (i + length)));
        return hs;
    }

    public static void main(String[] args) {

        String input = "aab";
        String input1 = "abbccde";

        HashSet<String> result = uniqueTuples(input, 2);
        HashSet<String> result1 = uniqueTuples(input1, 2);

        if ((result.contains("aa") && result.contains("ab"))
                && (result1.containsAll(Arrays.asList("ab", "bb", "bc", "cc", "cd", "de"))
                && result1.size() == 6)) {
            System.out.println("Test passed.");
        } else {
            System.out.println("Test failed.");
        }
    }
}
