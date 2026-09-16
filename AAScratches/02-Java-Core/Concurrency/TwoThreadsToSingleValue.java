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
