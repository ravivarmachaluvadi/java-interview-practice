/*
 * =====================================================================
 *  Shell Sort (gapped insertion sort)                      Medium
 * =====================================================================
 *
 * PROBLEM
 *   Sort an integer array ascending, in place, with O(1) extra space, beating
 *   plain insertion sort on inputs where small values start far from the front.
 *   Duplicates, negatives, empty and single-element arrays must all work.
 *
 * EXAMPLE
 *   [21, 12, 14, 46, 7, 25, 10, 62, 19, 31, 1]
 *        ->  [1, 7, 10, 12, 14, 19, 21, 25, 31, 46, 62]
 *   [5, -2, 5, 0, -2]  ->  [-2, -2, 0, 5, 5]
 *   []                 ->  []        (gap starts at 0, so no loop body runs)
 *
 * APPROACH  (insertion sort with a shrinking gap)
 *   1. Start with gap = n / 2 and halve it each round until gap = 1.
 *   2. For a fixed gap, run an insertion sort on every "gap-chain" at once:
 *      compare arr[i] with arr[i - gap] instead of arr[i - 1].
 *   3. Save arr[j] in temp, then shift each larger gap-predecessor forward by gap
 *      until the slot for temp is found, and drop temp in.
 *   4. After a round with gap g, the array is "g-sorted": every element is within
 *      g of where it belongs relative to its chain.
 *   5. The last round has gap = 1, which is exactly insertion sort - but on data
 *      that is already nearly sorted, so it does very few shifts.
 *
 * KEY INSIGHT
 *   Insertion sort is slow only because each element moves one step at a time; a
 *   value stranded at the far end needs n shifts. The gap lets an element jump
 *   most of the way in a single move, and every gap round makes the final gap = 1
 *   pass cheap. Same loop as insertion sort, one extra outer loop - that is the
 *   whole algorithm.
 *
 * COMPLEXITY
 *   Time  O(n^2) worst with this halving sequence; about O(n^(3/2)) in practice,
 *         and O(n log n)-ish with better gaps (Ciura, Sedgewick). The bound depends
 *         entirely on the gap sequence, which is why no single clean bound exists.
 *   Space O(1)   in place; only temp and the loop counters
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does the gap sequence change the complexity, and what does Ciura's give?
 *   - Is shell sort stable? (No - a gapped move can jump an equal element.)
 *   - Why use it at all when quick sort exists? (tiny code, no recursion, no extra
 *     memory - embedded and uClibc qsort fallbacks use it)
 *   - Show that the final gap = 1 pass is literally insertion sort.
 *
 * RUN
 *   main() runs 4 cases (typical, empty, already sorted, duplicates + negatives)
 *   and prints actual vs expected.
 */
import java.util.Arrays;

class ShellSort {

    static void shellSort(int[] arr) {
        int n = arr.length;

        for (int gap = n / 2; gap > 0; gap = gap / 2) {
            // Insertion sort every gap-chain; interleaving them is what keeps this
            // a single pass per gap instead of one pass per chain.
            for (int j = gap; j < n; j++) {
                int temp = arr[j];                 // hold the value, leaving a hole at j

                int i;
                for (i = j; i >= gap && arr[i - gap] > temp; i -= gap) {
                    arr[i] = arr[i - gap];         // shift the bigger predecessor forward
                }
                arr[i] = temp;                     // hole has reached temp's place
            }
        }
    }

    public static void main(String[] args) {
        int[] typical = {21, 12, 14, 46, 7, 25, 10, 62, 19, 31, 1};
        shellSort(typical);
        print("case 1 typical      ", typical, "[1, 7, 10, 12, 14, 19, 21, 25, 31, 46, 62]");

        int[] empty = {};
        shellSort(empty);
        print("case 2 empty        ", empty, "[]");

        int[] sorted = {1, 2, 3, 4};
        shellSort(sorted);
        print("case 3 already sorted", sorted, "[1, 2, 3, 4]");

        int[] dupsAndNegatives = {5, -2, 5, 0, -2};
        shellSort(dupsAndNegatives);
        print("case 4 dups + negs  ", dupsAndNegatives, "[-2, -2, 0, 5, 5]");
    }

    private static void print(String label, int[] actual, String expected) {
        System.out.println(label + ": " + Arrays.toString(actual) + "   expected " + expected);
    }
}
