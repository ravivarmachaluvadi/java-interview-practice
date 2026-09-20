/*
 * =====================================================================
 *  Sort Array by Frequency of Occurrence          LeetCode 1636 variant | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array, return a new array with the elements ordered by how often
 *   they occur, least frequent first. All copies of a value stay together. When two
 *   values have the same frequency, the one seen first in the input comes first.
 *   (LC 1636 breaks ties by decreasing value instead; that is a one-line comparator change.)
 *
 * EXAMPLE
 *   [1,1,1,1,2,2,3,3,3,5]  ->  [5, 2, 2, 3, 3, 3, 1, 1, 1, 1]
 *   []                     ->  []
 *   [4,4,6,6,2,2]          ->  [4, 4, 6, 6, 2, 2]   all tied, so input order is kept
 *   [-1,7,-1]              ->  [7, -1, -1]
 *
 * APPROACH  (count, then order by count)
 *   1. Count each value into a LinkedHashMap so first-seen order is remembered.
 *   2. Sort the map entries by count. Both Stream.sorted() on an ordered stream and
 *      List.sort() are stable, so ties keep the insertion order from step 1.
 *   3. Expand each entry back into "value repeated count times" and concatenate.
 *   The file has the author's stream pipeline and an equivalent plain-loop version.
 *
 * KEY INSIGHT
 *   Counting only sets up the problem; the answer is a ranking by count. Once you see
 *   "sort by frequency", the shape is always: count map -> sort or heap or bucket over
 *   the (value, count) pairs -> rebuild. Stability of the sort is what makes tie
 *   ordering predictable, and it is the detail interviewers poke at.
 *
 * COMPLEXITY
 *   Time  O(n + d log d)  n to count, d distinct values to sort, n to rebuild
 *   Space O(n + d)        the count map plus the output array
 *
 * INTERVIEW FOLLOW-UPS
 *   - LC 347 Top K Frequent: same count map, then a min-heap of size k or bucket sort by
 *     count for O(n).
 *   - Descending frequency, or ties by value: change the comparator only.
 *   - Strings instead of ints (LC 451 Sort Characters by Frequency): same skeleton.
 *
 * RUN
 *   main() runs 4 cases (typical, empty, all-tied, negatives) through both versions and
 *   prints actual vs expected.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

class SortBasedOnFrequencyOfOccurrence {

    /** Author's approach: one stream pipeline from counting to the flattened result. */
    public static int[] sortByFrequencyStream(int[] arr) {
        return Arrays.stream(arr).boxed()
                // LinkedHashMap keeps first-seen order, which decides ties later
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new,
                        Collectors.counting()))
                .entrySet()
                .stream()
                // stable on ordered streams, so ties keep first-seen order
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .map(entry -> {
                    int[] repeated = new int[entry.getValue().intValue()];
                    Arrays.fill(repeated, entry.getKey());
                    return repeated;
                })
                .flatMapToInt(Arrays::stream)   // int[] segments -> one IntStream
                .toArray();
    }

    /** Same algorithm without streams, the version to write on a whiteboard. */
    public static int[] sortByFrequencyLoop(int[] arr) {
        Map<Integer, Integer> count = new LinkedHashMap<>();
        for (int value : arr) count.merge(value, 1, Integer::sum);

        List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(count.entrySet());
        entries.sort(Map.Entry.comparingByValue());   // List.sort is stable (TimSort)

        int[] result = new int[arr.length];
        int pos = 0;
        for (Map.Entry<Integer, Integer> entry : entries) {
            for (int i = 0; i < entry.getValue(); i++) result[pos++] = entry.getKey();
        }
        return result;
    }

    private static void print(String label, int[] actual, String expected) {
        System.out.println(label + ": " + Arrays.toString(actual) + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] typical = {1, 1, 1, 1, 2, 2, 3, 3, 3, 5};
        int[] empty = {};
        int[] allTied = {4, 4, 6, 6, 2, 2};
        int[] negatives = {-1, 7, -1};

        print("case 1 stream", sortByFrequencyStream(typical), "[5, 2, 2, 3, 3, 3, 1, 1, 1, 1]");
        print("case 1 loop  ", sortByFrequencyLoop(typical), "[5, 2, 2, 3, 3, 3, 1, 1, 1, 1]");
        print("case 2 stream", sortByFrequencyStream(empty), "[]");
        print("case 2 loop  ", sortByFrequencyLoop(empty), "[]");
        print("case 3 stream", sortByFrequencyStream(allTied), "[4, 4, 6, 6, 2, 2]");
        print("case 3 loop  ", sortByFrequencyLoop(allTied), "[4, 4, 6, 6, 2, 2]");
        print("case 4 stream", sortByFrequencyStream(negatives), "[7, -1, -1]");
        print("case 4 loop  ", sortByFrequencyLoop(negatives), "[7, -1, -1]");
    }
}
