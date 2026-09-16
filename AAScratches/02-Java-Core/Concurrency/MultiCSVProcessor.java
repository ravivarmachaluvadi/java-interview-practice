import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

class MultiCSVProcessor {

    private static final int THREAD_COUNT = 4;
    private static final ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

    public static void main(String[] args) throws Exception {
        // Folder containing multiple CSV files
        Path folder = Paths.get("data/"); // e.g. data/file1.csv, data/file2.csv, etc.

        // Collect all CSV file paths
        List<Path> csvFiles;
        try (Stream<Path> stream = Files.list(folder)) {
            csvFiles = stream
                    .filter(path -> path.toString().endsWith(".csv"))
                    .collect(Collectors.toList());
        }

        System.out.println("Found CSV files: " + csvFiles);

        // Process each CSV file asynchronously
        List<CompletableFuture<FileStats>> futures = csvFiles.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> processCSV(file), executor))
                .toList();

        // Wait for all results and combine
        List<FileStats> allStats = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        // Merge all stats
        FileStats globalStats = mergeStats(allStats);

        System.out.println("\n=== Combined Statistics ===");
        System.out.println("Total Rows: " + globalStats.totalRows);
        System.out.println("Average Salary: " + globalStats.getAverageSalary());
        System.out.println("Min Salary: " + globalStats.minSalary);
        System.out.println("Max Salary: " + globalStats.maxSalary);

        executor.shutdown();
    }

    private static FileStats processCSV(Path path) {
        System.out.println("Processing: " + path.getFileName() + " on thread " + Thread.currentThread().getName());

        FileStats stats = new FileStats();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    double salary = Double.parseDouble(parts[2]);
                    stats.totalRows++;
                    stats.totalSalary += salary;
                    stats.minSalary = Math.min(stats.minSalary, salary);
                    stats.maxSalary = Math.max(stats.maxSalary, salary);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Finished: " + path.getFileName());
        return stats;
    }

    private static FileStats mergeStats(List<FileStats> statsList) {
        FileStats result = new FileStats();
        for (FileStats s : statsList) {
            result.totalRows += s.totalRows;
            result.totalSalary += s.totalSalary;
            result.minSalary = Math.min(result.minSalary, s.minSalary);
            result.maxSalary = Math.max(result.maxSalary, s.maxSalary);
        }
        return result;
    }

    static class FileStats {
        long totalRows = 0;
        double totalSalary = 0.0;
        double minSalary = Double.MAX_VALUE;
        double maxSalary = Double.MIN_VALUE;

        double getAverageSalary() {
            return totalRows == 0 ? 0.0 : totalSalary / totalRows;
        }
    }
}
