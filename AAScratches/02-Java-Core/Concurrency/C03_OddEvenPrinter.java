/*
 * =====================================================================
 *  Odd / Even printer: two threads, strict alternation   Java Core | Concurrency | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Print 0, 1, 2, ... up to limit-1 using two threads: one may only print even numbers,
 *   the other only odd numbers. The output must be in strict ascending order, both
 *   threads must exit when the limit is reached, and neither may be left waiting forever.
 *
 * EXAMPLE
 *   limit = 6  ->  Even 0, Odd 1, Even 2, Odd 3, Even 4, Odd 5
 *   limit = 1  ->  Even 0                       (odd thread never gets a turn)
 *   limit = 0  ->  (nothing printed, both threads exit immediately)
 *
 * APPROACH  (shared monitor + turn predicate)
 *   1. One shared counter (Value) and one shared monitor object guard everything.
 *   2. Each thread loops: while it is not my turn AND we are not done -> lock.wait().
 *   3. On waking, re-test. If the counter already reached the limit, break out.
 *   4. Otherwise print, increment the counter, and lock.notifyAll() to hand over the turn.
 *   5. On the way out, notifyAll() once more so the other thread cannot stay parked.
 *
 * KEY INSIGHT
 *   The turn is not a variable you flip, it is a predicate over shared state:
 *   value % 2 == myParity. Wait on that predicate in a while loop, and make the loop
 *   condition include the termination test too - "not my turn" and "we are finished"
 *   are both reasons to stop waiting, and a thread that only tests its turn will either
 *   print one past the limit or park forever after its partner exits.
 *
 * Fixed: the original guarded wait() with `if`, so after the odd thread printed 99 and
 *        exited, the even thread woke up without re-testing and printed 100 - one past
 *        the limit. Fixed: it also used a ReentrantLock purely as a synchronized(...)
 *        monitor and never called lock.lock(), which is misleading; the monitor is now a
 *        plain Object. notify() was also replaced by notifyAll().
 *
 * COMPLEXITY
 *   Time  O(n)   one print and one handover per number; n = limit
 *   Space O(1)   one counter and one monitor, regardless of limit (the log is test-only)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Generalise to N threads printing i, i+1, ... in turn (predicate becomes value % N).
 *   - Redo it with ReentrantLock and two Conditions so you signal only the right thread.
 *   - Redo it with a Semaphore pair - which is simpler, and why?
 *   - Why notifyAll() instead of notify() even though there are only two threads here?
 *
 * RUN
 *   main() runs 3 cases (limit 6, limit 1, limit 0) and prints the actual printed
 *   sequence against the expected sequence.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Scratch {

    public static void main(String[] args) throws InterruptedException {
        print("case 1", printUpTo(6), "[Even 0, Odd 1, Even 2, Odd 3, Even 4, Odd 5]");
        print("case 2", printUpTo(1), "[Even 0]");
        print("case 3", printUpTo(0), "[]");
    }

    /** Starts both printers, waits for them, and returns what was printed, in order. */
    static List<String> printUpTo(int limit) throws InterruptedException {
        Value value = new Value();
        Object lock = new Object();
        List<String> log = Collections.synchronizedList(new ArrayList<>());

        Thread evenThread = new Thread(new EvenPrinter(value, lock, limit, log)::run, "even");
        Thread oddThread = new Thread(new OddPrinter(value, lock, limit, log)::run, "odd");
        evenThread.start();
        oddThread.start();
        evenThread.join(2000);      // bounded join: a regression would time out, not hang
        oddThread.join(2000);
        return log;
    }

    /**
     * The shared turn-taking loop. myParity is 0 for the even thread, 1 for the odd one.
     * Both printers call this, so the wait/notify rule lives in exactly one place.
     */
    static void takeTurns(Value value, Object lock, int limit, int myParity,
                          String label, List<String> log) {
        synchronized (lock) {
            while (true) {
                // Stop waiting either when it is my turn OR when there is nothing left to do.
                while (value.value < limit && value.value % 2 != myParity) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (value.value >= limit) {
                    break;              // re-tested after the wakeup: never print past the limit
                }
                String line = label + " " + value.value;
                System.out.println("  " + line);
                log.add(line);
                value.value++;
                lock.notifyAll();       // hand the turn over
            }
            lock.notifyAll();           // on exit: release a partner that is still parked
        }
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

/** Shared counter. A plain int field so both threads see the same mutable cell. */
class Value {
    int value = 0;
}

class EvenPrinter {
    final Value value;
    final Object lock;
    final int limit;
    final List<String> log;

    EvenPrinter(Value value, Object lock, int limit, List<String> log) {
        this.value = value;
        this.lock = lock;
        this.limit = limit;
        this.log = log;
    }

    public void run() {
        Scratch.takeTurns(value, lock, limit, 0, "Even", log);
    }
}

class OddPrinter {
    final Value value;
    final Object lock;
    final int limit;
    final List<String> log;

    OddPrinter(Value value, Object lock, int limit, List<String> log) {
        this.value = value;
        this.lock = lock;
        this.limit = limit;
        this.log = log;
    }

    public void run() {
        Scratch.takeTurns(value, lock, limit, 1, "Odd", log);
    }
}
