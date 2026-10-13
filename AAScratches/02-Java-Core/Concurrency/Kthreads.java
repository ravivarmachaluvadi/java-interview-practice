/*
 * =====================================================================
 * Kthreads                                  LeetCode ? | Multithreading
 * =====================================================================
 *
 * PROBLEM
 *   Create 4 threads that print numbers from 1 to 100 in a coordinated
 *   order. Each thread prints numbers belonging to its assigned
 *   remainder group, and the output must be in ascending order.
 *
 * EXAMPLE
 *   Thread 1 prints: 1, 5, 9, 13, ...
 *   Thread 2 prints: 2, 6, 10, 14, ...
 *   Thread 3 prints: 3, 7, 11, 15, ...
 *   Thread 0 prints: 4, 8, 12, 16, ...
 *
 *   Actual combined output:
 *   1 2 3 4 5 6 7 8 9 10 ... 100
 *
 * APPROACH
 *   1. Use a shared int[] counter to maintain the current number.
 *   2. Create 4 threads, assigning each a reminder from 0 to 3.
 *   3. Use synchronized printer() to allow only one thread to
 *      access the shared counter at a time.
 *   4. Use counter % 4 to determine which thread should print.
 *   5. Use wait() when it is another thread's turn.
 *   6. Increment the counter and call notifyAll() after printing.
 *   7. Use join() in main() to wait for all threads to finish.
 *
 * KEY INSIGHT
 *   The shared counter determines whose turn it is.
 *   synchronized protects the shared state, wait() releases the
 *   monitor while waiting, and notifyAll() wakes waiting threads
 *   so the correct thread can check whether it is its turn.
 *
 * COMPLEXITY
 *   Time  O(N) total useful work, where N = 100.
 *   Space O(K) auxiliary space for K threads and the thread array.
 *
 * INTERVIEW FOLLOW-UPS
 *   1. Why is the counter an int[] instead of an int?
 *   2. Why use synchronized?
 *   3. Why must wait() be inside a while loop?
 *   4. Why use notifyAll() instead of notify()?
 *   5. Why start all threads before calling join()?
 *   6. What happens if a thread is interrupted?
 *
 * RUN
 *   main() starts four threads, waits for each using join(),
 *   and prints numbers from 1 through 100 in ascending order.
 */


class Kthreads {

    int[] counter = {1};
    static int noOfThreads = 4;

    public static void main(String[] args) throws InterruptedException {
        Kthreads sc = new Kthreads();
        Thread[] tarr = new Thread[4];

        for (int i = 0; i < noOfThreads; i++) {
            int finalI = i;

            Thread th = new Thread(() -> {
                try {
                    sc.printer(finalI);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            th.start();
            tarr[finalI] = th;
        }

        for (Thread thread : tarr) {
            thread.join();
        }
    }

    public synchronized void printer(int reminder)
            throws InterruptedException {

        while (counter[0] <= 100) {

            if ((counter[0] - 1) % noOfThreads == reminder) {
                System.out.println(
                    reminder + "th thread printing " + counter[0]
                );

                counter[0]++;
                notifyAll();

            } else {
                wait();
            }
        }
    }
}
