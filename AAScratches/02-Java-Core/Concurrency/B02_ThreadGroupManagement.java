/*
 * =====================================================================
 *  ThreadGroup: bulk interrupt of a set of threads   Java Core | Concurrency | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   A ThreadGroup is a named container every thread belongs to. It lets you count the
 *   live threads in the group (activeCount), list them (enumerate) and interrupt all
 *   of them with one call. This is the legacy way to manage a fleet of worker threads.
 *
 * WHAT YOU WILL SEE
 *   Three "client" threads tick every 150 ms; main lets them run ~450 ms.
 *   activeCount() reports 3 while they are alive.
 *   clientGroup.interrupt() delivers InterruptedException to all three at once, each
 *   worker prints that it was interrupted, and main joins them and reports 3.
 *
 * HOW IT WORKS
 *   1. Create a ThreadGroup and pass it as the first argument of the Thread constructor.
 *   2. Each worker sleeps in a loop; sleep() is the interruptible point.
 *   3. activeCount() is an estimate of live threads; enumerate(array) copies the live
 *      Thread references out so main can join them afterwards.
 *   4. group.interrupt() sets the interrupt flag on every thread in the group (and in
 *      its child groups). A thread parked in sleep/wait/join throws InterruptedException.
 *
 * KEY INSIGHT
 *   Interrupt is a request, not a kill. The group call only sets flags; the threads
 *   still have to cooperate by catching InterruptedException and returning. Nothing
 *   here can stop a worker that ignores the flag and never blocks.
 *
 * GOTCHAS
 *   - activeCount() and enumerate() are only estimates: threads can die or start between
 *     the count and the copy, so size the array with slack and use enumerate's return.
 *   - Catching InterruptedException clears the interrupt flag. If you do not exit, you
 *     must restore it with Thread.currentThread().interrupt().
 *   - ThreadGroup is effectively deprecated: no way to collect results, no bounded pool,
 *     no shutdown handshake. Real code uses an ExecutorService and shutdownNow().
 *   - stop()/suspend()/resume() on a group are unsafe and removed; never mention them
 *     as an answer except to say why they are gone.
 *
 * INTERVIEW FOLLOW-UPS
 *   - How would you write this with ExecutorService? (shutdownNow returns the queued
 *     tasks and interrupts the running ones.)
 *   - What happens if a worker swallows InterruptedException and loops forever?
 *   - Difference between Thread.interrupted() and isInterrupted().
 *   - How do you interrupt a thread blocked on socket or file IO? (close the channel)
 *
 * RUN
 *   main() runs 3 checks (group size, interrupt delivery, group drained) and prints
 *   actual vs expected. The whole demo finishes in well under 2 seconds.
 */
import java.util.concurrent.atomic.AtomicInteger;

class ThreadGroupManagement {

    static final int CLIENTS = 3;
    static final long TICK_MS = 150;
    static final long LET_THEM_RUN_MS = 450;

    /** Counts how many workers actually exited through the InterruptedException path. */
    static final AtomicInteger interruptedCount = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        ThreadGroup clientGroup = new ThreadGroup("ClientConnectionGroup");

        for (int i = 1; i <= CLIENTS; i++) {
            new Thread(clientGroup, new DummyClient(), "ClientThread-" + i).start();
        }

        Thread.sleep(LET_THEM_RUN_MS);

        int active = clientGroup.activeCount();
        print("case 1 (threads alive in the group)", active, CLIENTS);

        // Copy the live threads out BEFORE interrupting, so we can join them afterwards.
        Thread[] members = new Thread[active * 2];   // slack: activeCount() is only an estimate
        int found = clientGroup.enumerate(members);

        System.out.println("Interrupting all threads in the group with one call...");
        clientGroup.interrupt();

        for (int i = 0; i < found; i++) {
            members[i].join(1000);                  // bounded join so the demo cannot hang
        }

        print("case 2 (workers that saw the interrupt)", interruptedCount.get(), CLIENTS);
        print("case 3 (threads left in the group)", clientGroup.activeCount(), 0);
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}

class DummyClient implements Runnable {
    @Override
    public void run() {
        try {
            // Far more ticks than main will wait for, so the interrupt is what ends us.
            for (int i = 0; i < 100; i++) {
                System.out.println("  " + Thread.currentThread().getName() + " tick " + i);
                Thread.sleep(ThreadGroupManagement.TICK_MS);
            }
            System.out.println("  " + Thread.currentThread().getName() + " finished on its own");
        } catch (InterruptedException e) {
            // sleep() threw, which also CLEARED the interrupt flag. We exit, so that is fine.
            System.out.println("  " + Thread.currentThread().getName() + " was interrupted");
            ThreadGroupManagement.interruptedCount.incrementAndGet();
        }
    }
}
