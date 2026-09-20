/*
 * =====================================================================
 *  Writer / Reader handoff with two Conditions  Java Core | Concurrency | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   One writer and one reader share a single slot. The writer puts 1, 2, ... n into the
 *   slot; the reader takes each value out exactly once and empties the slot again. The
 *   reader must never read an empty slot, the writer must never overwrite an unread
 *   value, and both threads must exit after n handoffs.
 *
 * EXAMPLE
 *   n = 5  ->  reader receives [1, 2, 3, 4, 5], strictly alternating with the writer
 *   n = 1  ->  reader receives [1]
 *   n = 0  ->  nothing written, nothing read, both threads exit at once
 *
 * APPROACH  (ReentrantLock + two Conditions)
 *   1. Slot state: value == 0 means empty, anything else means full and unread.
 *   2. One ReentrantLock guards the slot. Two Conditions hang off that lock:
 *        slotFilled  - the reader waits here, the writer signals it
 *        slotEmptied - the writer waits here, the reader signals it
 *   3. Writer, n times: lock; while (slot is full) slotEmptied.await(); write; signal
 *      slotFilled; unlock in a finally.
 *   4. Reader, n times: lock; while (slot is empty) slotFilled.await(); take; set slot to
 *      0; signal slotEmptied; unlock in a finally.
 *   5. Both loops are bounded by n, so termination does not depend on the shared state.
 *
 * KEY INSIGHT
 *   One lock can own several wait queues. With synchronized you get exactly one queue,
 *   so notifyAll() wakes readers and writers alike and most of them go straight back to
 *   waiting. Two Conditions let you wake exactly the side that can make progress, which
 *   is what makes a real bounded buffer (and N-way sequencing) efficient. The rules stay
 *   the same as with wait/notify: await() only inside a `while` re-testing the predicate,
 *   and unlock() only in a finally.
 *
 * Fixed: three real bugs in the original.
 *   1. await() was guarded by `if`, not `while` - no re-test after waking.
 *   2. Termination was read from the shared value (`while (value.value < 100)`), but the
 *      reader resets it to 0 after every read, so the reader's test never became false.
 *      The writer exited at 100 and the reader waited forever: the program hung.
 *   3. unlock() was called on the normal path only; an exception inside the critical
 *      section leaked the lock. It is now in a finally.
 *   The unused AtomicInteger was also dropped - the writer already holds the lock, so a
 *   plain loop counter is enough.
 *
 * COMPLEXITY
 *   Time  O(n)   one lock/handoff/signal pair per item
 *   Space O(1)   a single slot plus two condition queues (the result list is test-only)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Grow the slot into a bounded buffer of size k: what changes? (the predicates only)
 *   - signal() vs signalAll(): when is signal() safe? (one waiter per condition, and every
 *     waiter on that condition can make progress)
 *   - What does a `new ReentrantLock(true)` fair lock buy you, and what does it cost?
 *   - await() vs awaitUninterruptibly(), and how do you add a timeout to the handoff?
 *   - When would you just use SynchronousQueue or ArrayBlockingQueue instead?
 *
 * RUN
 *   main() runs 3 cases (5 items, 1 item, 0 items) and prints the values the reader
 *   actually received against the expected list. Finishes in well under 2 seconds.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class WriterReader {

    public static void main(String[] args) throws InterruptedException {
        print("case 1", handoff(5), "[1, 2, 3, 4, 5]");
        print("case 2", handoff(1), "[1]");
        print("case 3", handoff(0), "[]");
    }

    /** Runs one writer and one reader over `items` handoffs and returns what was read. */
    static List<Integer> handoff(int items) throws InterruptedException {
        Value value = new Value();
        ReentrantLock lock = new ReentrantLock();
        Condition slotFilled = lock.newCondition();     // reader waits here
        Condition slotEmptied = lock.newCondition();    // writer waits here
        List<Integer> received = Collections.synchronizedList(new ArrayList<>());

        Reader reader = new Reader(value, lock, slotFilled, slotEmptied, items, received);
        Writer writer = new Writer(value, lock, slotFilled, slotEmptied, items);

        Thread readerThread = new Thread(reader::run, "reader");
        Thread writerThread = new Thread(writer::run, "writer");
        readerThread.start();
        writerThread.start();
        readerThread.join(2000);    // bounded join: a regression times out instead of hanging
        writerThread.join(2000);
        return received;
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

/** The single shared slot. 0 means empty; any other value means full and unread. */
class Value {
    int value = 0;
}

class Reader {
    final Value value;
    final ReentrantLock lock;
    final Condition slotFilled;
    final Condition slotEmptied;
    final int items;
    final List<Integer> received;

    Reader(Value value, ReentrantLock lock, Condition slotFilled, Condition slotEmptied,
           int items, List<Integer> received) {
        this.value = value;
        this.lock = lock;
        this.slotFilled = slotFilled;
        this.slotEmptied = slotEmptied;
        this.items = items;
        this.received = received;
    }

    public void run() {
        for (int i = 0; i < items; i++) {       // bounded by the contract, not by shared state
            lock.lock();
            try {
                while (value.value == 0) {      // while, not if: re-test after every wakeup
                    slotFilled.await();
                }
                int taken = value.value;
                received.add(taken);
                System.out.println("  Reader read   : " + taken);
                value.value = 0;                // slot is empty again
                slotEmptied.signal();           // wake the writer, and only the writer
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } finally {
                lock.unlock();                  // always, even if the body threw
            }
        }
    }
}

class Writer {
    final Value value;
    final ReentrantLock lock;
    final Condition slotFilled;
    final Condition slotEmptied;
    final int items;

    Writer(Value value, ReentrantLock lock, Condition slotFilled, Condition slotEmptied,
           int items) {
        this.value = value;
        this.lock = lock;
        this.slotFilled = slotFilled;
        this.slotEmptied = slotEmptied;
        this.items = items;
    }

    public void run() {
        for (int i = 1; i <= items; i++) {
            lock.lock();
            try {
                while (value.value != 0) {      // slot still holds an unread value
                    slotEmptied.await();
                }
                value.value = i;
                System.out.println("  Writer wrote  : " + i);
                slotFilled.signal();            // wake the reader, and only the reader
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } finally {
                lock.unlock();
            }
        }
    }
}
