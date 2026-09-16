/**
 * Problem: Given a list of badge scan times for employees (each entry is
 * [name, HHMM]), find the first contiguous 60‑minute window in which an employee
 * scans at least three times and output that employee’s name followed by the
 * formatted times of that window.
 *
 * Approach:
 * 1. Group all timestamps per employee and convert each HHMM string to minutes.
 * 2. For each employee, sort their times and slide a window checking differences
 *    ≤60 minutes; when a window contains ≥3 scans, record it and stop searching
 *    further for that employee.
 * 3. Print the results with times formatted back to HHMM.
 *
 * Time Complexity: O(n log n) – sorting each employee’s timestamps dominates,
 * where n is total number of badge entries.
 * Space Complexity: O(n) – storing all timestamps and intermediate lists.
 */
import java.util.*;

public class BadgeAccess {

    public static void main(String[] args) {
        String[][] badgeTimes = {
                {"Paul", "1355"}, {"Jennifer", "1910"}, {"Jose", "835"},
                {"Jose", "830"}, {"Paul", "1315"}, {"Chloe", "0"},
                {"Chloe", "1910"}, {"Jose", "1615"}, {"Jose", "1640"},
                {"Paul", "1405"}, {"Jose", "855"}, {"Jose", "930"},
                {"Jose", "915"}, {"Jose", "730"}, {"Jose", "940"},
                {"Jennifer", "1335"}, {"Jennifer", "730"}, {"Jose", "1630"},
                {"Jennifer", "5"}, {"Chloe", "1909"}, {"Zhang", "1"},
                {"Zhang", "10"}, {"Zhang", "109"}, {"Zhang", "110"},
                {"Amos", "1"}, {"Amos", "2"}, {"Amos", "400"},
                {"Amos", "500"}, {"Amos", "503"}, {"Amos", "504"},
                {"Amos", "601"}, {"Amos", "602"}, {"Paul", "1416"}
        };

        Map<String, List<Integer>> records = new HashMap<>();

        // Step 1: Group by employee
        for (String[] entry : badgeTimes) {
            String name = entry[0];
            int minutes = toMinutes(entry[1]);
            records.computeIfAbsent(name, k -> new ArrayList<>()).add(minutes);
        }

        // Step 2: Process each employee
        Map<String, List<String>> result = new HashMap<>();

        for (String name : records.keySet()) {
            List<Integer> times = records.get(name);
            Collections.sort(times);

            for (int i = 0; i < times.size(); i++) {
                List<Integer> window = new ArrayList<>();
                window.add(times.get(i));

                for (int j = i + 1; j < times.size(); j++) {
                    if (times.get(j) - times.get(i) <= 60) {
                        window.add(times.get(j));
                    } else {
                        break;
                    }
                }

                if (window.size() >= 3) {
                    List<String> formatted = new ArrayList<>();
                    for (int t : window) {
                        formatted.add(formatTime(t));
                    }
                    result.put(name, formatted);
                    break; // Only first valid window
                }
            }
        }

        // Step 3: Print result
        for (String name : result.keySet()) {
            System.out.println(name + ": " + String.join(" ", result.get(name)));
        }
    }

    // Convert "HHMM" string to total minutes since midnight
    private static int toMinutes(String timeStr) {
        int time = Integer.parseInt(timeStr);
        int hours = time / 100;
        int minutes = time % 100;
        return hours * 60 + minutes;
    }

    // Convert minutes back to "HHMM" format for printing
    private static String formatTime(int totalMinutes) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        return String.format("%02d%02d", hours, minutes);
    }
}
