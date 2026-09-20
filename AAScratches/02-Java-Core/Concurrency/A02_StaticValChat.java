/*
 * =====================================================================
 *  Which Monitor Are You Locking?                      Java Core | Concurrency
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   A lock only protects state if EVERY thread that touches that state takes the SAME lock.
 *   Here one static counter and one instance counter are bumped by two kinds of task - a
 *   static method and an instance method - running on a 10-thread pool. The demo runs the
 *   same workload twice: once with both methods locking the same monitor, once with the
 *   instance method locking a different object.
 *
 * WHAT YOU WILL SEE
 *   run 1 (one shared monitor)   -> both counters land exactly on 100000
 *   run 2 (two different monitors) -> both counters land short, e.g. 96324, updates were lost
 *
 * HOW IT WORKS
 *   1. 50000 static tasks and 50000 instance tasks are submitted; each task bumps BOTH
 *      counters once, so the correct total for each counter is 100000.
 *   2. In run 1 both methods use synchronized (StaticValChat.class), so the static and the
 *      instance work are mutually exclusive.
 *   3. In run 2 the instance method locks OTHER_LOCK instead. Two threads can then be inside
 *      the two critical sections at the same time and a ++ gets lost, exactly as in A01.
 *   4. The pool is shut down with shutdown() + awaitTermination() instead of spinning on
 *      isTerminated(), which is a busy-wait that burns a whole core for nothing.
 *
 * KEY INSIGHT
 *   Locks live on objects, not on code. "synchronized" on an instance method locks this;
 *   on a static method it locks the Class object. Shared STATIC state therefore has to be
 *   guarded by a monitor every caller can reach - the Class object or a static final lock -
 *   never by "this", because each instance would be a different lock.
 *
 * GOTCHAS
 *   - synchronized(SomeClass.class) serialises all instances; fine here, a bottleneck at scale.
 *   - while (!pool.isTerminated()) {} is a spin loop. Use awaitTermination with a timeout.
 *   - Submitting millions of tasks first queues millions of lambdas: an unbounded
 *     LinkedBlockingQueue is a memory leak waiting to happen. Bound the queue in real code.
 *
 * INTERVIEW FOLLOW-UPS
 *   - What does a synchronized static method lock, and what does a synchronized instance
 *     method lock? Can they run concurrently?
 *   - Why is synchronized(this) wrong for guarding static state?
 *   - Replace both counters with AtomicInteger - what do you gain, and what do you lose if
 *     the two counters must stay consistent with each other?
 *   - shutdown() vs shutdownNow() vs close() (Java 19+), and what awaitTermination returns.
 *
 * RUN
 *   main() runs the correct version and the broken version and prints actual vs expected.
 */
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class StaticValChat {

    private static final int TASKS_PER_KIND = 50_000;

    /** One copy for the whole class - shared by every thread and every instance. */
    static int sharedStaticVal;

    /** One copy per object; here a single object is shared by all tasks. */
    int instanceVal;

    /** A monitor that is deliberately NOT the one the static method uses. */
    private static final Object OTHER_LOCK = new Object();

    public static void main(String[] args) throws InterruptedException {
        int expected = 2 * TASKS_PER_KIND; // every task bumps both counters exactly once

        int[] shared = runWorkload(true);
        System.out.println("run 1 one monitor  - static counter  : " + shared[0]
                + "   expected " + expected);
        System.out.println("run 1 one monitor  - instance counter: " + shared[1]
                + "   expected " + expected);

        int[] split = runWorkload(false);
        System.out.println("run 2 two monitors - static counter  : " + split[0]
                + "   expected: usually < " + expected + " (lost updates)");
        System.out.println("run 2 two monitors - instance counter: " + split[1]
                + "   expected: usually < " + expected + " (lost updates)");
    }

    /**
     * Submits both kinds of task to a 10-thread pool and waits for them all.
     *
     * @param shareOneMonitor true  -> the instance method also locks StaticValChat.class
     *                        false -> the instance method locks OTHER_LOCK, so the two
     *                                 critical sections no longer exclude each other
     * @return {final static counter, final instance counter}
     */
    private static int[] runWorkload(boolean shareOneMonitor) throws InterruptedException {
        sharedStaticVal = 0;
        StaticValChat target = new StaticValChat();
        ExecutorService pool = Executors.newFixedThreadPool(10);

        for (int i = 0; i < TASKS_PER_KIND; i++) {
            pool.execute(() -> incrementFromStatic(target));
            pool.execute(shareOneMonitor ? target::incrementLockingClass
                                         : target::incrementLockingOtherObject);
        }

        pool.shutdown();
        if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
            pool.shutdownNow(); // do not leave worker threads running if something wedged
        }
        return new int[] { sharedStaticVal, target.instanceVal };
    }

    /** Static side: always locks the Class object, which every caller can reach. */
    private static void incrementFromStatic(StaticValChat target) {
        synchronized (StaticValChat.class) {
            sharedStaticVal++;
            target.instanceVal++;
        }
    }

    /** Correct instance side: takes the very same monitor as the static method. */
    private void incrementLockingClass() {
        synchronized (StaticValChat.class) {
            sharedStaticVal++;
            this.instanceVal++;
        }
    }

    /** Broken instance side: a real lock, just not the one that guards this state. */
    private void incrementLockingOtherObject() {
        synchronized (OTHER_LOCK) {
            sharedStaticVal++;
            this.instanceVal++;
        }
    }
}
