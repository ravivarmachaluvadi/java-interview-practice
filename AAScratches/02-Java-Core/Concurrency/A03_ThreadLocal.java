/*
 * =====================================================================
 *  ThreadLocal: per-thread state, and how it leaks in a pool   Java Core | Concurrency
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   ThreadLocal gives every thread its own private copy of a variable, so code deep in the
 *   call stack (here Service) can read a value the caller set without it being passed as a
 *   parameter. That is how request ids, security contexts and JDBC transactions are carried.
 *   The second half shows the trap: on a thread POOL the thread outlives the task, so a value
 *   that is never removed is still there when the next, unrelated task runs on that thread.
 *
 * WHAT YOU WILL SEE
 *   part 1: two tasks on two threads each read back exactly the value they set - no sharing
 *   part 2: on a single-thread pool, task B reads 42 that task A left behind (a leak)
 *           after a task that calls remove() in a finally block, the next task reads null
 *
 * HOW IT WORKS
 *   1. IntHolders.local is one static ThreadLocal object, but the value lives in a map that
 *      hangs off each Thread. local.get() looks up the entry for the CURRENT thread.
 *   2. Part 1 submits the tasks with submit() and reads the Futures in order, so the printed
 *      lines are deterministic even though the tasks ran concurrently.
 *   3. Part 2 uses a single-thread executor, which guarantees every task runs on the SAME
 *      thread - that is what makes the leak reproducible instead of occasional.
 *   4. Both pools are shut down. Fixed: the original never called shutdown(), so its
 *      non-daemon pool threads kept the JVM alive forever and the demo never exited.
 *
 * KEY INSIGHT
 *   ThreadLocal solves sharing by not sharing: confine the state to one thread. Its lifetime
 *   is the THREAD's, not the task's, so on any pooled or container-managed thread you must
 *   clear it yourself: set in a try, remove() in the finally. No remove means both a stale
 *   read for the next task and a classloader leak in an app server.
 *
 * GOTCHAS
 *   - InheritableThreadLocal copies to child threads at creation - but pool threads are
 *     created once, so it will not follow your value into a pool.
 *   - ThreadLocal does not follow work across CompletableFuture / reactive hops.
 *   - The entries are weakly keyed but the VALUES are strongly held until remove() or a
 *     later get/set on that thread cleans the stale slot.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Where is the value actually stored? (Thread.threadLocals, a ThreadLocalMap)
 *   - Why can ThreadLocal leak memory in Tomcat, and what does remove() in finally fix?
 *   - ThreadLocal vs passing a context object explicitly - trade-offs.
 *   - What do virtual threads and ScopedValue (Java 21+) change here?
 *
 * RUN
 *   main() runs the isolation case, the leak case and the remove() case, actual vs expected.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class ThreadLocalDemo {

    private static final Service SERVICE = new Service();

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        eachThreadKeepsItsOwnCopy();
        valueSurvivesIntoTheNextTaskOnAPooledThread();
    }

    /** Part 1: two tasks, two threads, two independent copies of the same ThreadLocal. */
    private static void eachThreadKeepsItsOwnCopy()
            throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        List<Future<Integer>> readBacks = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            int taskId = i;
            readBacks.add(pool.submit(() -> {
                IntHolders.local.set(taskId);
                // Service never receives taskId as an argument; it reads the thread's copy.
                return SERVICE.readValue();
            }));
        }
        shutdown(pool);

        for (int taskId = 0; taskId < readBacks.size(); taskId++) {
            System.out.println("case " + (taskId + 1) + ": task " + taskId + " set " + taskId
                    + ", Service read back " + readBacks.get(taskId).get()
                    + "   expected " + taskId);
        }
    }

    /** Part 2: one thread, four tasks in a row - the leak and the fix. */
    private static void valueSurvivesIntoTheNextTaskOnAPooledThread()
            throws InterruptedException, ExecutionException {
        ExecutorService singleThread = Executors.newSingleThreadExecutor();

        // Task A sets a value and, like most buggy code, never removes it.
        singleThread.submit(() -> IntHolders.local.set(42)).get();

        // Task B is a brand new task - but it lands on the very same thread.
        Integer leaked = singleThread.submit(() -> IntHolders.local.get()).get();
        System.out.println("case 3: next task on the reused thread sees " + leaked
                + "   expected 42 (stale value leaked from the previous task)");

        // Task C does it properly: set in the try, remove in the finally.
        singleThread.submit(() -> {
            try {
                IntHolders.local.set(7);
                SERVICE.readValue();
            } finally {
                IntHolders.local.remove();
            }
        }).get();

        Integer afterRemove = singleThread.submit(() -> IntHolders.local.get()).get();
        System.out.println("case 4: next task after remove() sees " + afterRemove
                + "   expected null (the slot was cleared before the task ended)");

        shutdown(singleThread);
    }

    private static void shutdown(ExecutorService pool) throws InterruptedException {
        pool.shutdown();
        if (!pool.awaitTermination(2, TimeUnit.SECONDS)) {
            pool.shutdownNow(); // never leave non-daemon pool threads holding the JVM open
        }
    }
}

/** Stands in for code far from the caller that needs the caller's context. */
class Service {
    Integer readValue() {
        return IntHolders.local.get();
    }
}

/** The ThreadLocal is static and shared; the VALUE behind it is per thread. */
class IntHolders {
    static final ThreadLocal<Integer> local = new ThreadLocal<>();
}
