import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ThreadLocalDemo {
    static ExecutorService executorService = Executors.newFixedThreadPool(10);
    static Service service = new Service();

    public static void main(String[] args) {
        for (int i = 0; i < 2; i++) {
            int finalI = i;
            executorService.execute(() -> {
                System.out.println("Before Setting : " + Thread.currentThread().getName() + " " + finalI);
                IntHolders.local.set(finalI);
                service.printVal();
            });
        }
    }
}

class Service {
    void printVal() {
        System.out.println("Service : " + Thread.currentThread().getName() + " " + IntHolders.local.get());
    }
}

class IntHolders {
    static ThreadLocal<Integer> local = new ThreadLocal<>();
}

