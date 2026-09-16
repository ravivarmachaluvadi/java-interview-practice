/**
 * Demonstrates how to handle exceptions that occur during asynchronous computation with CompletableFuture.
 *
 * The program launches a task that deliberately throws a RuntimeException. It then attaches an
 * exceptionally handler that logs the exception and supplies a fallback value (0). Finally, it waits
 * for completion using join() and prints the result.
 *
 * Approach:
 * 1. Create a CompletableFuture with supplyAsync to run code in another thread.
 * 2. Attach exceptionally to catch any thrown exception and provide a default value.
 * 3. Use join() to block until the future completes, then print the outcome.
 *
 * Time Complexity: O(1) – each operation is constant time; the asynchronous task runs once.
 * Space Complexity: O(1) – only a few objects are created regardless of input size.
 */
import java.util.concurrent.CompletableFuture;
class CompletableFutureExceptionHandling {
    public static void main(String[] args) {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Performing a task...");
            if (true) {
                throw new RuntimeException("Something went wrong!");
            }
            return 10;
        });

        Integer result = future
                .exceptionally(ex -> {
                    System.out.println("Exception: " + ex.getMessage());
                    return 0;  // Fallback value in case of exception
                })
                .join();  // Wait for completion

        System.out.println("Result: " + result);
    }
}
