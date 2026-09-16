/**
 * Generates all Cartesian product combinations from a list of lists.
 *
 * The program takes multiple lists (e.g., ["A","B","C"], ["1","2"], ["X","Y","Z"])
 * and produces every possible combination where one element is chosen from each
 * sublist, printing them to the console.
 *
 * Approach:
 * Recursively build permutations by selecting an element from the current list,
 * appending it to a running permutation, then recursing to the next depth.
 * When all depths are processed, add the completed permutation to the result set.
 *
 * Time Complexity: O(n₁ × n₂ × … × n_k) where n_i is the size of each sublist
 * (total number of generated permutations).
 * Space Complexity: O(k + m) where k is the recursion depth (number of lists)
 * and m is the total number of permutations stored in memory.
 */
import java.util.*;

class CombinationProgram {

    public static void main(String[] args) {
        List<List<String>> listOfLists = new ArrayList<>();
        listOfLists.add(List.of("A", "B", "C"));
        listOfLists.add(List.of("1", "2"));
        listOfLists.add(List.of("X", "Y", "Z"));
        List<List<String>> permutations = generatePermutations(listOfLists);
        for (List<String> permutation : permutations) System.out.println(permutation);
    }

    public static List<List<String>> generatePermutations(List<List<String>> listOfLists) {
        List<List<String>> permutations = new ArrayList<>();
        generatePermutationsHelper(listOfLists, 0, new ArrayList<>(), permutations);
        return permutations;
    }

    private static void generatePermutationsHelper(List<List<String>> listOfLists, int depth, List<String> currentPermutation, List<List<String>> permutations) {
        if (depth == listOfLists.size()) {
            permutations.add(new ArrayList<>(currentPermutation));
            return;
        }
        List<String> currentList = listOfLists.get(depth);
        for (String element : currentList) {
            currentPermutation.add(element);
            generatePermutationsHelper(listOfLists, depth + 1, currentPermutation, permutations);
            currentPermutation.remove(currentPermutation.size() - 1);
        }
    }
}
