/**
 * Problem: Given an integer array, produce a new array where elements are sorted by their frequency of occurrence in ascending order.
 *
 * Approach:
 * 1. Count occurrences using a LinkedHashMap to preserve insertion order.
 * 2. Sort the map entries by count (value) with natural ordering.
 * 3. Expand each entry back into an array segment containing its key repeated count times, then flatten all segments into the final result.
 *
 * Time Complexity: O(n log n) due to sorting of distinct values; counting and reconstruction are linear.
 * Space Complexity: O(k + n), where k is number of distinct elements (for the map) plus output array size n. */
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

class SortBasedOnFrequencyOfOccurrence {
    public static void main(String[] args) {

        // sort based on frequency of occurrence
        int[] arr = {1, 1, 1, 1, 2, 2, 3, 3, 3, 5};
        int[] array = Arrays.stream(arr).boxed()
                .collect(Collectors
                        .groupingBy(
                                Function.identity(),
                                LinkedHashMap::new,
                                Collectors.counting()
                        )
                )
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.naturalOrder()))
                .map(integerLongEntry -> {
                            int[] ints = new int[integerLongEntry.getValue().intValue()];
                            Arrays.fill(ints, integerLongEntry.getKey());
                            return ints;
                        }
                )
                //remember this
                .flatMapToInt(ints -> Arrays.stream(ints))
                .toArray();

        for (int i : array) {
            System.out.print(i);
        }
    }
}
