/*
 * =====================================================================
 *  Prefix vs postfix increment                       Tricky MCQ | Easy
 * =====================================================================
 *
 * QUESTION
 *   int[] arr = new int[2];
 *   int i = 0, j = 0;
 *   arr[0] = ++i;
 *   arr[1] = j++;
 *   What does Arrays.toString(arr) print, and what are i and j afterwards?
 *
 * OPTIONS
 *   A. [0, 0]      B. [1, 0]      C. [1, 1]      D. [0, 1]
 *
 * HOW TO REASON ABOUT IT
 *   1. ++i increments i first, then the expression evaluates to the NEW value (1).
 *   2. j++ evaluates to the OLD value (0) first, then increments j behind your back.
 *   3. Both variables end at 1; only the value handed to the assignment differs.
 *
 * GOTCHAS
 *   - "i = i++" is the classic trap: the old value is saved, i is incremented to 1,
 *     then the saved 0 is written back over it. i ends at 0, not 1.
 *   - In "a[k++] = k" Java evaluates the array index BEFORE the right-hand side
 *     (strict left-to-right), so the index uses the old k and the value the new k.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is i++ not atomic, and what does that mean for two threads? (Use AtomicInteger.)
 *   - Is ++i faster than i++ in Java? (For int, no - the JIT emits the same iinc.)
 *   - What does "System.out.println(i++ + ++i)" print when i starts at 1? (1 + 3 = 4.)
 *
 * RUN
 *   main() runs 3 cases: the original puzzle, the "i = i++" self-assignment trap,
 *   and the array-index evaluation-order case. Each prints actual vs expected.
 *
 * ---------------------------------------------------------------------
 * ANSWER  (stop above if you want to solve it yourself)
 * ---------------------------------------------------------------------
 *   B. [1, 0]   and afterwards i == 1, j == 1.
 *
 * WHY
 *   Prefix yields the value after incrementing, postfix yields the value before.
 *   The side effect on the variable is identical in both cases; what changes is only
 *   what the surrounding expression sees.
 */
import java.util.Arrays;

class Tricky10 {

    public static void main(String[] args) {
        print("case 1 (prefix vs postfix)", Arrays.toString(prefixVsPostfix()), "[1, 0]");
        print("case 2 (i = i++ trap)     ", selfAssignedPostfix(), "0");
        print("case 3 (index eval order) ", Arrays.toString(indexEvaluationOrder()), "[1, 0, 0]");
    }

    /** The original puzzle: ++i stores the new value, j++ stores the old one. */
    static int[] prefixVsPostfix() {
        int[] arr = new int[2];
        int i = 0, j = 0;
        arr[0] = ++i; // i becomes 1, expression is 1
        arr[1] = j++; // expression is 0, j becomes 1
        return arr;
    }

    /** i is incremented, then the saved old value is written back over it. */
    static int selfAssignedPostfix() {
        int i = 0;
        i = i++;
        return i;
    }

    /** Java evaluates the array index before the right-hand side. */
    static int[] indexEvaluationOrder() {
        int[] a = new int[3];
        int k = 0;
        a[k++] = k; // slot 0 is chosen while k is 0, then 1 is stored there
        return a;
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
