/**
 * Demonstrates the use of a {@link java.util.concurrent.CountDownLatch} to coordinate
 * two worker threads that add elements to a {@link java.util.concurrent.PriorityBlockingQueue}.
 * The first thread inserts 5 immediately, while the second sleeps for 1.5 seconds before inserting 10.
 * After both insertions have completed (or after a timeout), the main thread prints all queue elements in
 * descending order followed by "Hello world!".
 *
 * Approach:
 * - Create a priority queue with reverse ordering to store integers.
 * - Use a CountDownLatch initialized to 2 so the main thread waits for both workers.
 * - Each worker adds its element and decrements the latch; one worker sleeps first.
 * - Main thread awaits latch release (with timeout) then iterates over the queue.
 *
 * Time Complexity: O(n log n) for inserting n elements into the priority queue.
 * Space Complexity: O(n) to store the queued integers. */
import java.util.Comparator;
import java.util.concurrent.*;

class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        PriorityBlockingQueue<Integer> priorityBlockingQueue =
                new PriorityBlockingQueue<>(5, Comparator.reverseOrder());
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.execute(() -> {
            priorityBlockingQueue.add(5);
            latch.countDown();
        });
        executor.execute(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            priorityBlockingQueue.add(10);
            latch.countDown();
        });
//        latch.await(1, TimeUnit.SECONDS);
        latch.await(2, TimeUnit.SECONDS);
        priorityBlockingQueue.stream().iterator().forEachRemaining(System.out::println);
        System.out.println("Hello world!");

    }
}