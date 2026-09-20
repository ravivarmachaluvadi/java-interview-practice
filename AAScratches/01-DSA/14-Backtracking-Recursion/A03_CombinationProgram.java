/*
 * =====================================================================
 *  Cartesian Product Of A List Of Lists                 Building block | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given k lists, produce every combination that takes exactly one element from each
 *   list, in list order. With lists of sizes n1..nk there are n1 * n2 * ... * nk results.
 *   Zero lists yields one result: the empty combination. Any empty list yields none.
 *
 * EXAMPLE
 *   [[A, B], [1, 2]]     ->  [[A, 1], [A, 2], [B, 1], [B, 2]]
 *   []                   ->  [[]]      no choices to make, so one (empty) combination
 *   [[A, B], [], [X]]    ->  []        the empty list kills every branch
 *
 * APPROACH  (Cartesian product by depth)
 *   1. depth = which list we are choosing from right now; it doubles as the length of
 *      the combination built so far.
 *   2. Base case: depth == number of lists means one element has been chosen from every
 *      list, so snapshot the running combination (copy it - see KEY INSIGHT) and return.
 *   3. Recursive case: loop over every element of listOfLists.get(depth): add it, recurse
 *      at depth + 1, then remove it so the next element of the loop starts clean.
 *
 * KEY INSIGHT
 *   add -> recurse -> remove is the backtracking heartbeat: one mutable list is reused by
 *   the whole tree, and each branch undoes exactly what it did. Two rules follow from
 *   that, and both are classic interview bugs: you must copy the list when you record a
 *   result (otherwise every stored result is the same object, later emptied), and the
 *   remove must be unconditional, even when the recursive call returned nothing.
 *   Fixed depth and one choice per level is the simplest tree of choices - pick/not-pick
 *   (Subsets) is the very next step up.
 *
 * COMPLEXITY
 *   Time  O(k * P)  where P = n1 * ... * nk results, each copied in O(k).
 *   Space O(k + k*P)  k stack frames and k slots in the running list, plus the output.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Write it iteratively (fold the lists one at a time into a growing result set).
 *   - Return an Iterator so callers stream results instead of materialising all of them.
 *   - What if a list may be empty - should the answer be empty or should you skip it?
 *   - How is this different from LeetCode 17 Letter Combinations? (it is the same code
 *     with the lists produced by a digit-to-letters lookup)
 *
 * RUN
 *   main() runs 4 cases (small typical, no lists, empty sublist, larger 3x2x3) and prints
 *   actual vs expected.
 */
import java.util.ArrayList;
import java.util.List;

class CombinationProgram {

    public static List<List<String>> generatePermutations(List<List<String>> listOfLists) {
        List<List<String>> results = new ArrayList<>();
        buildFromDepth(listOfLists, 0, new ArrayList<>(), results);
        return results;
    }

    private static void buildFromDepth(List<List<String>> listOfLists,
                                       int depth,
                                       List<String> current,
                                       List<List<String>> results) {
        if (depth == listOfLists.size()) {
            // Copy: 'current' keeps mutating after this line, so storing it directly
            // would store a reference that ends up empty.
            results.add(new ArrayList<>(current));
            return;
        }
        for (String element : listOfLists.get(depth)) {
            current.add(element);                                  // choose
            buildFromDepth(listOfLists, depth + 1, current, results); // explore
            current.remove(current.size() - 1);                    // un-choose
        }
    }

    public static void main(String[] args) {
        List<List<String>> small = List.of(List.of("A", "B"), List.of("1", "2"));
        print("case 1 (typical)", generatePermutations(small),
                "[[A, 1], [A, 2], [B, 1], [B, 2]]");

        List<List<String>> none = List.of();
        print("case 2 (no lists)", generatePermutations(none), "[[]]");

        List<List<String>> withEmpty = List.of(List.of("A", "B"), List.of(), List.of("X"));
        print("case 3 (empty sublist)", generatePermutations(withEmpty), "[]");

        List<List<String>> bigger = List.of(
                List.of("A", "B", "C"),
                List.of("1", "2"),
                List.of("X", "Y", "Z"));
        List<List<String>> all = generatePermutations(bigger);
        print("case 4 (3x2x3) count", all.size(), 18);
        print("case 4 first / last", all.get(0) + " / " + all.get(all.size() - 1),
                "[A, 1, X] / [C, 2, Z]");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
