/*
 * =====================================================================
 *  Print Array In Cyclic Order                       Warm-up | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an int array of length n and a row width, produce n rows. Row r lists
 *   `width` consecutive elements starting at index r, wrapping around to the
 *   start of the array when the end is reached. Return the rows as strings.
 *
 * EXAMPLE
 *   arr = [1, 2, 3, 4, 5, 6], width 4  ->  1234 / 2345 / 3456 / 4561 / 5612 / 6123
 *   arr = [1, 2, 3], width 4           ->  1231 / 2312 / 3123   (width > n wraps twice)
 *   arr = [9], width 3                 ->  999                  (single element)
 *
 * APPROACH  (Modulo wraparound indexing)
 *   1. For each row r in 0..n-1 the starting index is r.
 *   2. For each column c in 0..width-1 read arr[(r + c) % n].
 *   3. The % n turns an index that ran past the end back into 0, 1, 2, ...
 *   4. Collect each row into a string; main() prints and compares them.
 *
 * KEY INSIGHT
 *   index % n is the "wrap" operator: it makes a flat array behave like a ring.
 *   Rotation, circular buffers, cyclic placement and the (start + i) % n trick
 *   for O(1) rotation are all this one expression in different clothes.
 *
 * COMPLEXITY
 *   Time  O(n * width)  one read per printed cell
 *   Space O(n * width)  for the returned row strings (O(1) if printed directly)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Print anticlockwise: read arr[((r - c) % n + n) % n]; the + n fixes a negative %.
 *   - Circular buffer / ring queue: head and tail advance with (idx + 1) % capacity.
 *   - Rotate by k without moving data: element i of the view is arr[(i + k) % n].
 *
 * RUN
 *   main() runs 3 cases (typical 6x4, width larger than n, single element) and
 *   prints actual vs expected.
 *
 * Fixed: the row count and modulus were hard-coded to 6 instead of arr.length.
 */
import java.util.ArrayList;
import java.util.List;

class PrintArrayInCyclic {

    /** Row r holds `width` elements starting at arr[r]; % n wraps past the end. */
    public static List<String> cyclicRows(int[] arr, int width) {
        int n = arr.length;
        List<String> rows = new ArrayList<>();
        for (int row = 0; row < n; row++) {
            StringBuilder line = new StringBuilder();
            for (int col = 0; col < width; col++) {
                line.append(arr[(row + col) % n]);  // % n wraps back to index 0
            }
            rows.add(line.toString());
        }
        return rows;
    }

    private static void run(String label, int[] arr, int width, List<String> expected) {
        List<String> actual = cyclicRows(arr, width);
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 typical 6x4", new int[]{1, 2, 3, 4, 5, 6}, 4,
                List.of("1234", "2345", "3456", "4561", "5612", "6123"));
        run("case 2 width > n  ", new int[]{1, 2, 3}, 4, List.of("1231", "2312", "3123"));
        run("case 3 single     ", new int[]{9}, 3, List.of("999"));
    }
}
