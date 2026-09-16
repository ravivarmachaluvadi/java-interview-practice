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
