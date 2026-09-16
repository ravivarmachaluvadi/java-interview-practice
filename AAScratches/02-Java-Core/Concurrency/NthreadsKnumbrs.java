/**
 * Problem: Print numbers 1 to 100 using four threads, each thread printing numbers that satisfy
 * a specific modulo condition (value % 4 == remainder). The output must be in ascending order.
 *
 * Approach: Each Printer thread synchronizes on a shared ValueObject. Inside the synchronized block,
 * it loops until the value exceeds 100. If the current value matches its modulo condition, the thread
 * prints and increments the value, then notifies all waiting threads. Otherwise, it waits for a
 * notification. This ensures orderly printing without race conditions.
 *
 * Time Complexity: O(n) where n = 100 (each number is processed once).
 * Space Complexity: O(1) auxiliary space; only a few integer variables and the shared object are used.
 */
class NthreadsKnumbrs {
    static int val = 100;
    public static void main(String[] args) {

        ValueObject obj = new ValueObject();
        Printer printer0 = new Printer(obj, 4, 1);
        Printer printer1 = new Printer(obj, 4, 2);
        Printer printer2 = new Printer(obj, 4, 3);
        Printer printer3 = new Printer(obj, 4, 0);

        Thread thread0 = new Thread(printer0);
        Thread thread1 = new Thread(printer1);
        Thread thread2 = new Thread(printer2);
        Thread thread3 = new Thread(printer3);

        thread0.start();
        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread0.join();
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


}

class ValueObject {
    public int value = 1;
}

/*1 2 3 4 5 6 7 8 9

n*/
class Printer implements Runnable {

    final ValueObject obj;
    int threadVal;
    int remainder;

    Printer(ValueObject obj, int threadVal, int remainder) {
        this.obj = obj;
        this.threadVal = threadVal;
        this.remainder = remainder;
    }

    @Override
    public void run() {
        synchronized (obj) {
            while (obj.value < 101) {
                if (obj.value % threadVal == remainder) {
                    System.out.println("match case : " + obj.value + " : " + "thread " + remainder);
                    obj.value++;
                    obj.notifyAll();
                } else {
                    try {
                        obj.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Exception : " + e.getMessage());
                    }
                }
            }
        }
    }
}
