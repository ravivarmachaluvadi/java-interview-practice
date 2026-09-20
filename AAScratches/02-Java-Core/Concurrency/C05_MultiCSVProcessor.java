/*
 * =====================================================================
 *  Aggregate stats across many CSV files            Concurrency | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A folder holds several CSV files with the columns id,name,salary and a header row.
 *   Compute combined statistics over all of them: total data rows, average salary, minimum
 *   salary and maximum salary. Files are independent, so parse them in parallel on a
 *   bounded pool.
 *
 * EXAMPLE
 *   a.csv = 100, 200 | b.csv = 300, 400 | c.csv = 500 -> rows=5 avg=300.0 min=100 max=500
 *   header-only file + one row of 250 -> rows=1 avg=250.0 (empty file must not skew min/max)
 *   corrections file of -50, -10      -> rows=2 avg=-30.0 min=-50.0 max=-10.0
 *
 * APPROACH  (CompletableFuture fan-out / fan-in)
 *   1. List the .csv files in the folder.
 *   2. Fan out: one supplyAsync(() -> parseOneFile(f), pool) per file. Each task walks its
 *      own file and returns a private FileStats, so no task ever writes shared state.
 *   3. Fan in: join() every future, then fold the per-file FileStats into one with merge().
 *   4. Shut the pool down in a finally block so the JVM can exit.
 *
 * KEY INSIGHT
 *   Parallel aggregation is safe when the per-task result is a value you can FOLD, and the
 *   fold has a neutral element. FileStats starts at rows=0, sum=0, min=+INF, max=-INF, so an
 *   empty file merges in without changing anything. Pick the identity first, then the merge
 *   function - that is what makes "split the work, combine the answers" provably correct.
 *
 *   Fixed: maxSalary started at Double.MIN_VALUE, which is the smallest POSITIVE double
 *   (4.9E-324), not the most negative one. Any all-negative file reported max = 4.9E-324.
 *   The neutral element for max is Double.NEGATIVE_INFINITY.
 *
 * COMPLEXITY
 *   Time  O(R)  every row is parsed once; wall clock ~ O(R / min(threads, files)) because
 *               the split is per file, not per row
 *   Space O(F)  one FileStats and one future per file; each file streams line by line
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why supplyAsync(task, pool) and not supplyAsync(task)? (the common ForkJoinPool is
 *     sized for CPU work and must not be blocked on file IO)
 *   - One file is corrupt: fail the whole job or skip that file? (exceptionally / handle
 *     per future, or allOf plus a collected failure list)
 *   - One file is 50 GB: how do you split WITHIN a file? (byte ranges, align to line breaks)
 *   - Replace all of this with files.parallelStream() - what do you lose? (pool control)
 *
 * RUN
 *   main() builds 3 temp folders under java.io.tmpdir (typical, header-only, negatives), runs
 *   the pipeline on each, prints actual vs expected, then deletes them.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class MultiCSVProcessor {

    private static final int THREAD_COUNT = 4;

    /** Fan out one task per CSV file, then fold the per-file results into one total. */
    static FileStats aggregateFolder(Path folder, ExecutorService pool) throws IOException {
        List<Path> csvFiles;
        try (Stream<Path> stream = Files.list(folder)) {
            csvFiles = stream.filter(p -> p.toString().endsWith(".csv"))
                             .sorted()                 // stable order so output is repeatable
                             .collect(Collectors.toList());
        }

        List<CompletableFuture<FileStats>> futures = csvFiles.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> parseOneFile(file), pool))
                .collect(Collectors.toList());

        FileStats total = new FileStats();
        for (CompletableFuture<FileStats> f : futures) {
            total = merge(total, f.join());           // join re-throws the task's failure here
        }
        return total;
    }

    /** Runs on a pool thread. Touches only its own file and its own FileStats. */
    private static FileStats parseOneFile(Path path) {
        FileStats stats = new FileStats();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            reader.readLine();                        // drop the header row
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    continue;                         // malformed row: skip, do not count it
                }
                stats.accept(Double.parseDouble(parts[2]));
            }
        } catch (IOException e) {
            // Wrap, do not swallow: an unchecked throw here surfaces at join() as
            // CompletionException, so a broken file fails the job instead of silently
            // reporting a smaller total.
            throw new UncheckedIOException(e);
        }
        return stats;
    }

    /** The fold. Associative, and a fresh FileStats is its identity element. */
    private static FileStats merge(FileStats a, FileStats b) {
        FileStats out = new FileStats();
        out.totalRows = a.totalRows + b.totalRows;
        out.totalSalary = a.totalSalary + b.totalSalary;
        out.minSalary = Math.min(a.minSalary, b.minSalary);
        out.maxSalary = Math.max(a.maxSalary, b.maxSalary);
        return out;
    }

    static class FileStats {
        long totalRows = 0;
        double totalSalary = 0.0;
        double minSalary = Double.POSITIVE_INFINITY;  // identity for min
        double maxSalary = Double.NEGATIVE_INFINITY;  // identity for max (see Fixed: above)

        void accept(double salary) {
            totalRows++;
            totalSalary += salary;
            minSalary = Math.min(minSalary, salary);
            maxSalary = Math.max(maxSalary, salary);
        }

        double averageSalary() {
            return totalRows == 0 ? 0.0 : totalSalary / totalRows;
        }

        @Override
        public String toString() {
            if (totalRows == 0) {
                return "rows=0 avg=0.0 min=n/a max=n/a";
            }
            return "rows=" + totalRows + " avg=" + averageSalary()
                    + " min=" + minSalary + " max=" + maxSalary;
        }
    }

    // ----------------------------------------------------------------- demo scaffolding

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Path> tempFolders = new ArrayList<>();
        try {
            Path typical = newFolder(tempFolders, "csv-typical");
            writeCsv(typical, "a.csv", "1,Ann,100", "2,Bob,200");
            writeCsv(typical, "b.csv", "3,Cid,300", "4,Dee,400");
            writeCsv(typical, "c.csv", "5,Eve,500");
            print("case 1 (3 files)   ", aggregateFolder(typical, pool),
                    "rows=5 avg=300.0 min=100.0 max=500.0");

            Path edge = newFolder(tempFolders, "csv-edge");
            writeCsv(edge, "empty.csv");                         // header only, zero data rows
            writeCsv(edge, "one.csv", "7,Gil,250");
            print("case 2 (empty file)", aggregateFolder(edge, pool),
                    "rows=1 avg=250.0 min=250.0 max=250.0");

            Path negative = newFolder(tempFolders, "csv-negative");
            writeCsv(negative, "corrections.csv", "8,Hal,-50", "9,Ivy,-10");
            print("case 3 (negatives) ", aggregateFolder(negative, pool),
                    "rows=2 avg=-30.0 min=-50.0 max=-10.0");
        } finally {
            pool.shutdown();
            pool.awaitTermination(2, TimeUnit.SECONDS);
            for (Path folder : tempFolders) {
                deleteRecursively(folder);
            }
        }
    }

    private static void print(String label, Object actual, String expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    private static Path newFolder(List<Path> registry, String prefix) throws IOException {
        Path folder = Files.createTempDirectory(prefix);
        registry.add(folder);
        return folder;
    }

    private static void writeCsv(Path folder, String name, String... rows) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("id,name,salary");
        lines.addAll(List.of(rows));
        Files.write(folder.resolve(name), lines);
    }

    private static void deleteRecursively(Path root) throws IOException {
        try (Stream<Path> walk = Files.walk(root)) {
            for (Path p : walk.sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
                Files.deleteIfExists(p);
            }
        }
    }
}
