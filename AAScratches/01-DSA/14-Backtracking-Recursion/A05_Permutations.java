/*
 * =====================================================================
 *  String Permutations                      LeetCode 46 (variant) | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given a string, return every ordering of its characters. A string of length n has
 *   n! orderings. The input is short (n is typically <= 8) because the output explodes.
 *   If the input has repeated characters this method returns repeated permutations - see
 *   case 3 and the follow-ups.
 *
 * EXAMPLE
 *   "abc"  ->  [abc, acb, bac, bca, cba, cab]   6 = 3! orderings, swap order (not sorted)
 *   ""     ->  [""]                             one ordering: the empty one
 *   "aab"  ->  [aab, aba, aab, aba, baa, baa]   duplicates in, duplicates out
 *
 * APPROACH  (swap in place, recurse, swap back)
 *   1. index = the position currently being fixed; everything left of it is already
 *      decided, everything from index onwards is still free.
 *   2. Base case: index == length means all positions are fixed, so snapshot the array
 *      as a new String and return.
 *   3. For every i from index to the end: swap str[i] into position index, recurse on
 *      index + 1 to arrange the remaining suffix, then swap back.
 *   The swap back is the whole point: it restores the array so the next i in the loop
 *   starts from the same state its siblings did.
 *
 * KEY INSIGHT
 *   Permutations differ from subsets in what the loop means. In subsets the choice is
 *   "in or out"; here the choice is "which of the remaining characters goes next", so
 *   the branching factor shrinks n, n-1, n-2 ... giving n! leaves instead of 2^n.
 *   Swapping mutates the single shared array, so backtracking is literally undoing the
 *   swap - this is the cleanest demonstration in the folder that backtracking means
 *   restoring state, not building new state. Note the output order is NOT lexicographic:
 *   after the swap the suffix is no longer sorted, which is why "cba" comes before "cab".
 *
 * COMPLEXITY
 *   Time  O(n! * n)  n! permutations, each costing O(n) to copy into a String.
 *   Space O(n) working (recursion depth n, the char array is shared and reused),
 *         plus O(n! * n) for the result list.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Input has duplicates - return only distinct permutations. (sort, then skip
 *     equal characters at the same depth, or use a HashSet per level: LeetCode 47)
 *   - Produce them in lexicographic order. (use the used[] + sorted-input template, or
 *     repeatedly apply next-permutation)
 *   - Return only the k-th permutation without generating the rest. (D02_KthPermutation, factorial
 *     number system - counting beats generating)
 *   - Why does swapping i with index work, and what breaks if you forget the swap back?
 *
 * RUN
 *   main() runs 4 cases (typical, empty, single char, repeated chars) and prints
 *   actual vs expected.
 */
import java.util.ArrayList;
import java.util.List;

class Permuataions {

    /** Entry point: returns every ordering of the characters of {@code input}. */
    public static List<String> permutationsOf(String input) {
        List<String> ansList = new ArrayList<>();
        generatePermutations(input.toCharArray(), ansList, 0);
        return ansList;
    }

    private static void generatePermutations(char[] str, List<String> ansList, int index) {
        if (index == str.length) {
            // Every position is fixed: copy the array out before it is mutated again.
            ansList.add(new String(str));
            return;
        }
        // Try every still-free character in the position 'index'.
        for (int i = index; i < str.length; i++) {
            swap(str, i, index);                          // choose
            generatePermutations(str, ansList, index + 1); // explore the suffix
            swap(str, i, index);                          // un-choose (restore the array)
        }
    }

    private static void swap(char[] str, int i, int j) {
        char temp = str[i];
        str[i] = str[j];
        str[j] = temp;
    }

    public static void main(String[] args) {
        print("case 1 (typical) \"abc\"", permutationsOf("abc"), "[abc, acb, bac, bca, cba, cab]");

        List<String> ofEmpty = permutationsOf("");
        // An empty string prints as nothing inside the brackets, so show the size too.
        print("case 2 (empty)   \"\"   ", ofEmpty + " size " + ofEmpty.size(), "[] size 1");

        print("case 3 (single)  \"a\"  ", permutationsOf("a"), "[a]");

        // Tricky: swapping does not know about duplicate characters, so "aab" yields
        // 3! = 6 results with repeats. Deduping is LeetCode 47's job.
        print("case 4 (repeats) \"aab\"", permutationsOf("aab"), "[aab, aba, aab, aba, baa, baa]");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
