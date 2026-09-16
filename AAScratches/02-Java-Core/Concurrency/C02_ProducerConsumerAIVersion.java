import java.util.LinkedList;
import java.util.Queue;

/**
 * Yes, we need the while loop.
 * Without it, the program can fail because of:
 * <p>
 * Spurious wakeups – Threads can wake up from wait()
 * <p>
 * even if no notify was called.
 * <p>
 * Multiple threads – When multiple producers/consumers are waiting,
 * <p>
 * a wakeup may happen but the condition might not actually be valid for all of them.
 */
class ProducerConsumerAIVersion {
    static Queue<Integer> queue = new LinkedList<>();
    static int val = 0;
    static final int CAPACITY = 5;

    public static void main(String[] args) throws InterruptedException {
        ProducerConsumerAIVersion pc = new ProducerConsumerAIVersion();

        Thread producer = new Thread(pc::producer);
        Thread consumer = new Thread(pc::consumer);
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }

    public synchronized void producer() {
        while (true) {
            while (queue.size() == CAPACITY) { // wait if full
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            int e = val++;
            queue.add(e);
            System.out.println("Produced : " + e);
            notifyAll();
            try {
                Thread.sleep(500); // sleep OUTSIDE critical logic
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public synchronized void consumer() {
        while (true) {
            while (queue.isEmpty()) { // wait if empty
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            int item = queue.poll();
            System.out.println("Consumed : " + item);
            notifyAll();
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
