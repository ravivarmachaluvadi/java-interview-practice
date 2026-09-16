/**
 * Problem:
 * Demonstrates thread-safe updates to an immutable object (ImmutablePerson) using
 * AtomicReference and compareAndSet, ensuring each concurrent task adds a new address
 * without corrupting shared state.
 *
 * Approach:
 * 1. Wrap the current ImmutablePerson in an AtomicReference.
 * 2. Each worker thread creates a new instance with an added address.
 * 3. Use compareAndSet to atomically replace the reference; retry on failure.
 *
 * Complexity:
 * Time: O(n * m) where n is number of threads and m is average list size per update
 * Space: O(m) for each new ImmutablePerson copy (list duplication).
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

// Immutable Person class with secondary list
final class ImmutablePerson {

    private final String name;
    private final List<String> addresses;

    public ImmutablePerson(String name, List<String> addresses) {
        this.name = name;
        this.addresses = Collections.unmodifiableList(new ArrayList<>(addresses)); // Defensive Copy
    }

    public String getName() {
        return name;
    }

    public List<String> getAddresses() {
        return List.copyOf(addresses);
    }

    // Return a new ImmutablePerson object with the updated address
    public ImmutablePerson withAddedAddress(String newAddress) {
        List<String> updatedAddresses = new ArrayList<>(this.addresses);
        updatedAddresses.add(newAddress);
        return new ImmutablePerson(this.name, updatedAddresses);
    }

    @Override
    public String toString() {
        return "ImmutablePerson{name='" + name + "', addresses=" + addresses + '}';
    }
}

class ImmutablePersonExample {
    public static void main(String[] args) {
        // Initial address list
        List<String> initialAddresses = new ArrayList<>();
        initialAddresses.add("New York, NY");

        // Create the initial immutable person object
        ImmutablePerson initialPerson = new ImmutablePerson("John Doe", initialAddresses);

        // AtomicReference to hold the latest immutable instance
        AtomicReference<ImmutablePerson> personRef = new AtomicReference<>(initialPerson);

        // Create an ExecutorService to handle concurrent threads
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        // Each thread will add a new address incrementally
        for (int i = 0; i < 5; i++) {
            int threadId = i;
            executorService.submit(() -> {
                // Retry loop to ensure that the update is applied correctly
                while (true) {
                    ImmutablePerson currentPerson = personRef.get(); // Get the current state
                    // Create a new instance with the additional address
                    ImmutablePerson modifiedPerson = currentPerson.withAddedAddress("City " + threadId);

                    // Atomically update the reference to the new instance
                    if (personRef.compareAndSet(currentPerson, modifiedPerson)) {
                        System.out.println(Thread.currentThread() + " " + threadId + " added address: " + modifiedPerson);
                        break; // Exit the loop on successful update
                    }
                }
            });
        }

        // Shutdown the executor service
        executorService.shutdown();

        // Wait for all tasks to complete
        while (!executorService.isTerminated()) {
            // Busy wait
        }

        // Print the final state of the person object
        System.out.println("Final Person: " + personRef.get());
    }
}
