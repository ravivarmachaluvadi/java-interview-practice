/*
 * =====================================================================
 *  Stack Sortable Permutation                       GeeksforGeeks | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A is a permutation of 1..n. You read it left to right; each element must be
 *   pushed onto a single stack, and at any moment you may pop the top to the output.
 *   Return true if the output can be exactly 1, 2, ..., n (the sorted order).
 *
 * EXAMPLE
 *   [4, 1, 2, 3]  ->  true    push 4; push 1 pop 1; push 2 pop 2; push 3 pop 3; pop 4
 *   [4, 3, 1, 2]  ->  true    push 4,3,1; pop 1; push 2; pop 2, pop 3, pop 4
 *   [2, 3, 1]     ->  false   2 is buried under 3, but 2 is due before 3
 *   [3, 1, 4, 2]  ->  false   after popping 1, 3 is under 4 and 2 is still coming
 *   [1, 2, 3, 4]  ->  true    already sorted, pop each right after its push
 *   []            ->  true    nothing to sort
 *
 * APPROACH  (greedy stack simulation)
 *   1. expected = 1, the next value the sorted output needs.
 *   2. For each element: push it, then while the top equals expected, pop and
 *      expected++. Popping as early as possible is always safe: the top is exactly
 *      what the output needs, and holding it can only block later values.
 *   3. After the scan, sortable iff expected == n + 1 (every value was output in order).
 *
 * KEY INSIGHT
 *   The stack models a process, not a pairing of symbols. The greedy rule works
 *   because the stack is LIFO: if a smaller value ever ends up below a larger one
 *   while an even smaller value is still to come (the 2-3-1 pattern), the smaller
 *   one can never get out in time. Popping whenever the top is 'expected' is the
 *   only way to avoid burying it. Same simulation as Validate Stack Sequences.
 *
 * COMPLEXITY
 *   Time  O(n)  each value is pushed once and popped at most once
 *   Space O(n)  the stack in the worst case (a descending input)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Validate Stack Sequences (LC 946): same loop, but the pop order is a given array
 *   - Characterise sortable permutations: exactly those avoiding the 2-3-1 pattern
 *   - Count them: the n-th Catalan number
 *   - What if A is not a permutation (duplicates or gaps)? The == expected test fails
 *     and the method returns false, which is the right answer for "sorted 1..n"
 *
 * RUN
 *   main() runs 6 cases (typical, hidden-true, two false shapes, sorted, empty) and
 *   prints actual vs expected. Fixed: main labelled every line "A1" and claimed
 *   [4,3,1,2] is not sortable; it is (the code was right, the comment was wrong).
 */
import java.util.Arrays;
import java.util.Stack;

class StackSortable {

    public static boolean isStackSortable(int[] A) {
        Stack<Integer> stack = new Stack<>();
        int expected = 1; // the next value the sorted output needs

        for (int value : A) {
            stack.push(value);
            // Pop greedily while the top is exactly what the output needs next
            while (!stack.isEmpty() && stack.peek() == expected) {
                stack.pop();
                expected++;
            }
        }
        // Every value 1..n was output in order iff expected advanced past n
        return expected == A.length + 1;
    }

    private static void print(String label, int[] A, boolean expected) {
        System.out.println(label + " " + Arrays.toString(A) + " -> " + isStackSortable(A)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1", new int[]{4, 1, 2, 3}, true);
        print("case 2", new int[]{4, 3, 1, 2}, true);
        print("case 3", new int[]{2, 3, 1}, false);
        print("case 4", new int[]{3, 1, 4, 2}, false);
        print("case 5", new int[]{1, 2, 3, 4}, true);
        print("case 6", new int[]{}, true);
    }
}
