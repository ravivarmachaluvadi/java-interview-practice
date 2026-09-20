/*
 * =====================================================================
 *  CompletableFuture exception handling          Java Core | Concurrency | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   How a failure inside an async task travels back to the caller, and the three
 *   recovery hooks: exceptionally (fallback value), handle (see both outcomes),
 *   and the bare join() that rethrows. Also what join() actually throws, which is
 *   not the exception you threw.
 *
 * WHAT YOU WILL SEE
 *   supplyAsync fails  -> exceptionally supplies 0            (case 1)
 *   supplyAsync works  -> exceptionally is never called       (case 2)
 *   join with no handler rethrows CompletionException wrapping RuntimeException
 *   handle() runs on both the success and the failure path    (cases 4 and 5)
 *
 * HOW IT WORKS
 *   1. supplyAsync runs the Supplier on the common ForkJoinPool. If it throws, the
 *      future completes exceptionally instead of completing with a value.
 *   2. exceptionally(fn) adds a stage that runs ONLY on the failure path; its return
 *      value becomes the value of the new future, so the chain is healthy again.
 *   3. handle(fn) adds a stage that runs on BOTH paths: (value, throwable) with one
 *      of the two always null.
 *   4. join() blocks until the stage settles. On failure it throws the unchecked
 *      CompletionException; the exception you actually threw is getCause().
 *
 * KEY INSIGHT
 *   An async failure is a completion state, not a thrown exception at the call site.
 *   Nothing is thrown until you ask for the value, and what is thrown is a wrapper:
 *   join -> CompletionException, get -> ExecutionException. Always unwrap getCause()
 *   before matching on the exception type.
 *
 * GOTCHAS
 *   - ex.getMessage() on the wrapper prints "java.lang.RuntimeException: ..." because
 *     the wrapper's own message is the cause's toString(). Use getCause().
 *   - exceptionally cannot see the success value, so it cannot "fix up" a good result.
 *   - The common pool uses daemon threads: a JVM exit will not wait for them.
 *   - Blocking work on the common pool starves every other user of it; pass your own
 *     Executor to supplyAsync for IO-bound tasks.
 *
 * INTERVIEW FOLLOW-UPS
 *   - exceptionally vs handle vs whenComplete: which can change the value?
 *   - join() vs get(): checked or unchecked, and which wrapper does each throw?
 *   - How do you add a timeout? (orTimeout / completeOnTimeout, Java 9+)
 *   - thenApply vs thenCompose when the mapper itself returns a CompletableFuture.
 *
 * RUN
 *   main() runs 5 cases (failure, success, unhandled, handle-on-both) and prints
 *   actual vs expected on each line.
 */
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

class CompletableFutureExceptionHandling {

    /** The async unit of work: succeeds with 10, or blows up, depending on the flag. */
    static CompletableFuture<Integer> loadValue(boolean fail) {
        return CompletableFuture.supplyAsync(() -> {
            if (fail) {
                throw new RuntimeException("Something went wrong!");
            }
            return 10;
        });
    }

    /** Failure path only: exceptionally swaps the failure for a fallback value. */
    static int withFallback(boolean fail) {
        return loadValue(fail)
                .exceptionally(ex -> {
                    // ex is a CompletionException; the thrown RuntimeException is its cause.
                    System.out.println("   recovered from: " + ex.getCause().getMessage());
                    return 0;
                })
                .join();
    }

    /** No handler at all: join() rethrows, so we report the wrapper and its cause. */
    static String joinUnhandled() {
        try {
            loadValue(true).join();
            return "no exception";
        } catch (CompletionException ex) {
            return ex.getClass().getSimpleName() + " -> "
                    + ex.getCause().getClass().getSimpleName();
        }
    }

    /** Both paths: exactly one of (value, throwable) is non-null. */
    static int withHandle(boolean fail) {
        return loadValue(fail)
                .handle((value, throwable) -> throwable == null ? value * 2 : -1)
                .join();
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        System.out.println("case 1 (task throws, exceptionally supplies a fallback):");
        print("case 1", withFallback(true), 0);

        System.out.println("case 2 (task succeeds, exceptionally never runs):");
        print("case 2", withFallback(false), 10);

        print("case 3", joinUnhandled(), "CompletionException -> RuntimeException");
        print("case 4", withHandle(false), 20);
        print("case 5", withHandle(true), -1);
    }
}
