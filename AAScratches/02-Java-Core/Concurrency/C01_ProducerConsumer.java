/*
 * =====================================================================
 *  Producer-Consumer on a shared queue        Java Core | Concurrency | Medium
 * =====================================================================
 *
 * PROBLEM
 *   One producer pushes items into a queue shared with one consumer. The consumer must
 *   never poll an empty queue and the producer must never exceed the buffer capacity.
 *   Coordinate them with one monitor: synchronized + wait() + notifyAll(). Both threads
 *   must finish after exactly ITEMS items, with items consumed in FIFO order.
 *
 * TWO VERSIONS, RUN SIDE BY SIDE
 *   1. NaiveIfGuard      - guards wait() with `if`, has no explicit capacity, and sleeps
 *                          while holding the monitor. This is WHAT NOT TO DO.
 *   2. CorrectWhileGuard - guards wait() with `while`, bounds the queue with CAPACITY,
 *                          and moves the sleep AFTER the add/poll + notifyAll. This is
 *                          the answer to give for bugs 1 and 2. It still holds the
 *                          monitor while sleeping, because the methods themselves are
 *                          synchronized - the real fix for that is to synchronize only
 *                          the queue operations, or to use ArrayBlockingQueue.
 *
 * APPROACH  (monitor + condition loop)
 *   1. Every read or write of the queue happens while holding the same monitor (`this`).
 *   2. Producer: while (queue.size() == CAPACITY) wait();  then add, then notifyAll().
 *   3. Consumer: while (queue.isEmpty())        wait();  then poll, then notifyAll().
 *   4. Each thread has its own counter (val, consumed) so both loops terminate.
 *
 * KEY INSIGHT
 *   wait() must always sit inside a `while` that re-tests the condition, never an `if`.
 *   Waking up is not a promise that the condition now holds: a wakeup can be spurious,
 *   and with several waiters notifyAll wakes everyone though only one can proceed. With
 *   `if`, the woken thread acts on a stale assumption - adds to a full queue, or polls an
 *   empty one and gets null. "wait in a loop, signal after the state change" is the whole
 *   monitor pattern.
 *
 * COMPLEXITY
 *   Time  O(n)          n items, each enqueue/dequeue is O(1); the sleeps are demo pacing
 *   Space O(CAPACITY)   the bounded buffer, an implicit 1-slot one in the naive
 *                       version (the consumed list is test-only, O(n))
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why notifyAll() and not notify()? (with mixed waiters, notify can wake the wrong
 *     kind of thread and the queue stalls with work available)
 *   - Rewrite with ReentrantLock and two Conditions: now notify exactly the right side.
 *   - Rewrite with ArrayBlockingQueue.put/take: how much of this disappears?
 *   - How do you shut it down cleanly with N producers and M consumers? (poison pills)
 *
 * RUN
 *   main() runs both versions and prints the consumed sequence vs the expected sequence,
 *   plus an edge case with ITEMS = 1. Finishes in under 2 seconds.
 */
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class ProducerConsumer {

    /** Pacing only, so the interleaving is visible in the output. */
    static final int SLEEP_MS = 20;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Approach 1: naive `if` guard (what NOT to do), 5 items ===");
        print("case 1", new NaiveIfGuard(5).run(), "[0, 1, 2, 3, 4]");

        System.out.println();
        System.out.println("=== Approach 2: correct `while` guard, CAPACITY=2, 5 items ===");
        print("case 2", new CorrectWhileGuard(5).run(), "[0, 1, 2, 3, 4]");

        System.out.println();
        System.out.println("=== Edge case: a single item, so one side always waits first ===");
        print("case 3", new CorrectWhileGuard(1).run(), "[0]");
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    // ---------------------------------------------------------------------
    // Approach 1: naive version. Happens to work for exactly one producer and
    // one consumer, but has three interview-relevant mistakes (marked BUG).
    // ---------------------------------------------------------------------
    static class NaiveIfGuard {
        final Queue<Integer> queue = new LinkedList<>();
        final List<Integer> consumedItems = new ArrayList<>();
        /** Bounded so the demo terminates; the original version looped forever. */
        final int items;
        int val = 0;
        int consumed = 0;

        NaiveIfGuard(int items) {
            this.items = items;
        }

        List<Integer> run() throws InterruptedException {
            Thread producer = new Thread(this::producer, "naive-producer");
            Thread consumer = new Thread(this::consumer, "naive-consumer");
            producer.start();
            consumer.start();
            producer.join();
            consumer.join();
            return consumedItems;           // safe to read: both threads have finished
        }

        synchronized void producer() {
            while (val < items) {
                if (queue.isEmpty()) {      // BUG 1: `if` -> not re-tested after a wakeup
                    int e = val++;          // BUG 2: no CAPACITY; "isEmpty" makes it 1 slot
                    System.out.println("  Produced : " + e);
                    sleep();                // BUG 3: sleeps BEFORE the item is even
                    queue.add(e);           //        visible, still inside the monitor
                    notifyAll();
                } else {
                    await();
                }
            }
        }

        synchronized void consumer() {
            while (consumed < items) {
                if (!queue.isEmpty()) {     // BUG 1 again
                    int item = queue.poll();
                    consumedItems.add(item);
                    consumed++;
                    System.out.println("  Consumed : " + item);
                    sleep();                // BUG 3 again: sleeps before notifyAll()
                    notifyAll();
                } else {
                    await();
                }
            }
        }

        private void await() {
            try {
                wait();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    // ---------------------------------------------------------------------
    // Approach 2: corrected version. Fixes all three bugs above.
    // ---------------------------------------------------------------------
    static class CorrectWhileGuard {
        static final int CAPACITY = 2;

        final Queue<Integer> queue = new LinkedList<>();
        final List<Integer> consumedItems = new ArrayList<>();
        /** Bounded so the demo terminates; the original version looped forever. */
        final int items;
        int val = 0;
        int consumed = 0;

        CorrectWhileGuard(int items) {
            this.items = items;
        }

        List<Integer> run() throws InterruptedException {
            Thread producer = new Thread(this::producer, "correct-producer");
            Thread consumer = new Thread(this::consumer, "correct-consumer");
            producer.start();
            consumer.start();
            producer.join();
            consumer.join();
            return consumedItems;
        }

        synchronized void producer() {
            while (val < items) {
                // FIX 1 + 2: `while` guard on an explicit bound; re-tested after every wakeup.
                while (queue.size() == CAPACITY) {
                    await();
                }
                int e = val++;
                queue.add(e);
                System.out.println("  Produced : " + e);
                notifyAll();
                sleep();                    // FIX 3: pace AFTER the state change + notifyAll
                                            // (still inside the monitor - see the NOTE below)
            }
        }

        synchronized void consumer() {
            while (consumed < items) {
                while (queue.isEmpty()) {   // wait if empty; re-tested on every wakeup
                    await();
                }
                int item = queue.poll();
                consumedItems.add(item);
                consumed++;
                System.out.println("  Consumed : " + item);
                notifyAll();
                sleep();
            }
        }

        private void await() {
            try {
                wait();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    // NOTE: even the corrected version still sleeps while holding `this`, because the whole
    // method is synchronized. Production code would synchronize only the queue operations,
    // or use ArrayBlockingQueue.put/take, which does all of this for you.

    /** Pacing only, so the interleaving is visible in the output. */
    static void sleep() {
        try {
            Thread.sleep(SLEEP_MS);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
}
