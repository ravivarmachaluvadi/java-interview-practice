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
