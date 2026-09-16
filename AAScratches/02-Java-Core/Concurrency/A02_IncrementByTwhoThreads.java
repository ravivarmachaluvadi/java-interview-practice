/**
 * Problem: Demonstrates concurrent increment and decrement operations on shared data.
 * Two threads run in parallel; one increments a volatile int array element 1000 times,
 * the other decrements it 1000 times, while an AtomicInteger tracks the same count.
 *
 * Approach: Each thread calls instance methods that modify the shared array element
 * and the AtomicInteger. The array update is not atomic, but the AtomicInteger uses
 * CAS to ensure thread‑safe updates. No explicit synchronization is used in the code.
 *
 * Time Complexity: O(n) where n = 2000 operations (1000 increments + 1000 decrements).
 * Space Complexity: O(1) – only a fixed-size array and an AtomicInteger are allocated.
 */
package com.infotech.client;

import java.util.concurrent.atomic.AtomicInteger;

class IncrementByTwhoThreads {
    static volatile int[] array = {0};
    static AtomicInteger integer = new AtomicInteger();

    public static void main(String[] args) throws InterruptedException {
        final IncrementByTwhoThreads incrementByTwhoThreads = new IncrementByTwhoThreads();
        Thread a = new Thread("A") {
            public void run() {
                for (int i = 1; i <= 1000; i++) {
                    incrementByTwhoThreads.increase();
                }
            }
        };
        Thread b = new Thread("B") {
            public void run() {
                for (int i = 1; i <= 1000; i++) {
                    incrementByTwhoThreads.decrease();
                }
            }
        };
        a.start();
        b.start();
        a.join();
        b.join();
        System.out.println(array[0]);
        System.out.println(integer.get());
    }

    public /*synchronized*/  void increase() {
        array[0]++;
        integer.getAndIncrement();
    }
    public /*synchronized*/ void decrease() {
        array[0]--;
        integer.getAndDecrement();
    }
}
