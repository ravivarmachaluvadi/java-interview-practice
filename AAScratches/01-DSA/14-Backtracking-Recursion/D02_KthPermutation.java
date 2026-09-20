/*
 * =====================================================================
 *  Permutation Sequence                              LeetCode 60 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   The numbers 1..n have n! permutations. Written out in increasing
 *   (lexicographic) order they form a sequence. Given n and k, return the k-th
 *   permutation of that sequence as a String.
 *   k is 1-based, and 1 <= n <= 9, so both n! and k fit in an int.
 *
 * EXAMPLE
 *   n = 3, k = 3   ->  "213"    order is 123, 132, 213, 231, 312, 321
 *   n = 4, k = 9   ->  "2314" n = 4, k = 24  ->  "4321"   edge case: the very last permutation
 *   n = 1, k = 1   ->  "1"      edge case: only one permutation exists
 *
 * APPROACH  (factorial number system - counting instead of generating)
 *   1. Make k zero-based (k = k - 1) so it becomes a plain offset into the list.
 *   2. Keep the unused digits in a sorted list, and keep fact = (size - 1)!,
 *      which is how many permutations share any one fixed leading digit.
 *   3. The leading digit is therefore the element at index k / fact. Append it
 *      and remove it - the remaining list is still sorted.
 *   4. k = k % fact  throws away the whole blocks we just skipped over;
 *      fact = fact / newSize  shrinks the block size for the next position.
 *   5. Repeat until the list is empty.
 *
 *   bruteForcePermutation() is here only as a cross-check: it really does build
 *   all n! permutations in order and index into them. main() runs both and
 *   compares them for every k of n = 1..6, which is how we know the fast
 *   version is right.
 *
 * KEY INSIGHT
 *   Fixing the first digit fixes a contiguous BLOCK of (n-1)! permutations, so
 *   k / (n-1)! names the block and k % (n-1)! is the offset inside it. Once you
 *   can count how many candidates a choice covers, you jump straight to the
 *   answer instead of backtracking through the ones you do not want.
 *   Recognise this whenever a problem asks for the k-th item of an ordered
 *   enumeration rather than for all of them.
 *
 * COMPLEXITY
 *   Time  O(n^2)  n positions, and each ArrayList.remove shifts up to n elements
 *   Space O(n)    the list of unused digits plus the output builder
 *   (brute force is O(n! * n) time and space - the thing we are avoiding)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Inverse problem: given a permutation, find its rank k (count smaller digits
 *     to the right of each position and multiply by the factorials).
 *   - Next permutation in O(n) without computing k at all (LeetCode 31).
 *   - What breaks if n can be 20? n! overflows long; k must be a BigInteger.
 *   - Replace the ArrayList with an order-statistic tree for O(n log n).
 *
 * RUN
 *   main() runs 4 direct cases plus an exhaustive cross-check of every k for
 *   n = 1..6 against the brute-force generator, printing actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class KthPermutation {

    /**
     * Fast version: walks the factorial number system, one digit at a time.
     */
    static String getPermutation(int n, int k) {
        // numbers holds the still-unused digits, always in ascending order.
        // fact ends up as (n-1)!: the size of the block that shares a leading digit.
        int fact = 1;
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i < n; i++) {
            fact = fact * i;
            numbers.add(i);
        }
        numbers.add(n); // n itself is added without multiplying it into fact

        k = k - 1; // switch to a zero-based offset

        StringBuilder ans = new StringBuilder();
        while (true) {
            int index = k / fact; // which block of size fact does k fall in?
            ans.append(numbers.get(index));
            numbers.remove(index);
            if (numbers.isEmpty()) {
                break;
            }
            k = k % fact;              // drop the blocks we skipped past
            fact = fact / numbers.size(); // next level's block is smaller
        }
        return ans.toString();
    }

    /**
     * Reference version: generates every permutation in lexicographic order and
     * picks the k-th. Only used to prove getPermutation correct on small n.
     */
    static String bruteForcePermutation(int n, int k) {
        List<Integer> remaining = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            remaining.add(i);
        }
        List<String> all = new ArrayList<>();
        generate(remaining, new StringBuilder(), all);
        return all.get(k - 1);
    }

    private static void generate(List<Integer> remaining, StringBuilder current,
                                 List<String> out) {
        if (remaining.isEmpty()) {
            out.add(current.toString());
            return;
        }
        // remaining is sorted, so taking it left to right emits lexicographic order
        for (int i = 0; i < remaining.size(); i++) {
            int picked = remaining.remove(i);
            current.append(picked);
            generate(remaining, current, out);
            current.deleteCharAt(current.length() - 1); // undo
            remaining.add(i, picked);                   // undo
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (n=3, k=3)", getPermutation(3, 3), "213");
        print("case 2 (n=1, k=1)", getPermutation(1, 1), "1");
        print("case 3 (n=4, k=9)", getPermutation(4, 9), "2314");
        print("case 4 (n=4, k=24, last)", getPermutation(4, 24), "4321");

        // Exhaustive cross-check: fast version must match the generated list for
        // every k of every n up to 6.
        boolean allMatch = true;
        for (int n = 1; n <= 6; n++) {
            int factorial = 1;
            for (int i = 2; i <= n; i++) {
                factorial *= i;
            }
            for (int k = 1; k <= factorial; k++) {
                if (!getPermutation(n, k).equals(bruteForcePermutation(n, k))) {
                    allMatch = false;
                    System.out.println("  mismatch at n=" + n + ", k=" + k);
                }
            }
        }
        print("case 5 (n=1..6, all k match brute force)", allMatch, true);
    }
}
