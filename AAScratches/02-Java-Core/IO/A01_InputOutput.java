/**
 * Reads an integer from standard input and echoes it back to the console.
 *
 * The program prompts the user with "Enter a number:", reads the next integer
 * token using java.util.Scanner, and immediately prints that value without any
 * additional formatting or processing.
 *
 * Approach:
 * 1. Create a Scanner tied to System.in.
 * 2. Prompt the user for input.
 * 3. Read an int with nextInt().
 * 4. Print the integer directly.
 *
 * Time Complexity: O(1) – constant time operations regardless of input size.
 * Space Complexity: O(1) – only a few primitive variables are used.
 */
import java.util.Scanner;

class InputOutput {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a number:");
        int number = sc.nextInt();
        System.out.print(number);
    }
}
