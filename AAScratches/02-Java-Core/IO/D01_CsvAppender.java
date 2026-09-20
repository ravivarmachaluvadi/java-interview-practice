/*
 * =====================================================================
 *  Append records to one CSV from many threads     Java Core | IO | Hard
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   The write side of the big-file question, and why the obvious answer is wrong.
 *   Approach 1 is the code most people write: N worker threads, a global ReentrantLock,
 *   and a FileWriter opened in append mode for every single record. It is correct but
 *   it is not concurrent - the lock serialises everything, and the per-record open and
 *   close is a syscall pair plus a metadata update each time.
 *   Approach 2 is the answer an interviewer is looking for: producers hand records to a
 *   bounded queue, ONE writer thread owns the file and streams through a BufferedWriter.
 *
 * WHAT YOU WILL SEE
 *   5000 records, 10 threads, global lock    ->  5000 lines, every id 0..4999 present
 *   5000 records, 10 threads, single writer  ->  5000 lines, every id 0..4999 present,
 *                                                and far fewer milliseconds
 *   0 records (edge)                         ->  0 lines, file created and empty
 *   same file written twice (rerun)          ->  5000 lines, not 10000
 *
 * HOW IT WORKS
 *   Approach 1 - appendWithGlobalLock
 *     1. Fixed pool of 10 threads, one task per record.
 *     2. Each task builds its record, takes the lock, opens FileWriter(file, append),
 *        writes one line, closes, releases the lock.
 *     3. The lock is what makes the output non-interleaved; it is also the bottleneck.
 *   Approach 2 - appendWithSingleWriter
 *     1. One writer thread loops take() on an ArrayBlockingQueue and writes each record
 *        through a BufferedWriter, so the OS sees a few big writes, not 5000 small ones.
 *     2. Producers put() into the bounded queue; when it is full they block. That is
 *        back-pressure, and it is why memory stays flat however fast producers are.
 *     3. After the pool drains, a poison-pill record tells the writer to stop, and the
 *        main thread joins it with a timeout so the demo can never hang.
 *
 * KEY INSIGHT
 *   A file is a single append cursor, so writes are serialised no matter what you do.
 *   The choice is only WHERE you pay for it. A global lock pays per record on every
 *   worker (open, seek, write, close, contend). A single writer pays once: the workers
 *   never touch the file, they hand off a message and go back to work. Whenever shared
 *   mutable state has exactly one natural owner, funnel work to that owner through a
 *   bounded queue instead of guarding the state with a lock everyone fights over.
 *
 * GOTCHAS
 *   - Fixed: the original never truncated, so re-running it appended to the previous
 *     run and silently doubled the data. Each run here starts from a deleted file.
 *   - Fixed: elapsed time was printed as (end - start) / 1000 in whole seconds, so any
 *     run under a second reported "0 seconds". It now reports milliseconds.
 *   - Fixed: 2,000,000 records with a per-record open and close does not finish in demo
 *     time. RECORD_COUNT is 5000 here; the shape of the answer is identical.
 *   - Fixed: the original wrote a relative "output.csv" into whatever directory you
 *     happened to run from. It now writes under java.io.tmpdir and deletes after.
 *   - The original never verified anything - it only printed that it had finished.
 *     Both approaches here are checked for line count AND for every id landing once.
 *   - BufferedWriter is not thread-safe; it is safe here only because exactly one
 *     thread ever touches it.
 *
 * INTERVIEW FOLLOW-UPS
 *   - What if the writer thread dies mid-run? (producers block on a full queue forever
 *     unless you use offer with a timeout, or a poison pill on the failure path)
 *   - How do you make the append durable across a crash? (flush plus FileChannel.force,
 *     and accept the throughput cost; or write-ahead-log style fsync batching)
 *   - Multiple processes appending to one file: what does O_APPEND guarantee, and up to
 *     what write size?
 *   - How would you shard instead - one file per writer, merged later? What do you lose?
 *   - Why is FileWriter(file, true) per record so expensive even without the lock?
 *
 * RUN
 *   main() runs both approaches over 5000 records into java.io.tmpdir, plus an empty
 *   run and a rerun-truncation check, printing actual vs expected, then deletes the files.
 */
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

class CsvAppender {

    private static final int THREAD_COUNT = 10;
    private static final int RECORD_COUNT = 5000;

    /** Guards the file in approach 1: only one thread may hold the append cursor. */
    private static final ReentrantLock lock = new ReentrantLock();

    /** Sentinel that tells the single writer thread there is nothing more coming. */
    /** Compared by identity (==), so no real record can ever be mistaken for it. */
    private static final String POISON_PILL = new String("END-OF-RECORDS");

    private static String generateRecord(int recordNumber) {
        // Three columns for simplicity: ID, Column1, Column2.
        return recordNumber + ",value1_" + recordNumber + ",value2_" + recordNumber;
    }

    // ---------------------------------------------------------------- approach 1

    /** The author's approach: every worker opens the file itself, under one global lock. */
    static long appendWithGlobalLock(Path file, int records, int threads) throws Exception {
        Files.deleteIfExists(file); // start clean, or a rerun appends to the last run
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        long start = System.currentTimeMillis();
        for (int i = 0; i < records; i++) {
            final int recordNumber = i;
            executor.submit(() -> appendToCsv(file, generateRecord(recordNumber)));
        }
        shutdownAndWait(executor);
        if (records == 0) {
            Files.createFile(file); // no task ran, so nothing created it
        }
        return System.currentTimeMillis() - start;
    }

    private static void appendToCsv(Path file, String record) {
        lock.lock();
        // Opening in append mode inside the lock: correct, and the expensive part.
        try (FileWriter writer = new FileWriter(file.toFile(), true)) {
            writer.write(record);
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            lock.unlock();
        }
    }

    // ---------------------------------------------------------------- approach 2

    /** One writer thread owns the file; producers only hand records to a bounded queue. */
    static long appendWithSingleWriter(Path file, int records, int threads) throws Exception {
        Files.deleteIfExists(file);
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(1024); // bounded => back-pressure

        Thread writerThread = new Thread(() -> drainQueueToFile(file, queue), "csv-writer");
        writerThread.setDaemon(true); // never keep the JVM alive if something goes wrong
        writerThread.start();

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        long start = System.currentTimeMillis();
        for (int i = 0; i < records; i++) {
            final int recordNumber = i;
            executor.submit(() -> {
                try {
                    queue.put(generateRecord(recordNumber)); // blocks when the queue is full
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        shutdownAndWait(executor);

        queue.put(POISON_PILL);          // only safe once every producer has finished
        writerThread.join(5000);
        if (writerThread.isAlive()) {
            throw new IllegalStateException("writer thread did not stop");
        }
        return System.currentTimeMillis() - start;
    }

    /** Runs on the single writer thread: one open file, one buffer, batched physical writes. */
    private static void drainQueueToFile(Path file, BlockingQueue<String> queue) {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            while (true) {
                String record = queue.take();
                if (record == POISON_PILL) { // identity, not equals: a sentinel, not data
                    return; // try-with-resources flushes and closes on the way out
                }
                writer.write(record);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ---------------------------------------------------------------- helpers

    private static void shutdownAndWait(ExecutorService executor) throws InterruptedException {
        executor.shutdown();
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            executor.shutdownNow();
            throw new IllegalStateException("producers did not finish in time");
        }
    }

    /** "lines=5000 allIds=true" - proves nothing was lost, duplicated or interleaved. */
    private static String verify(Path file, int expectedRecords) throws IOException {
        boolean[] seen = new boolean[expectedRecords];
        long lines = 0;
        boolean wellFormed = true;
        try (Stream<String> stream = Files.lines(file)) {
            for (String line : (Iterable<String>) stream::iterator) {
                lines++;
                String[] fields = line.split(",");
                int id = Integer.parseInt(fields[0]);
                if (fields.length != 3 || id < 0 || id >= expectedRecords || seen[id]) {
                    wellFormed = false;
                } else {
                    seen[id] = true;
                }
            }
        }
        for (boolean present : seen) {
            wellFormed &= present;
        }
        return "lines=" + lines + " allIds=" + wellFormed;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) throws Exception {
        Path lockFile = Path.of(System.getProperty("java.io.tmpdir"), "csvappender-lock.csv");
        Path queueFile = Path.of(System.getProperty("java.io.tmpdir"), "csvappender-queue.csv");
        Path emptyFile = Path.of(System.getProperty("java.io.tmpdir"), "csvappender-empty.csv");
        String expected = "lines=" + RECORD_COUNT + " allIds=true";

        try {
            long lockMs = appendWithGlobalLock(lockFile, RECORD_COUNT, THREAD_COUNT);
            print("case 1 global lock    ", verify(lockFile, RECORD_COUNT), expected);

            long queueMs = appendWithSingleWriter(queueFile, RECORD_COUNT, THREAD_COUNT);
            print("case 2 single writer  ", verify(queueFile, RECORD_COUNT), expected);

            appendWithSingleWriter(queueFile, RECORD_COUNT, THREAD_COUNT);
            print("case 3 rerun same file", verify(queueFile, RECORD_COUNT), expected);

            appendWithGlobalLock(emptyFile, 0, THREAD_COUNT);
            print("case 4 zero records   ", verify(emptyFile, 0), "lines=0 allIds=true");

            System.out.println("timing (varies per machine): global lock " + lockMs
                    + " ms, single writer " + queueMs + " ms for " + RECORD_COUNT + " records");
        } finally {
            Files.deleteIfExists(lockFile);
            Files.deleteIfExists(queueFile);
            Files.deleteIfExists(emptyFile);
        }
    }
}
