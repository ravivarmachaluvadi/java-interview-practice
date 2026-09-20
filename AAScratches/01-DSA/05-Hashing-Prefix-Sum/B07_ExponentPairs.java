/*
 * =====================================================================
 *  Exponent Pairs (a^b == b^a)                        Classic puzzle | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of positive integers, list every unique unordered pair of values (a, b),
 *   taken from two different positions, such that a^b == b^a. Equal values (a == b) trivially
 *   qualify, so a duplicated value produces the pair (a, a). Output pairs sorted, no repeats.
 *
 * EXAMPLE
 *   [2, 4, 16, 256, 3]  ->  [[2, 4]]           2^4 = 16 = 4^2
 *   [1, 5, 7]           ->  []                 no pair works
 *   [2, 4, 2]           ->  [[2, 2], [2, 4]]   the two 2s form a trivial pair
 *   [1000, 2000]        ->  []                 huge values must not be reported by mistake
 *
 * APPROACH  (brute-force pairs, set for dedup)  -- findPairs
 *   1. Nested loops over index pairs i < j. Starting j at i+1 visits each unordered pair once.
 *   2. Test a^b == b^a. Comparing b*ln(a) with a*ln(b) is safe where Math.pow is not: ln keeps
 *      the numbers tiny, so nothing overflows to Infinity.
 *   3. Store the pair as [min, max] in a Set so (4, 2) and (2, 4) collapse to one entry.
 *   4. Sort the set for stable output.
 *
 * Fixed: the original compared Math.pow(a, b) == Math.pow(b, a). Both sides overflow to
 *   Infinity for values around 1000 and up, and Infinity == Infinity is true, so any two
 *   large numbers were wrongly reported as a pair (e.g. 1000 and 2000).
 *
 * APPROACH 2  (closed form, no arithmetic at all)  -- findPairsClosedForm
 *   1. Same nested walk over index pairs i < j, so each unordered pair is visited once.
 *   2. Keep the pair only when a == b, or when {a, b} is exactly {2, 4}. No powers, no logs,
 *      so there is nothing left to overflow or to compare with a tolerance.
 *   3. Why that is the whole answer: a^b == b^a  <=>  ln(a)/a == ln(b)/b, and ln(x)/x is
 *      strictly increasing up to x = e and strictly decreasing after it. Two distinct values
 *      can therefore match only by straddling e, and 2 and 4 are the only integers that do.
 *   4. Dedupe with the same [min, max] Set and sort for stable output.
 *
 * KEY INSIGHT
 *   Take the log of both sides: a^b == b^a  <=>  ln(a)/a == ln(b)/b. The function ln(x)/x rises
 *   until x = e and then falls, so for distinct positive integers the only solution is {2, 4}.
 *   The closed form (second method) needs no arithmetic at all and is the interview punchline.
 *
 * COMPLEXITY
 *   Time  O(n^2)  every pair of positions is examined once
 *   Space O(n)    the set holds at most one (a, a) entry per distinct repeated value plus the
 *                 single {2, 4} pair, so it can never grow to O(n^2) despite the O(n^2) walk
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does Math.pow fail here and how would you check exactly? (logs, or BigInteger)
 *   - Prove that {2, 4} is the only distinct-integer solution (monotonicity of ln(x)/x).
 *   - Count pairs in O(n): count the 2s and 4s, plus C(c, 2) for every value with count c.
 *
 * RUN
 *   main() runs 4 cases (typical, none, duplicate value, overflow-sized) through both methods
 *   and prints actual vs expected.
 */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class ExponentPairs {

    /** Approach 1: brute force every pair, test with logs, dedupe with a Set. */
    public static List<List<Integer>> findPairs(int[] arr) {
        Set<List<Integer>> pairs = new HashSet<>();
        for (int i = 0; i < arr.length; i++) {
            // j starts at i + 1 so each unordered pair of positions is seen exactly once
            for (int j = i + 1; j < arr.length; j++) {
                if (powersEqual(arr[i], arr[j])) {
                    pairs.add(sortedPair(arr[i], arr[j]));
                }
            }
        }
        return sorted(pairs);
    }

    /** Approach 2: closed form. For positive ints a^b == b^a iff a == b or {a, b} == {2, 4}. */
    public static List<List<Integer>> findPairsClosedForm(int[] arr) {
        Set<List<Integer>> pairs = new HashSet<>();
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                int a = arr[i], b = arr[j];
                boolean isTwoAndFour = (a == 2 && b == 4) || (a == 4 && b == 2);
                if (a == b || isTwoAndFour) {
                    pairs.add(sortedPair(a, b));
                }
            }
        }
        return sorted(pairs);
    }

    /** a^b == b^a  <=>  b*ln(a) == a*ln(b). Requires a, b > 0. Logs cannot overflow like pow. */
    private static boolean powersEqual(int a, int b) {
        double left = b * Math.log(a);
        double right = a * Math.log(b);
        double tolerance = 1e-9 * Math.max(1.0, Math.abs(left));
        return Math.abs(left - right) < tolerance;
    }

    private static List<Integer> sortedPair(int a, int b) {
        return a <= b ? List.of(a, b) : List.of(b, a);
    }

    private static List<List<Integer>> sorted(Set<List<Integer>> pairs) {
        List<List<Integer>> result = new ArrayList<>(pairs);
        result.sort(Comparator.comparingInt((List<Integer> p) -> p.get(0))
                .thenComparingInt(p -> p.get(1)));
        return result;
    }

    private static void run(String label, int[] arr, String expected) {
        System.out.println(label + " brute  : " + findPairs(arr) + "   expected " + expected);
        System.out.println(label + " closed : " + findPairsClosedForm(arr)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 (typical)", new int[]{2, 4, 16, 256, 3}, "[[2, 4]]");
        run("case 2 (no pairs)", new int[]{1, 5, 7}, "[]");
        run("case 3 (duplicate value)", new int[]{2, 4, 2}, "[[2, 2], [2, 4]]");
        run("case 4 (overflow-sized)", new int[]{1000, 2000}, "[]");
    }
}
