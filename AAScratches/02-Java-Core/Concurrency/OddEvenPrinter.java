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
