/**
 * Prints numbers from 0 to 99 in alternating even and odd order using two threads.
 *
 * The EvenPrinter thread prints when the shared counter is even, while the OddPrinter
 * thread prints when it is odd. They coordinate via a ReentrantLock and its wait/notify
 * mechanism on a common monitor object.
 *
 * Approach:
 * 1. Each printer acquires the lock and loops until the counter reaches 100.
 * 2. If it's not this thread's turn, it waits; otherwise it prints, increments,
 *    and notifies the other thread.
 *
 * Time Complexity: O(n) where n = 100 (constant work per number).
 * Space Complexity: O(1) – only a few primitive variables are used.
 */
import java.util.concurrent.locks.ReentrantLock;

class Scratch {
    public static void main(String[] args) throws InterruptedException {
        Value value = new Value();
        ReentrantLock lock = new ReentrantLock();
        EvenPrinter evenPrinter = new EvenPrinter(value, lock);
        OddPrinter oddPrinter = new OddPrinter(value, lock);
        Thread eventThread = new Thread(evenPrinter::run);
        Thread oddhread = new Thread(oddPrinter::run);
        eventThread.start();
        oddhread.start();
        eventThread.join();
        oddhread.join();
    }
}

class Value {
    int value = 0;
}
class EvenPrinter {
    Value value;
    final ReentrantLock lock;

    EvenPrinter(Value value, ReentrantLock lock) {
        this.value = value;
        this.lock = lock;
    }

    public void run() {
        synchronized (lock) {
            while (value.value < 100) {
                if (value.value % 2 == 1) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Even Thread : " + value.value);
                value.value++;
                lock.notify();
            }
        }
    }
}


class OddPrinter {
    Value value;
    final ReentrantLock lock;

    OddPrinter(Value value, ReentrantLock lock) {
        this.value = value;
        this.lock = lock;
    }

    public void run() {
        synchronized (lock) {
            while (value.value < 100) {
                if (value.value % 2 == 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Odd Thread : " + value.value);
                value.value++;
                lock.notify();
            }
        }
    }
}
