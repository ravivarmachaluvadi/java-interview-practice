import java.util.*;
class SortStringDescending {

    public static String sortStringInDescendingOrder(String input) {
        // Convert the string to a character array
        Character[] chars = new Character[input.length()];

        // Fill the character array
        for (int i = 0; i < input.length(); i++)
            chars[i] = input.charAt(i);

        // Sort the array in descending order
        Arrays.sort(chars, Collections.reverseOrder());

        // Convert the sorted array back to a string
        StringBuilder sortedString = new StringBuilder(chars.length);
        for (Character ch : chars)
            sortedString.append(ch);

        return sortedString.toString();
    }

    public static void main(String[] args) {
        String input = "mupursingh";
        String result = sortStringInDescendingOrder(input);
        System.out.println("Sorted string in descending order: " + result);
        // uusrpnmihg
    }
}
