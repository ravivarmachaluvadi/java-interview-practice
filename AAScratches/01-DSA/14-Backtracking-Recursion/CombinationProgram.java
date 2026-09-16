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
