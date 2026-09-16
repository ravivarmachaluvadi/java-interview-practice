/**
 * Reads exactly five whitespace‑separated tokens from standard input
 * and echoes each one on its own line.
 *
 * The program uses a {@link java.util.Scanner} to read tokens with
 * {@code next()}, looping five times and printing the token immediately.
 *
 * Time Complexity: O(5) ≈ O(1), since it performs a constant number of I/O operations.
 * Space Complexity: O(1), only a few primitive variables are used regardless of input size.
 */
import java.util.Scanner;

class ScannerMain {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        for (int i = 0; i < 5; i++) {
//            in.nextInt ( );
            System.out.println(in.next());
        }
    }
}
