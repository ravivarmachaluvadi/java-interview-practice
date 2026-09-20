/*
 * =====================================================================
 *  Stopping a Thread the Only Legal Way: interrupt   Java Core | Concurrency   MUST-KNOW
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   Java has no way to kill a thread from outside (Thread.stop is deprecated and unsafe).
 *   Cancellation is cooperative: you set the target thread's interrupt flag and the target
 *   is responsible for noticing and winding down. There are exactly two ways to notice:
 *     - you are blocked in sleep/wait/join/take -> you get an InterruptedException
 *     - you are busy computing -> you must poll Thread.currentThread().isInterrupted()
 *   Both workers are shown here, each asked to do 100 units of work and stopped early.
 *
 * WHAT YOU WILL SEE
 *   sleeping worker: stops after ~3 of its 100 ticks, via InterruptedException out of sleep
 *   inside the catch block the interrupt flag reads FALSE - throwing cleared it
 *   the worker restores it with Thread.currentThread().interrupt(), so it reads true again
 *   busy worker: stops after millions of iterations, the moment its poll sees the flag
 *
 * HOW IT WORKS
 *   1. main starts the worker, sleeps briefly, then calls thread.interrupt(). That does not
 *      stop anything - it only sets a boolean on that thread (and wakes a blocking call).
 *   2. LongRunningThread is inside Thread.sleep, so the JVM throws InterruptedException
 *      there, CLEARS the flag, and the catch block exits the loop cleanly.
 *   3. Before exiting it calls Thread.currentThread().interrupt() to put the flag back, so
 *      callers further up the stack can still see that this thread was cancelled.
 *   4. BusyWorker never blocks, so no exception can be thrown at it; it checks the flag at
 *      the top of every iteration. main then join()s each worker with a timeout.
 *
 * KEY INSIGHT
 *   interrupt() is a request, not a kill. Catching InterruptedException clears the flag, so
 *   a catch block must do one of two things: propagate the exception, or restore the flag
 *   with Thread.currentThread().interrupt() before returning. Swallowing it - catch, log,
 *   carry on - destroys the only evidence that cancellation was requested and is the single
 *   most common concurrency bug in production Java.
 *
 * GOTCHAS
 *   - The original checked isInterrupted() AFTER sleep returned. That check is dead code:
 *     if the interrupt arrives during sleep you leave by exception, and if it arrives while
 *     the thread is running, sleep will throw immediately on the next call anyway.
 *   - Thread.interrupted() CLEARS the flag; thread.isInterrupted() only reads it.
 *   - Blocking on a plain InputStream or an intrinsic lock is NOT interruptible. Sockets,
 *     InterruptibleChannel and ReentrantLock.lockInterruptibly() are.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why was Thread.stop() deprecated? What can it corrupt?
 *   - How does ExecutorService.shutdownNow() cancel tasks, and what must the task do?
 *   - What is a non-interruptible blocking call, and how do you cancel one?
 *   - Where does Future.cancel(true) fit, and what does mayInterruptIfRunning mean?
 *
 * RUN
 *   main() interrupts a sleeping worker and a busy worker and prints actual vs expected.
 *   The whole demo finishes in well under a second.
 */

class ThreadInterruptDemo {

    public static void main(String[] args) throws InterruptedException {
        LongRunningThread sleeper = new LongRunningThread();
        Thread sleepingWorker = new Thread(sleeper, "sleeping-worker");
        sleepingWorker.start();
        Thread.sleep(350);             // let it complete a few ticks first
        sleepingWorker.interrupt();    // only sets a flag - and wakes it out of sleep
        sleepingWorker.join(1000);

        System.out.println("case 1: sleeping worker still alive? " + sleepingWorker.isAlive()
                + "   expected false (it exited on InterruptedException)");
        System.out.println("case 2: ticks done " + sleeper.ticksCompleted
                + " of 100, stopped early? " + (sleeper.ticksCompleted < 100)
                + "   expected true");
        System.out.println("case 3: flag inside the catch block: " + sleeper.flagInsideCatch
                + "   expected false (throwing InterruptedException cleared it)");
        System.out.println("case 4: flag after restoring it: " + sleeper.flagAfterRestore
                + "   expected true (Thread.currentThread().interrupt() put it back)");

        BusyWorker busy = new BusyWorker();
        Thread busyWorker = new Thread(busy, "busy-worker");
        busyWorker.start();
        Thread.sleep(200);
        busyWorker.interrupt();
        busyWorker.join(1000);

        System.out.println("case 5: busy worker still alive? " + busyWorker.isAlive()
                + "   expected false (its poll of isInterrupted() saw the flag)");
        System.out.println("case 6: busy worker noticed the flag? " + busy.noticedInterrupt
                + "   expected true");
    }
}

/** Worker that spends its life blocked in sleep(): it learns of cancellation by exception. */
class LongRunningThread implements Runnable {

    volatile int ticksCompleted;        // volatile: main reads these after join()
    volatile boolean flagInsideCatch = true;
    volatile boolean flagAfterRestore;

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(100);      // stands in for real blocking work
                ticksCompleted++;
            } catch (InterruptedException e) {
                // sleep threw, which means the flag was ALREADY cleared on the way out.
                flagInsideCatch = Thread.currentThread().isInterrupted();
                // Restore it so anyone above us still sees that we were cancelled.
                Thread.currentThread().interrupt();
                flagAfterRestore = Thread.currentThread().isInterrupted();
                return;                 // wind down, do not keep looping
            }
        }
    }
}

/** Worker that never blocks: nothing can throw at it, so it must poll the flag itself. */
class BusyWorker implements Runnable {

    volatile long iterations;
    volatile boolean noticedInterrupt;

    @Override
    public void run() {
        long sum = 0;
        // Bound the loop as well, so the demo always ends even if the interrupt is missed.
        for (long i = 0; i < 2_000_000_000L; i++) {
            if (Thread.currentThread().isInterrupted()) {
                noticedInterrupt = true;
                break;
            }
            sum += i;                   // some work so the loop is not optimised away
            iterations = i;
        }
        if (sum == Long.MIN_VALUE) {
            System.out.println("unreachable, keeps the compiler honest");
        }
    }
}
