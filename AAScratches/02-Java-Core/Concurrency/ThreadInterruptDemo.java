/**
 * Demonstrates how to safely interrupt a long‑running thread.
 *
 * The main method starts a worker thread that prints numbers 0–99,
 * sleeping two seconds between each print. After five seconds the
 * main thread interrupts the worker and waits for it to finish.
 *
 * The worker checks for interruption after each sleep; if interrupted
 * (either via isInterrupted() or an InterruptedException), it exits
 * gracefully, printing a message before returning.
 *
 * Time Complexity: O(n) where n = 100 iterations (constant in this demo).
 * Space Complexity: O(1) – only a few primitive variables are used.
 */

class ThreadInterruptDemo {
    public static void main(String[] args) throws InterruptedException {

        Thread thread = new Thread(new LongRunningThread());
        thread.start();

        // Sleep for 5 seconds before interrupting the thread
        Thread.sleep(5000);
        // Interrupt the long-running thread
        thread.interrupt();
        // Wait for the thread to finish
        thread.join();
    }
}

class LongRunningThread implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                System.out.println(i);
                Thread.sleep(2000);  // Sleep for 2 seconds

                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Stopping Long Running Thread");
                    return;
                }

            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted during sleep");
                return;
            }
        }
    }
}
