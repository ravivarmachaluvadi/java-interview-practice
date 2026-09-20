/*
 * =====================================================================
 *  Two Threads, One Counter: atomic vs plain int       Java Core | Concurrency   MUST-KNOW
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   One shared counter, two threads: one does +1 a hundred thousand times, the other does
 *   -1 the same number of times. The increments cancel out, so a correct counter must end
 *   at 0. Three versions of the same counter are run: an AtomicInteger, a plain int with no
 *   protection, and a plain int guarded by a synchronized block.
 *
 * WHAT YOU WILL SEE
 *   AtomicInteger      -> 0      every single run
 *   plain int          -> some random number such as 3891 or -2170, almost never 0
 *   synchronized int   -> 0      every single run
 *
 * HOW IT WORKS
 *   1. runBothWays() starts two threads, one running the "up" operation, one the "down"
 *      operation, then join()s both so main only reads the counter after they have finished.
 *   2. value++ is not one step. It is read value, add one, write value back. Two threads can
 *      read the same old value, and one of the two updates is then silently thrown away.
 *      That lost update is why the plain int drifts away from 0.
 *   3. AtomicInteger.getAndIncrement() performs the read-modify-write as a single CPU
 *      compare-and-swap instruction, so no update can be lost.
 *   4. The synchronized block fixes it a different way: only one thread at a time may be
 *      inside the read-modify-write, so the steps cannot interleave.
 *
 * KEY INSIGHT
 *   "It is a single line of Java" says nothing about atomicity. Any read-modify-write on
 *   shared mutable state (++, --, +=, check-then-act) needs either a lock or an atomic type.
 *   volatile alone would NOT fix this: it gives visibility, not atomicity.
 *
 * GOTCHAS
 *   - The plain int can print 0 by luck. A passing run does not prove thread safety.
 *   - join() is what makes main's read safe; without it main may see a stale value.
 *   - Atomics win on a single field. For two fields that must change together, use a lock.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does volatile not fix counter++? What does volatile actually guarantee?
 *   - AtomicInteger vs LongAdder under high contention - which and why?
 *   - Show the same bug as check-then-act (lazy singleton, "if absent then put").
 *   - What is the happens-before edge that Thread.start() and Thread.join() give you?
 *
 * RUN
 *   main() runs the three counters and prints each final value against what it should be.
 */
import java.util.concurrent.atomic.AtomicInteger;

class TwoThreadsToSingleValue {

    private static final int ITERATIONS = 100_000;

    /** Shared, deliberately unprotected - this is the field that loses updates. */
    private static int plainCounter;

    /** Shared, but every update happens inside synchronized (LOCK). */
    private static int guardedCounter;

    private static final Object LOCK = new Object();

    public static void main(String[] args) throws InterruptedException {
        int atomicResult = runAtomicCounter();
        int plainResult = runPlainCounter();
        int guardedResult = runGuardedCounter();

        System.out.println("case 1 AtomicInteger   : " + atomicResult
                + "   expected 0 (compare-and-swap cannot lose an update)");
        System.out.println("case 2 plain int       : " + plainResult
                + "   expected: almost never 0 - lost updates from the data race");
        System.out.println("case 3 synchronized int: " + guardedResult
                + "   expected 0 (the lock makes read-modify-write indivisible)");
    }

    /** Thread-safe: getAndIncrement/getAndDecrement are single atomic operations. */
    private static int runAtomicCounter() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();
        runBothWays(counter::getAndIncrement, counter::getAndDecrement);
        return counter.get();
    }

    /** Broken on purpose: plainCounter++ is three steps, so updates get lost. */
    private static int runPlainCounter() throws InterruptedException {
        plainCounter = 0;
        runBothWays(() -> plainCounter++, () -> plainCounter--);
        return plainCounter;
    }

    /** Thread-safe: the same plain int, but nobody can interleave inside the lock. */
    private static int runGuardedCounter() throws InterruptedException {
        guardedCounter = 0;
        runBothWays(
                () -> { synchronized (LOCK) { guardedCounter++; } },
                () -> { synchronized (LOCK) { guardedCounter--; } });
        return guardedCounter;
    }

    /** Runs up on one thread and down on another, ITERATIONS times each, and waits for both. */
    private static void runBothWays(Runnable up, Runnable down) throws InterruptedException {
        Thread incrementer = new Thread(() -> repeat(up), "incrementer");
        Thread decrementer = new Thread(() -> repeat(down), "decrementer");
        incrementer.start();
        decrementer.start();
        // join() both before reading: it also publishes the threads' writes to main.
        incrementer.join();
        decrementer.join();
    }

    private static void repeat(Runnable operation) {
        for (int i = 0; i < ITERATIONS; i++) {
            operation.run();
        }
    }
}
