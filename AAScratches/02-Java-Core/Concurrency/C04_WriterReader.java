import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
class WriterReader {
    public static void main(String[] args) throws InterruptedException {
        Value value = new Value();
        ReentrantLock lock = new ReentrantLock();
        Condition writerCondition = lock.newCondition();
        Condition readerCondition = lock.newCondition();
        Reader reader = new Reader(value, lock, readerCondition, writerCondition);
        Writer writer = new Writer(value, lock, readerCondition, writerCondition);
        Thread eventThread = new Thread(reader::run);
        Thread oddhread = new Thread(writer::run);
        eventThread.start();
        oddhread.start();
        eventThread.join();
        oddhread.join();
    }
}

class Value {
    int value = 0;
}

class Reader {
    Value value;
    final ReentrantLock lock;
    Condition writerCondition;
    Condition readerCondition;

    Reader(Value value, ReentrantLock lock, Condition readerCondition, Condition writerCondition) {
        this.value = value;
        this.lock = lock;
        this.writerCondition = writerCondition;
        this.readerCondition = readerCondition;
    }

    public void run() {
        while (value.value < 100) {
            lock.lock();
            if (value.value == 0) {
                try {
                    writerCondition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("Reader " + value.value);
            value.value = 0;
            readerCondition.signal();
            lock.unlock();
        }
    }
}

class Writer {
    Value value;
    final ReentrantLock lock;
    Condition writerCondition;
    Condition readerCondition;

    AtomicInteger atomicInteger = new AtomicInteger(1);

    Writer(Value value, ReentrantLock lock, Condition readerCondition, Condition writerCondition) {
        this.value = value;
        this.lock = lock;
        this.writerCondition = writerCondition;
        this.readerCondition = readerCondition;
    }

    public void run() {
        while (value.value < 100) {
            lock.lock();
            if (value.value != 0) {
                try {
                    readerCondition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            value.value = atomicInteger.getAndIncrement();
            System.out.println("Writing : " + value.value);
            writerCondition.signal();
            lock.unlock();
        }
    }
}
