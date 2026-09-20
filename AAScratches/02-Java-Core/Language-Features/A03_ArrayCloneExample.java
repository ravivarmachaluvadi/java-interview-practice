/*
 * =====================================================================
 *  Array clone() is a shallow copy            Java language | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   arr.clone() on a two-dimensional array copies only the OUTER array of
 *   references. The inner rows are shared between the original and the copy,
 *   so writing through one is visible through the other.
 *
 * WHAT YOU WILL SEE
 *   arr    = [[8, 9], [7, 8, 6]]
 *   cloned = arr.clone()          -> prints the same contents
 *   cloned != arr                 -> true  (a new outer array)
 *   cloned[0] == arr[0]           -> true  (the SAME row object)
 *   cloned[0][0] = 99             -> arr[0][0] is now 99 as well
 *   deepCopy(arr) breaks that link, so the original stays untouched.
 *
 * HOW IT WORKS
 *   1. int[][] is an array whose elements are int[] REFERENCES.
 *   2. clone() allocates a new outer array and copies those references, one
 *      slot at a time - it never looks inside a row.
 *   3. So cloned[0] and arr[0] point at one and the same int[] on the heap.
 *   4. deepCopy() fixes it by cloning each row as well; for a 1-D int[] a
 *      clone IS a full copy, because the elements are primitives, not refs.
 *
 * KEY INSIGHT
 *   clone(), Arrays.copyOf() and System.arraycopy() all copy one level. Copy
 *   depth must match reference depth: every level that holds references needs
 *   its own copy, which is exactly the defensive-copy rule for mutable fields.
 *
 * GOTCHAS
 *   - The same trap hits arrays of objects, List.copyOf() and new
 *     ArrayList<>(other): the elements themselves are still shared.
 *   - Arrays.equals() compares rows by reference; use Arrays.deepEquals().
 *   - Arrays.toString() on int[][] prints hashes; use Arrays.deepToString().
 *
 * INTERVIEW FOLLOW-UPS
 *   - How would you deep-copy an arbitrary object graph, and what about cycles?
 *   - Why is Cloneable considered a broken interface?
 *   - Where does a shallow copy actually bite you in production code?
 *   - Is a copy constructor or a static factory better than clone()? Why?
 *
 * RUN
 *   main() runs 3 cases (identity checks, shared-row mutation, deep copy) and
 *   prints actual vs expected.
 */
import java.util.Arrays;

class ArrayCloneExample {

    public static void main(String[] args) {
        int[][] arr = {{8, 9}, {7, 8, 6}};
        int[][] cloned = arr.clone();

        print("case 1: contents after clone", Arrays.deepToString(cloned),
                "[[8, 9], [7, 8, 6]]");
        print("case 1: new outer array?", cloned != arr, true);
        print("case 1: row 0 shared?", cloned[0] == arr[0], true);

        // Writing through the copy is visible through the original: same row object.
        cloned[0][0] = 99;
        print("case 2: original after cloned[0][0] = 99", Arrays.deepToString(arr),
                "[[99, 9], [7, 8, 6]]");

        // Edge case: a deep copy gives rows of its own, so the original is safe.
        int[][] deep = deepCopy(arr);
        deep[0][0] = -1;
        print("case 3: original after deep copy write", Arrays.deepToString(arr),
                "[[99, 9], [7, 8, 6]]");
        print("case 3: deep copy itself", Arrays.deepToString(deep), "[[-1, 9], [7, 8, 6]]");
        print("case 3: row 0 shared?", deep[0] == arr[0], false);
    }

    /** Clones the outer array AND every row, so nothing is shared. */
    private static int[][] deepCopy(int[][] source) {
        int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i].clone();  // rows hold primitives, so this is a full copy
        }
        return copy;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
