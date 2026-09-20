/*
 * =====================================================================
 *  Stream a CSV and process rows on a thread pool  Java Core | IO | Medium  MUST-KNOW
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   The read side of every "process a huge file" question: one thread streams the file
 *   line by line with a BufferedReader so memory stays flat no matter how big the file
 *   is, and hands each record to a fixed thread pool. Shared counts live in atomics
 *   because many worker threads touch them at once.
 *
 * WHAT YOU WILL SEE
 *   header + 5 rows (amounts 10..50)     ->  rows=5 sum=150 errors=0
 *   header only, no data rows            ->  rows=0 sum=0   errors=0
 *   header + 2 good rows + 1 garbage row ->  rows=2 sum=30  errors=1
 *
 * HOW IT WORKS
 *   1. Create a fixed pool of N worker threads.
 *   2. Read the header line, then loop readLine() until null. Only ONE line is in
 *      memory at a time - that is what "streaming" buys you.
 *   3. Copy the line into a final local and submit a task that parses it and updates
 *      AtomicLong counters. Each task catches its own parse failure and counts it.
 *   4. shutdown() (stop accepting work), then awaitTermination with a real timeout;
 *      if it expires, shutdownNow() and fail loudly rather than report a bogus total.
 *
 * KEY INSIGHT
 *   The reader must stay single-threaded and sequential - a file has one cursor - while
 *   the per-record work fans out. The queue inside the ExecutorService is the seam
 *   between the two. That also means a slow consumer lets the unbounded queue grow
 *   without limit: at real scale you bound the queue so the reader blocks, which is the
 *   same back-pressure shape D01_CsvAppender needs on the write side.
 *
 * GOTCHAS
 *   - Fixed: the original opened a hard-coded relative "output.csv", printed a stack
 *     trace when it was missing, then reported "Finished reading 0 records" as if it
 *     had succeeded. It now builds its own sample and lets IOException propagate.
 *   - Fixed: THREAD_COUNT was 1, so the pool added a queue and no parallelism at all.
 *   - Fixed: submit() parks a task failure inside a Future nobody reads, so a throwing
 *     record vanished silently. Each task now catches and counts its own failure.
 *   - Do not collect one Future per line just to check for errors - that reintroduces
 *     the O(file size) memory the streaming reader was there to avoid.
 *   - awaitTermination(60, MINUTES) is not error handling; it is a hang with a long fuse.
 *
 * INTERVIEW FOLLOW-UPS
 *   - How do you stop the reader outrunning the workers? (bounded queue plus
 *     CallerRunsPolicy, or a Semaphore acquired per submitted task)
 *   - How would you preserve input order in the output? (tag each record with its index,
 *     or submit in batches and drain each batch of Futures in order)
 *   - Why not Files.readAllLines()? (whole file in heap; OOM on a real feed)
 *   - Why is split(",") wrong for real CSV? (quoted fields may contain commas)
 *   - When does adding threads stop helping? (when the disk, not the CPU, is the limit)
 *
 * RUN
 *   main() writes 3 sample CSVs into java.io.tmpdir, reads each on a 4-thread pool,
 *   prints actual vs expected, and deletes the files.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

class CsvReader {

    private static final int THREAD_COUNT = 4;
    private static final String HEADER = "id,name,amount";

    /** What the pool accumulated while streaming one file. */
    static class Stats {
        final long rows;
        final long sum;
        final long errors;

        Stats(long rows, long sum, long errors) {
            this.rows = rows;
            this.sum = sum;
            this.errors = errors;
        }

        @Override
        public String toString() {
            return "rows=" + rows + " sum=" + sum + " errors=" + errors;
        }
    }

    /**
     * Streams the file on the calling thread and processes each record on the pool.
     * Throws if the file is missing, so a failed read can never look like an empty file.
     */
    static Stats readCsv(Path file, int threadCount) throws IOException, InterruptedException {
        AtomicLong rows = new AtomicLong();
        AtomicLong sum = new AtomicLong();
        AtomicLong errors = new AtomicLong();

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            reader.readLine(); // header line, not a record
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                final String record = line; // a lambda can capture only an effectively-final local
                executor.submit(() -> processRecord(record, rows, sum, errors));
            }
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                throw new IllegalStateException("workers did not finish in time");
            }
        }
        return new Stats(rows.get(), sum.get(), errors.get());
    }

    /** Runs on a worker thread, so it has to report its own failure - nobody reads the Future. */
    private static void processRecord(String record, AtomicLong rows,
                                      AtomicLong sum, AtomicLong errors) {
        try {
            String[] fields = record.split(",");
            long amount = Long.parseLong(fields[2].trim());
            rows.incrementAndGet();
            sum.addAndGet(amount);
        } catch (RuntimeException badRecord) {
            errors.incrementAndGet();
        }
    }

    /** Writes a throwaway CSV (header plus the given rows) under java.io.tmpdir. */
    private static Path writeSampleCsv(String name, List<String> dataRows) throws IOException {
        Path file = Path.of(System.getProperty("java.io.tmpdir"), name);
        StringBuilder text = new StringBuilder(HEADER).append(System.lineSeparator());
        for (String row : dataRows) {
            text.append(row).append(System.lineSeparator());
        }
        Files.writeString(file, text.toString());
        file.toFile().deleteOnExit();
        return file;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) throws Exception {
        Path typical = writeSampleCsv("csvreader-typical.csv",
                List.of("1,alpha,10", "2,bravo,20", "3,charlie,30", "4,delta,40", "5,echo,50"));
        Path headerOnly = writeSampleCsv("csvreader-header-only.csv", List.of());
        Path withGarbage = writeSampleCsv("csvreader-garbage.csv",
                List.of("1,alpha,10", "oops", "2,bravo,20"));

        try {
            print("case 1 typical    ", readCsv(typical, THREAD_COUNT), "rows=5 sum=150 errors=0");
            print("case 2 header only", readCsv(headerOnly, THREAD_COUNT),
                    "rows=0 sum=0 errors=0");
            print("case 3 bad record ", readCsv(withGarbage, THREAD_COUNT),
                    "rows=2 sum=30 errors=1");
        } finally {
            Files.deleteIfExists(typical);
            Files.deleteIfExists(headerOnly);
            Files.deleteIfExists(withGarbage);
        }
    }
}
