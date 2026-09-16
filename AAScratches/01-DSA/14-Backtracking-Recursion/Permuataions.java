import java.util.ArrayList;
import java.util.List;

class Permuataions {
    public static void main(String[] args) {
        List<String> ansList = new ArrayList<>();
        char[] charArray = "abc".toCharArray();
        generatePermutations(charArray, ansList, 0);
        System.out.println(ansList);
    }

    // [abc, acb, bac, bca, cba, cab]
    private static void generatePermutations(char[] str,
                                             List<String> ansList,
                                             int index) {

        if (index == str.length) ansList.add(new String(str));

        // swap first character with every other character
        // on right side of it and including itself
        for (int i = index; i < str.length; i++) {
            swap(i, index, str); // swap
            generatePermutations(str, ansList, index + 1); // recurse
            swap(i, index, str); //reswap
        }
    }

    private static void swap(int i, int index, char[] str) {
        char temp = str[i];
        str[i] = str[index];
        str[index] = temp;
    }
}
