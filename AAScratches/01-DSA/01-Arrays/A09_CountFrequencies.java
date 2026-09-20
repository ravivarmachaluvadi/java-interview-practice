/*
 * =====================================================================
 *  Count Frequencies In Place                        Warm-up | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an int array of length n whose values are all in 1..n, return an
 *   array freq where freq[v - 1] is how many times value v occurs. Use O(1)
 *   extra space beyond the output, and leave the input unchanged when done.
 *
 * EXAMPLE
 *   [3, 3, 3, 5, 5]  ->  [0, 0, 3, 0, 2]   3 occurs three times, 5 twice
 *   [1, 1, 1, 1]     ->  [4, 0, 0, 0]      all equal
 *   [1]              ->  [1]               single element
 *   [10, 5, 10, 15, 10, 5]  ->  {5=2, 10=3, 15=1}   values outside 1..n: map method
 *
 * APPROACH  (In-place counting via value encoding)
 *   1. Shift every value down by one so value v is the index v - 1 (0..n-1).
 *   2. For each i: original = input[i] % n, then input[original] += n.
 *      Every original value is < n, so % n recovers it no matter how many
 *      multiples of n have already been piled onto that slot.
 *   3. Now input[i] / n is the count of value i + 1. Copy it into freq[i].
 *   4. Restore: input[i] = input[i] % n + 1 puts the original value back.
 *
 * KEY INSIGHT
 *   When values are bounded by the array length, the array can be its own hash
 *   map (index = value), and one slot can hold two numbers at once because
 *   (original + k * n) % n == original and (original + k * n) / n == k.
 *   Sign marking (C14), cyclic sort (C13, C15) and first-missing-positive (D01)
 *   are all variations of "encode extra information into the slot itself".
 *
 * COMPLEXITY
 *   Time  O(n)  three linear passes
 *   Space O(1)  extra, besides the returned freq array; the input is restored
 *
 * INTERVIEW FOLLOW-UPS
 *   - Values not in 1..n: the encoding breaks; use a HashMap (O(n) space), see below.
 *   - Why % n instead of the raw value? A slot may already hold original + k * n.
 *   - Can you skip the decrement pass? Yes: index by (v - 1) and take % n on the read.
 *
 * RUN
 *   main() runs 4 cases: typical, all equal, single element (encoding method)
 *   and out-of-range values (map method), printing actual vs expected.
 *
 * Fixed: the second sample used values outside 1..n, which the encoding trick
 * cannot handle (it printed "4 occurs 5 times"); that input now runs the map method.
 */
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

class CountFrequencies {

    /** Values must be in 1..n. O(1) extra space; the input is restored before returning. */
    public static int[] countFrequenciesEfficient(int[] input) {
        int n = input.length;
        for (int i = 0; i < n; i++) {
            input[i]--;                    // value v now names index v - 1
        }
        for (int i = 0; i < n; i++) {
            int original = input[i] % n;   // strip any n's already added to this slot
            input[original] += n;          // one more occurrence of value original + 1
        }
        int[] freq = new int[n];
        for (int i = 0; i < n; i++) {
            freq[i] = input[i] / n;        // how many times n was added here
            input[i] = input[i] % n + 1;   // put the original value back
        }
        return freq;
    }

    /** Works for any values at the cost of O(n) extra space. TreeMap keeps the output sorted. */
    public static Map<Integer, Integer> countFrequenciesWithMap(int[] input) {
        Map<Integer, Integer> freq = new TreeMap<>();
        for (int v : input) {
            freq.merge(v, 1, Integer::sum);
        }
        return freq;
    }

    private static void run(String label, int[] input, String expected) {
        int[] before = input.clone();
        String actual = Arrays.toString(countFrequenciesEfficient(input));
        String restored = Arrays.equals(before, input) ? "input restored" : "INPUT CHANGED";
        System.out.println(label + ": " + actual + "   expected " + expected
                + "   (" + restored + ")");
    }

    public static void main(String[] args) {
        run("case 1 typical  ", new int[]{3, 3, 3, 5, 5}, "[0, 0, 3, 0, 2]");
        run("case 2 all equal", new int[]{1, 1, 1, 1}, "[4, 0, 0, 0]");
        run("case 3 single   ", new int[]{1}, "[1]");
        System.out.println("case 4 map, any values: "
                + countFrequenciesWithMap(new int[]{10, 5, 10, 15, 10, 5})
                + "   expected {5=2, 10=3, 15=1}");
    }
}
