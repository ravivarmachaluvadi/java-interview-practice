/*
 * =====================================================================
 *  Immutable object + AtomicReference CAS retry      Java core | Hard   MUST-KNOW
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   How five threads concurrently "add an address" to shared state without a lock and
 *   without losing an update. The state itself (ImmutablePerson) is immutable, so an
 *   update means building a new instance and swapping one reference atomically with
 *   compareAndSet, retrying if someone else swapped first. This is the copy-on-write /
 *   optimistic-concurrency pattern used by CopyOnWriteArrayList and by every
 *   version-column update in a database.
 *
 * WHAT YOU WILL SEE
 *   case 1  immutability: withAddedAddress returns a NEW person; the original is unchanged.
 *   case 2  defensive copies: mutating the list you passed in, or the list you got back,
 *           cannot reach the object's state.
 *   case 3  five threads, no locks: all five addresses survive (6 total with the seed).
 *   case 4  the contrast - the same read-modify-write on a plain field loses updates.
 *
 * HOW IT WORKS
 *   1. The constructor copies the incoming list and wraps it unmodifiable, so no caller
 *      keeps a handle on the internal list.
 *   2. withAddedAddress copies the addresses, appends one, and returns a new instance.
 *      Nothing is ever mutated in place.
 *   3. Each worker loops: read the current reference, build the successor, then
 *      compareAndSet(expected = what I read, new = what I built). If another thread swapped
 *      in between, CAS returns false and the loop re-reads and rebuilds on the newer value.
 *   4. The pool is shut down and joined with awaitTermination, so the demo always ends.
 *
 * KEY INSIGHT
 *   CAS turns a read-modify-write into an atomic "publish only if nothing changed". That
 *   only works because the value being published is immutable: a rival thread that read
 *   the old snapshot cannot be corrupted by your update, it simply loses the race and
 *   retries. Immutability is what makes lock-free retry correct, not just fast. The cost
 *   is that contention shows up as wasted retries, so it suits low-to-medium contention;
 *   under heavy contention a lock or an accumulator wins.
 *
 * GOTCHAS
 *   - Fixed: the old version spun on isTerminated() in a busy-wait loop, burning a core;
 *     awaitTermination with a timeout is the correct join and bounds the demo.
 *   - Without the retry loop, a failed CAS silently drops the update (case 4).
 *   - compareAndSet compares by REFERENCE identity, not equals. An immutable value type
 *     with a value-based equals would make the ABA problem visible.
 *   - Collections.unmodifiableList wraps, it does not copy: without the new ArrayList<>
 *     around it, the caller's list would still be a live back door.
 *   - "final field" is not immutability. A final List field can still be mutated; only the
 *     copy-in and copy-out make it safe.
 *
 * INTERVIEW FOLLOW-UPS
 *   - When does this beat synchronized, and when does it lose? (contention level, length
 *     of the critical section, size of the copied state)
 *   - What is the ABA problem and how does AtomicStampedReference fix it?
 *   - How is this the same idea as an optimistic @Version column in JPA?
 *   - Would a Java record give you this for free? (shallow immutability only - the list
 *     component still needs the defensive copy)
 *
 * RUN
 *   main() runs 4 cases (immutability, defensive copies, lock-free concurrent updates, the
 *   lost-update contrast) and prints actual vs expected. It finishes in well under a second.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/** Immutable value: final class, final fields, copy on the way in and on the way out. */
final class ImmutablePerson {

    private final String name;
    private final List<String> addresses;

    public ImmutablePerson(String name, List<String> addresses) {
        this.name = name;
        // Copy first, then wrap: unmodifiableList alone would still see the caller's edits.
        this.addresses = Collections.unmodifiableList(new ArrayList<>(addresses));
    }

    public String getName() {
        return name;
    }

    public List<String> getAddresses() {
        return List.copyOf(addresses);     // callers cannot mutate what they get back
    }

    /** Returns a NEW person; this instance is never touched. */
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

    private static final int WORKERS = 5;

    /** A deliberately unsafe holder used in case 4 to show what CAS is protecting you from. */
    private static final class UnsafeHolder {
        private ImmutablePerson person;

        UnsafeHolder(ImmutablePerson person) {
            this.person = person;
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    /** Sorted copy so the assertions do not depend on thread scheduling order. */
    private static List<String> sortedAddresses(ImmutablePerson person) {
        List<String> copy = new ArrayList<>(person.getAddresses());
        Collections.sort(copy);
        return copy;
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> initialAddresses = new ArrayList<>();
        initialAddresses.add("New York, NY");
        ImmutablePerson initialPerson = new ImmutablePerson("John Doe", initialAddresses);

        // ---- case 1: an "update" produces a new object ------------------------------
        ImmutablePerson updated = initialPerson.withAddedAddress("Austin, TX");
        print("case 1 original", initialPerson.getAddresses(), "[New York, NY]");
        print("case 1 updated", updated.getAddresses(), "[New York, NY, Austin, TX]");
        print("case 1 different objects", initialPerson != updated, true);

        // ---- case 2: the defensive copies actually hold -------------------------------
        initialAddresses.add("Sneaky, XX");            // mutate the list we passed in
        print("case 2 after caller mutates input", initialPerson.getAddresses(),
                "[New York, NY]");
        String mutationResult;
        try {
            initialPerson.getAddresses().add("Sneaky, XX");   // mutate what we got back
            mutationResult = "mutated";
        } catch (UnsupportedOperationException e) {
            mutationResult = "UnsupportedOperationException";
        }
        print("case 2 returned list is immutable", mutationResult,
                "UnsupportedOperationException");

        // ---- case 3: five threads, lock-free, nothing lost ---------------------------
        AtomicReference<ImmutablePerson> personRef = new AtomicReference<>(initialPerson);
        AtomicInteger retries = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(WORKERS);
        CountDownLatch startSignal = new CountDownLatch(1);

        for (int i = 0; i < WORKERS; i++) {
            int workerId = i;
            pool.submit(() -> {
                awaitQuietly(startSignal);                 // make them collide on purpose
                while (true) {
                    ImmutablePerson current = personRef.get();                 // read
                    ImmutablePerson next = current.withAddedAddress("City " + workerId);
                    if (personRef.compareAndSet(current, next)) {              // publish
                        return;
                    }
                    retries.incrementAndGet();             // someone beat us; rebuild
                }
            });
        }
        startSignal.countDown();
        pool.shutdown();
        boolean finished = pool.awaitTermination(2, TimeUnit.SECONDS);

        print("case 3 pool finished", finished, true);
        print("case 3 address count", personRef.get().getAddresses().size(), 6);
        print("case 3 addresses", sortedAddresses(personRef.get()),
                "[City 0, City 1, City 2, City 3, City 4, New York, NY]");
        System.out.println("case 3 CAS retries observed: " + retries.get()
                + "   (any number >= 0; 0 means the threads never collided)");

        // ---- case 4: the same work without CAS loses updates -------------------------
        UnsafeHolder unsafe = new UnsafeHolder(initialPerson);
        ExecutorService unsafePool = Executors.newFixedThreadPool(WORKERS);
        CountDownLatch unsafeStart = new CountDownLatch(1);

        for (int i = 0; i < WORKERS; i++) {
            int workerId = i;
            unsafePool.submit(() -> {
                awaitQuietly(unsafeStart);
                ImmutablePerson current = unsafe.person;              // read
                sleepQuietly(50);                                     // widen the window
                unsafe.person = current.withAddedAddress("City " + workerId);   // write
            });
        }
        unsafeStart.countDown();
        unsafePool.shutdown();
        boolean unsafeFinished = unsafePool.awaitTermination(2, TimeUnit.SECONDS);

        int unsafeCount = unsafe.person.getAddresses().size();
        print("case 4 pool finished", unsafeFinished, true);
        print("case 4 updates were lost", unsafeCount < 6, true);
        System.out.println("case 4 address count: " + unsafeCount
                + "   expected fewer than 6 (CAS-free read-modify-write drops updates)");
    }

    private static void awaitQuietly(CountDownLatch latch) {
        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
