import java.util.Arrays;
import java.util.stream.Collectors;

class FirstCharToUpperCase {
    public static void main(String[] args) {
        String str = "ravi varma";
        String capitalizedStr = Arrays
                .stream(str.split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
        System.out.println(capitalizedStr); // Ravi Varma
    }
}
