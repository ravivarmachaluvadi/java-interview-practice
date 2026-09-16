
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
