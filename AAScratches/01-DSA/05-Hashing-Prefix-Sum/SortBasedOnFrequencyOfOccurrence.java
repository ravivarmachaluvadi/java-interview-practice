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
