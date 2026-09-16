/**
 * Problem: Demonstrates the difference between atomic and non‑atomic updates to a shared integer.
 *
 * Two threads concurrently increment and decrement a counter:
 *   - One thread uses AtomicInteger.getAndIncrement()/getAndDecrement() (thread‑safe).
 *   - The other thread modifies a plain int field valueInt without synchronization (data race).
 *
 * Approach: Each thread loops 100,000 times performing its respective operation. After both threads finish,
 * the atomic counter should be zero while the non‑atomic counter will likely have an incorrect final value.
 *
 * Time Complexity: O(n) where n is the number of iterations per thread (here 200,000 total operations).
 * Space Complexity: O(1) – only a few primitive variables and an AtomicInteger instance are used.
 */
import java.util.concurrent.atomic.AtomicInteger;

class TwoThreadsToSingleValue {
    static AtomicInteger value = new AtomicInteger();
    static int valueInt = 0;
    public static void main(String[] args) throws InterruptedException {

        Thread thread0 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                value.getAndIncrement();
                valueInt++;
            }
        });

        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                value.getAndDecrement();
                valueInt--;
            }
        });
        thread0.start();
        thread1.start();
        thread0.join();
        thread1.join();
        System.out.println(value.get()); // 0
        System.out.println(valueInt); // -6839
    }
}
