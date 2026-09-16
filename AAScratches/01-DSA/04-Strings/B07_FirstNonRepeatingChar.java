/**
 * Finds the first non‑repeating character in a string and records its index.
 *
 * The algorithm builds a LinkedHashMap that preserves insertion order while
 * counting occurrences of each character via Java Streams. It then streams the
 * map entries, filters for those with count == 1, and takes the first such entry.
 * If found, it sets an AtomicInteger to the index of that character in the original string.
 *
 * Time Complexity: O(n) – single pass over characters plus constant‑time map operations.
 * Space Complexity: O(k) – where k is the number of distinct characters (at most 256 for ASCII).
 */
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

class FirstNonRepeatingChar {
    public static void main(String[] args) {
        String str = "loveleetcode";

        AtomicInteger index = new AtomicInteger(-1);

        str
                .chars()
                .mapToObj(value -> (char) value)
                .collect(Collectors
                        .groupingBy(
                                Function.identity(),
                                LinkedHashMap::new,
                                counting()
                        )
                )
                .entrySet()
                .stream()
                .filter(characterLongEntry -> characterLongEntry.getValue() == 1)
                .findFirst()
                .ifPresent(characterLongEntry -> index
                        .set(str.indexOf(characterLongEntry.getKey())));

    }
}
