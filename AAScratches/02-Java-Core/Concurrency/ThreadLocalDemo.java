/**
 * Demonstrates the use of ThreadLocal to store thread‑specific data.
 *
 * Problem: In a multithreaded environment, each task needs its own copy of an integer value
 * that should not be shared between threads. The code shows how to set and retrieve this
 * per‑thread value using ThreadLocal.
 *
 * Approach: Create a fixed thread pool and submit two tasks. Each task sets a unique
 * Integer into a static ThreadLocal instance, then calls a service method that reads
 * the value from the same ThreadLocal. Because each thread has its own copy of the
 * variable, the values printed by the service are independent.
 *
 * Time Complexity: O(n) where n is the number of tasks submitted (constant 2 here).
 * Space Complexity: O(t) for t threads in the pool; each holds one Integer in ThreadLocal storage. */
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

