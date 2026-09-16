import java.util.LinkedList;
import java.util.Queue;

class ProducerConsumer {

    static Queue<Integer> queue = new LinkedList<>();
    static int val = 0;

    public static void main(String[] args) throws InterruptedException {
        ProducerConsumer producerConsumer = new ProducerConsumer();

        Thread producer = new Thread(producerConsumer::producer);
        Thread consumer = new Thread(producerConsumer::consumer);
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();

    }

    public synchronized void producer() {
        while (true) {
            if (queue.isEmpty()) {
                int e = val++;
                System.out.println("Produced : " + e);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                queue.add(e);
                notifyAll();
            } else {
                try {
                    // wait also should be inside while loop
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public synchronized void consumer() {
        while (true) {
            if (!queue.isEmpty()) {
                System.out.println("Consumed : " + queue.poll());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                notifyAll();
            } else {
                try {
                    // wait also should be inside while loop
                    wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
