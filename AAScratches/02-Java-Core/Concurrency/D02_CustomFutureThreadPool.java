/*
 * =====================================================================
 *  Build a thread pool from scratch      Concurrency | Hard | MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Implement a miniature ExecutorService without using Executors. It must accept Callable<T>
 *   tasks, hand the caller a Future<T> immediately, run tasks on a fixed set of worker
 *   threads, carry both a result and a thrown exception back to get(), and shut down cleanly.
 *
 * DESIGN  (three pieces, that is the whole pool)
 *   BlockingQueue<FutureTask<?>>  the handoff. put() from the caller, poll() from a worker.
 *                                 It is the ONLY shared mutable state and is already
 *                                 thread-safe, so the pool needs no locks of its own.
 *   FutureTask<T>                 the bridge: it IS both a Runnable and a Future. The worker
 *                                 calls run(), the caller calls get(). It owns the result
 *                                 handoff, the blocking of get(), and the exception capture.
 *   WorkerThread[]                the engine. A plain Thread in a loop: take, run, repeat.
 *                                 Never let an exception escape this loop.
 *
 * KEY DECISIONS
 *   - poll(timeout) rather than take(), so a worker can notice the shutdown flag. take()
 *     blocks forever and can only be broken by interrupt, which would kill a running task.
 *   - shutdown() is graceful: stop accepting, drain what is queued, then exit. Tasks already
 *     submitted still run. shutdownNow() (interrupt the workers) would be the rude variant.
 *   - Workers are daemon threads. Belt and braces: a bug in the shutdown path then cannot
 *     keep the JVM alive, which is exactly what the original version of this file did.
 *   - submitTask after shutdown throws RejectedExecutionException instead of queueing a task
 *     that would never run - a silently lost task is far worse than a loud failure.
 *
 *   Fixed: the original workers ran `while (true) { queue.take(); }` as NON-daemon threads
 *   with no shutdown method, so the JVM hung forever after main finished (verified: the
 *   process had to be killed on timeout). Fixed: the second Callable was built but never
 *   submitted, so nothing ever proved a second task could run.
 *
 * COMPLEXITY
 *   Time  O(1) amortised to submit; a task waits O(queued / poolSize) turns before it runs
 *   Space O(P + Q)  P worker threads plus Q queued tasks (the queue is unbounded here - a
 *                   real pool bounds it and applies a rejection policy when it fills)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why FutureTask and not your own Future? (it already implements the state machine
 *     NEW -> COMPLETING -> NORMAL/EXCEPTIONAL/CANCELLED plus the park/unpark in get())
 *   - Bound the queue: what should submit do when it is full? (block, drop, or run on the
 *     caller's thread - CallerRunsPolicy, which back-pressures the producer)
 *   - Where does a task's exception go? (captured by FutureTask, re-thrown from get() inside
 *     ExecutionException - it does NOT kill the worker thread)
 *   - Why does a real ThreadPoolExecutor need core vs max pool size? (grow only once the
 *     queue is full, then retire idle non-core threads after keepAliveTime)
 *
 * RUN
 *   main() submits a value task, a second of a different type, and one that throws, printing
 *   actual vs expected; then shuts down and shows a late submit rejected. Under 2 seconds.
 */
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

class CustomFutureThreadPool {

    /** How long a worker parks on the queue before re-checking the shutdown flag. */
    private static final long POLL_MILLIS = 20;

    private final BlockingQueue<FutureTask<?>> taskQueue = new LinkedBlockingQueue<>();
    private final WorkerThread[] workers;
    private volatile boolean shutdown = false;   // volatile: written by main, read by workers

    CustomFutureThreadPool(int poolSize) {
        this.workers = new WorkerThread[poolSize];
        for (int i = 0; i < poolSize; i++) {
            workers[i] = new WorkerThread("pool-worker-" + i);
            workers[i].start();
        }
    }

    /** Wraps the task in a FutureTask, queues it, and returns that same object as a Future. */
    <T> Future<T> submitTask(Callable<T> task) {
        if (shutdown) {
            throw new RejectedExecutionException("pool is shut down");
        }
        FutureTask<T> futureTask = new FutureTask<>(task);
        try {
            taskQueue.put(futureTask);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RejectedExecutionException("interrupted while queueing", e);
        }
        return futureTask;
    }

    /** Stop accepting work. Already-queued tasks still run, then the workers exit. */
    void shutdown() {
        shutdown = true;
    }

    /** @return true if every worker finished within the timeout. */
    boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        for (WorkerThread w : workers) {
            long remainingMillis = (deadline - System.nanoTime()) / 1_000_000;
            if (remainingMillis > 0) {
                w.join(remainingMillis);
            }
            if (w.isAlive()) {
                return false;
            }
        }
        return true;
    }

    private class WorkerThread extends Thread {

        WorkerThread(String name) {
            super(name);
            setDaemon(true);     // a stuck worker can never hold the JVM open
        }

        @Override
        public void run() {
            while (true) {
                FutureTask<?> task;
                try {
                    task = taskQueue.poll(POLL_MILLIS, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;                       // shutdownNow-style exit
                }
                if (task == null) {
                    if (shutdown) {
                        return;                   // nothing left and no more coming: done
                    }
                    continue;                     // idle tick, go round again
                }
                // FutureTask.run() catches whatever the Callable throws and stores it,
                // so no task failure can ever kill this worker.
                task.run();
            }
        }
    }

    // ----------------------------------------------------------------- demo

    public static void main(String[] args) throws Exception {
        CustomFutureThreadPool pool = new CustomFutureThreadPool(3);

        Future<String> slow = pool.submitTask(() -> {
            Thread.sleep(100);                    // pretend work; get() must block for it
            return "Task 1 Completed";
        });
        Future<Integer> fast = pool.submitTask(() -> 42);
        Future<Integer> boom = pool.submitTask(() -> {
            throw new IllegalStateException("task blew up");
        });

        print("case 1 (result)   ", slow.get(), "Task 1 Completed");
        print("case 2 (other type)", fast.get(), 42);

        String failure;
        try {
            failure = "no exception: " + boom.get();
        } catch (ExecutionException e) {
            // The cause is the exception the Callable actually threw, not a pool error.
            failure = e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage();
        }
        print("case 3 (throwing) ", failure, "IllegalStateException: task blew up");

        pool.shutdown();
        print("case 4 (terminated)", pool.awaitTermination(2, TimeUnit.SECONDS), true);

        String rejected;
        try {
            pool.submitTask(() -> "too late");
            rejected = "accepted";
        } catch (RejectedExecutionException e) {
            rejected = "rejected: " + e.getMessage();
        }
        print("case 5 (late submit)", rejected, "rejected: pool is shut down");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
