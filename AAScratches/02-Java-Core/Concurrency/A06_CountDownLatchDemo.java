/*
 * =====================================================================
 *  CountDownLatch + PriorityBlockingQueue            Java Core | Concurrency
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   Two primitives that show up in almost every fan-out question:
 *     - CountDownLatch: "wait here until N workers have finished", with a timeout so the
 *       waiter can never be stuck forever.
 *     - PriorityBlockingQueue: a thread-safe unbounded priority queue - safe to add to from
 *       several threads, and it hands elements back in comparator order when you POLL it.
 *   The demo also shows the classic trap: iterating a priority queue does NOT give you
 *   sorted order.
 *
 * WHAT YOU WILL SEE
 *   case 1: both workers count down, await returns true, the queue drains as [10, 5]
 *   case 2: only one of two workers counts down, so await(150 ms) returns false
 *   case 3: the same queue printed by iteration and by draining - the two differ
 *
 * HOW IT WORKS
 *   1. A latch is created with count 2. Each worker adds its number and calls countDown()
 *      in a finally block, so a crash in the worker can never hang the waiter.
 *   2. main calls latch.await(1, SECONDS), which blocks until the count hits 0 or the
 *      timeout expires, and returns true only in the first case.
 *   3. Draining with poll() in a loop is what produces descending order, because the
 *      comparator is Comparator.reverseOrder().
 *   4. Fixed two bugs in the original: the executor was never shut down, so its non-daemon
 *      threads kept the JVM running forever; and the queue was printed by iterating it,
 *      which happens to look sorted for two elements and is wrong for five.
 *
 * KEY INSIGHT
 *   A latch counts DOWN once and can never be reset - it is a one-shot gate, which is
 *   exactly why it is the right tool for "wait for startup / wait for N results" and the
 *   wrong tool for a repeating rendezvous (that is CyclicBarrier or Phaser). Always use the
 *   timed await and check its boolean: an untimed await is a deadlock waiting for a bug.
 *
 * GOTCHAS
 *   - countDown() belongs in a finally block, or one thrown exception hangs the waiter.
 *   - await() returning false means TIMED OUT; ignoring the return value hides that.
 *   - A heap is only partially ordered: peek() is the smallest by the comparator, but the
 *     iterator (and therefore toString) walks the backing array in heap order.
 *   - PriorityBlockingQueue is unbounded, so put() never blocks and a fast producer can
 *     exhaust the heap.
 *
 * INTERVIEW FOLLOW-UPS
 *   - CountDownLatch vs CyclicBarrier vs Phaser vs Semaphore - one line each.
 *   - How would you write case 1 with an ExecutorService and invokeAll / CompletableFuture?
 *   - Which BlockingQueue would you pick for a bounded producer-consumer, and why?
 *   - What happens if a worker throws before countDown, and how do you surface that error?
 *
 * RUN
 *   main() runs the three cases and prints actual vs expected. It finishes in under a second.
 */
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
        waitForBothWorkers();
        awaitTimesOutWhenAWorkerNeverArrives();
        iterationOrderIsNotSortedOrder();
    }

    /** Case 1: the normal path - two workers, latch reaches zero, await returns true. */
    private static void waitForBothWorkers() throws InterruptedException {
        PriorityBlockingQueue<Integer> queue =
                new PriorityBlockingQueue<>(5, Comparator.reverseOrder());
        CountDownLatch done = new CountDownLatch(2);
        ExecutorService pool = Executors.newFixedThreadPool(2);

        pool.execute(() -> addThenCountDown(queue, 5, 0, done));
        pool.execute(() -> addThenCountDown(queue, 10, 300, done)); // the slow worker

        boolean released = done.await(1, TimeUnit.SECONDS);
        System.out.println("case 1a: await returned " + released
                + "   expected true (both workers counted down inside the timeout)");
        System.out.println("case 1b: queue drained " + drain(queue)
                + "   expected [10, 5]");

        shutdown(pool);
    }

    /** Case 2: one worker never counts down, so the timed await gives up and returns false. */
    private static void awaitTimesOutWhenAWorkerNeverArrives() throws InterruptedException {
        CountDownLatch done = new CountDownLatch(2);
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(done::countDown); // only one of the two expected count downs ever happens

        boolean released = done.await(150, TimeUnit.MILLISECONDS);
        System.out.println("case 2a: await returned " + released
                + "   expected false (count is still 1, so it timed out)");
        System.out.println("case 2b: remaining count " + done.getCount()
                + "   expected 1");

        // A latch cannot be reset; counting the last one down here releases it for good.
        done.countDown();
        boolean releasedNow = done.await(1, TimeUnit.SECONDS);
        System.out.println("case 2c: await after the last countDown " + releasedNow
                + "   expected true");

        shutdown(pool);
    }

    /** Case 3: a heap is only partially ordered - iterating it is not the same as draining it. */
    private static void iterationOrderIsNotSortedOrder() {
        PriorityBlockingQueue<Integer> queue =
                new PriorityBlockingQueue<>(5, Comparator.reverseOrder());
        for (int value : new int[] { 7, 10, 9, 1, 5 }) {
            queue.add(value);
        }
        System.out.println("case 3a: iteration order " + queue
                + "   expected [10, 7, 9, 1, 5] - raw heap array, NOT sorted");
        System.out.println("case 3b: drain order    " + drain(queue)
                + "   expected [10, 9, 7, 5, 1] - descending, because poll() reheapifies");
    }

    /** Adds one value after an optional delay, and always counts down, even on failure. */
    private static void addThenCountDown(PriorityBlockingQueue<Integer> queue, int value,
                                         long delayMillis, CountDownLatch latch) {
        try {
            if (delayMillis > 0) {
                Thread.sleep(delayMillis);
            }
            queue.add(value);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore the flag, then fall through
        } finally {
            latch.countDown(); // in finally: a failed worker must not hang the waiter
        }
    }

    /** Removes every element in comparator order. This, not iteration, is what sorts. */
    private static List<Integer> drain(PriorityBlockingQueue<Integer> queue) {
        List<Integer> inOrder = new ArrayList<>();
        for (Integer head = queue.poll(); head != null; head = queue.poll()) {
            inOrder.add(head);
        }
        return inOrder;
    }

    private static void shutdown(ExecutorService pool) throws InterruptedException {
        pool.shutdown();
        if (!pool.awaitTermination(1, TimeUnit.SECONDS)) {
            pool.shutdownNow(); // without this the non-daemon pool threads keep the JVM alive
        }
    }
}
