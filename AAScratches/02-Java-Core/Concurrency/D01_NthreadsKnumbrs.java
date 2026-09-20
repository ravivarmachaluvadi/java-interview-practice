/*
 * =====================================================================
 *  Print 1..K in order using N threads               Concurrency | Hard
 * =====================================================================
 *
 * PROBLEM
 *   N threads share one counter. Thread i may only print a number v when v % N == i. Every
 *   number from 1 to K must be printed exactly once in ascending order, and every thread
 *   must exit cleanly once the counter passes K. This is the general form of the classic
 *   odd/even printer, which is just N = 2.
 *
 * EXAMPLE
 *   N = 4, K = 8  ->  1:T1 2:T2 3:T3 4:T0 5:T1 6:T2 7:T3 8:T0
 *   N = 1, K = 3  ->  1:T0 2:T0 3:T0      (one thread does everything, never waits)
 *   N = 3, K = 1  ->  1:T1                (two threads never get a turn at all)
 *
 * APPROACH  (turn predicate + wait / notifyAll on one monitor)
 *   1. All threads synchronize on the same ValueObject, so exactly one is awake in the
 *      critical section at a time and `value` needs no volatile or atomic.
 *   2. Loop while value <= limit. This is the LOOP CONDITION, not an if: a woken thread
 *      must re-check it, because notifyAll wakes everyone including threads with no turn.
 *   3. If value % n == myRemainder it is my turn: record it, increment, notifyAll.
 *   4. Otherwise wait(). wait() releases the monitor, which is what lets the thread whose
 *      turn it actually is get in. sleep() would hold the lock and deadlock all N threads.
 *
 * KEY INSIGHT
 *   Two rules make every "ordered handoff between threads" problem work, and they are both
 *   about the WAKE-UP, not the sleep:
 *     - notifyAll, not notify. With N > 2, notify may wake a thread whose turn it is not;
 *       that thread waits again and nobody is left to make progress - a lost-wakeup hang.
 *     - The last increment calls notifyAll BEFORE re-testing the loop condition, so the
 *       N-1 threads still parked wake up, see value > limit, and exit instead of leaking.
 *   Recognise the shape: shared predicate + while(!myTurn) wait() + notifyAll after a change.
 *
 * COMPLEXITY
 *   Time  O(K * N)  each number costs up to N-1 wasted wake-ups before the right thread
 *                   gets the monitor; the printing itself is O(K)
 *   Space O(N + K)  N threads, plus the recorded log used to assert the output order
 *
 * INTERVIEW FOLLOW-UPS
 *   - Rewrite with ReentrantLock and N Conditions: now signal() is enough. Why? (each
 *     thread parks on its own condition queue, so you wake exactly the right one)
 *   - Why is Semaphore[] the cleanest version? (release the next thread's permit directly)
 *   - What breaks if `while` becomes `if`? (a spurious or mistimed wakeup prints out of turn)
 *   - Does this scale? (no - one global monitor serialises everything: N threads, zero
 *     parallelism, which is the honest answer to "is this a good design?")
 *
 * RUN
 *   main() runs 3 cases (N=4/K=8, N=1/K=3, N=3/K=1) and prints the recorded order against
 *   the expected order. The original printed 1..100 and asserted nothing; the locking logic
 *   is unchanged, only the output is now checkable.
 */
import java.util.ArrayList;
import java.util.List;

class NthreadsKnumbrs {

    public static void main(String[] args) throws InterruptedException {
        print("case 1 (N=4, K=8)", run(4, 8), "1:T1 2:T2 3:T3 4:T0 5:T1 6:T2 7:T3 8:T0");
        print("case 2 (N=1, K=3)", run(1, 3), "1:T0 2:T0 3:T0");
        print("case 3 (N=3, K=1)", run(3, 1), "1:T1");
    }

    /** Starts n threads that together print 1..limit in order, and returns what they printed. */
    static String run(int n, int limit) throws InterruptedException {
        ValueObject shared = new ValueObject(limit);

        List<Thread> threads = new ArrayList<>();
        for (int remainder = 0; remainder < n; remainder++) {
            Thread t = new Thread(new Printer(shared, n, remainder), "T" + remainder);
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) {
            t.join(2000);                 // bounded join: a bug must fail the run, not hang it
        }
        return String.join(" ", shared.log);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

/** The single monitor every printer locks on, plus the state it guards. */
class ValueObject {
    final int limit;
    int value = 1;                        // guarded by "this"
    final List<String> log = new ArrayList<>();   // guarded by "this"

    ValueObject(int limit) {
        this.limit = limit;
    }
}

class Printer implements Runnable {

    private final ValueObject obj;
    private final int n;                  // how many threads share the counter
    private final int remainder;          // this thread prints v only when v % n == remainder

    Printer(ValueObject obj, int n, int remainder) {
        this.obj = obj;
        this.n = n;
        this.remainder = remainder;
    }

    @Override
    public void run() {
        synchronized (obj) {
            while (obj.value <= obj.limit) {
                if (obj.value % n == remainder) {
                    obj.log.add(obj.value + ":T" + remainder);
                    obj.value++;
                    // Wake everyone: only one of them has the next turn, and on the final
                    // increment this is what releases the parked threads so they can exit.
                    obj.notifyAll();
                } else {
                    try {
                        obj.wait();       // releases the monitor; re-tests the while on wake
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }
}
