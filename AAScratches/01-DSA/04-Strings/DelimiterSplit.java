import java.util.Arrays;

class DelimiterSplit {
    public static void main(String[] args) {
        String input = "apple,banana,orange,grape";
        char delimiter = ',';
        // Split the string based on the delimiter
        String[] result = input.split(String.valueOf(delimiter));
        String[] result1 = input.split(",");
        // Print the list of resulting substrings
        System.out.println(Arrays.toString(result));
        System.out.println(Arrays.toString(result1));
    }
}
