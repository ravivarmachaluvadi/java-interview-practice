import java.util.LinkedList;
import java.util.Queue;

/**
 * Problem: Producer-consumer on a shared queue using one monitor (synchronized / wait / notifyAll).
 * Approaches:
 *   1. naiveIfGuard      - guards wait() with `if`, implicit 1-slot buffer, sleeps while holding the lock. WHAT NOT TO DO.
 *   2. correctWhileGuard - guards wait() with `while`, explicit CAPACITY bound, sleeps after notifyAll. The interview answer.
 *
 * Why the `while` guard matters (from the corrected version):
 *   - Spurious wakeups: a thread can return from wait() even if nobody called notify.
 *   - Multiple producers/consumers: notifyAll wakes everyone, but the condition may only hold for one of them.
 *   With `if`, a woken thread acts without re-checking -> add to a full queue / poll from an empty one.
 *
 * Time Complexity: O(n) for n produced/consumed items (each queue op is O(1)).
 * Space Complexity: O(CAPACITY) for the bounded buffer; O(1) for the naive 1-slot version.
 */
class ProducerConsumer {

    /** Items each demo produces and consumes, so the run terminates (the originals looped forever). */
    static final int ITEMS = 5;
    static final int SLEEP_MS = 50;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Approach 1: naive `if` guard (what NOT to do) ===");
        new NaiveIfGuard().run();

        System.out.println();
        System.out.println("=== Approach 2: correct `while` guard, CAPACITY=" + CorrectWhileGuard.CAPACITY + " ===");
        new CorrectWhileGuard().run();
    }

    // ---------------------------------------------------------------------
    // Approach 1: naive version. Works for exactly one producer + one consumer
    // by luck, but has three interview-relevant mistakes (marked BUG below).
    // ---------------------------------------------------------------------
    static class NaiveIfGuard {
        Queue<Integer> queue = new LinkedList<>();
        int val = 0;
        int consumed = 0;

        void run() throws InterruptedException {
            Thread producer = new Thread(this::producer, "naive-producer");
            Thread consumer = new Thread(this::consumer, "naive-consumer");
            producer.start();
            consumer.start();
            producer.join();
            consumer.join();
        }

        synchronized void producer() {
            while (val < ITEMS) {
                if (queue.isEmpty()) {          // BUG 1: `if` -> not re-checked after a spurious/irrelevant wakeup
                    int e = val++;              // BUG 2: no CAPACITY; "isEmpty" makes it an implicit 1-slot buffer
                    System.out.println("Produced : " + e);
                    sleep();                    // BUG 3: sleeping INSIDE the monitor blocks the consumer for no reason
                    queue.add(e);
                    notifyAll();
                } else {
                    try {
                        wait();                 // original comment: "wait also should be inside while loop"
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }

        synchronized void consumer() {
            while (consumed < ITEMS) {
                if (!queue.isEmpty()) {         // BUG 1 again
                    System.out.println("Consumed : " + queue.poll());
                    consumed++;
                    sleep();                    // BUG 3 again
                    notifyAll();
                } else {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }

    // ---------------------------------------------------------------------
    // Approach 2: corrected version. Fixes all three bugs above.
    // ---------------------------------------------------------------------
    static class CorrectWhileGuard {
        static final int CAPACITY = 5;

        Queue<Integer> queue = new LinkedList<>();
        int val = 0;
        int consumed = 0;

        void run() throws InterruptedException {
            Thread producer = new Thread(this::producer, "correct-producer");
            Thread consumer = new Thread(this::consumer, "correct-consumer");
            producer.start();
            consumer.start();
            producer.join();
            consumer.join();
        }

        synchronized void producer() {
            while (val < ITEMS) {
                while (queue.size() == CAPACITY) { // FIX 1+2: `while` guard on an explicit bound; re-check after every wakeup
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                int e = val++;
                queue.add(e);
                System.out.println("Produced : " + e);
                notifyAll();
                sleep();                            // FIX 3: sleep AFTER the state change + notifyAll, not before
            }
        }

        synchronized void consumer() {
            while (consumed < ITEMS) {
                while (queue.isEmpty()) {           // wait if empty; re-check on wakeup
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                int item = queue.poll();
                consumed++;
                System.out.println("Consumed : " + item);
                notifyAll();
                sleep();
            }
        }
    }

    // NOTE: even the corrected version still sleeps while holding `this` because the whole method is
    // synchronized. Production code would use a synchronized block around only the queue ops, or a
    // BlockingQueue (ArrayBlockingQueue.put/take) which does all of this for you.

    static void sleep() {
        try {
            Thread.sleep(SLEEP_MS);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
