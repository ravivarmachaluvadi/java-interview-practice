import java.util.*;

/**
 * Problem: Employees badge in at times given as [name, "HHMM"]. Find every employee
 *          who badged in 3 or more times within a single one-hour window.
 *          LeetCode 2933 High-Access Employees is the LeetCode form of the classic
 *          Karat "badge access" question; the two forms differ only in output and in
 *          whether the hour is open (<60) or closed (<=60).
 *
 * Approaches:
 *  1. leetcodeNamesOnly  - sort per employee, then check times[i] - times[i-2] < 60.
 *                          Three sorted scans fit in an open hour iff the outer two do,
 *                          so one subtraction per index is enough. O(n log n), returns names.
 *  2. karatFirstWindow   - sort per employee, then from each scan i grow a window of
 *                          scans within 60 min (inclusive). The first window with >= 3
 *                          scans is reported with its times formatted back to HHMM.
 *                          O(n log n + n*k), returns name -> window times.
 *
 * Shared helpers: toMinutes (parse "HHMM", also tolerates "835", "5", "0" since the
 * Karat input is not zero-padded) and formatTime (minutes -> "HHMM").
 */
class HighAccessEmployees {

    // ---------- Approach 1: LeetCode 2933 (names only, strict < 60) ----------
    public List<String> leetcodeNamesOnly(List<List<String>> accessTimes) {
        Map<String, List<Integer>> byEmployee = groupByEmployee(accessTimes);

        List<String> result = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : byEmployee.entrySet()) {
            List<Integer> times = entry.getValue();
            if (times.size() < 3) continue;
            Collections.sort(times);
            // Sorted, so times[i-2] <= times[i-1] <= times[i]: if the outer pair is
            // under an hour apart, all three scans share the same open hour.
            for (int i = 2; i < times.size(); i++) {
                if (times.get(i) - times.get(i - 2) < 60) {
                    result.add(entry.getKey());
                    break;
                }
            }
        }
        return result;
    }

    // ---------- Approach 2: Karat form (first window with its times, inclusive <= 60) ----------
    public Map<String, List<String>> karatFirstWindow(List<List<String>> accessTimes) {
        Map<String, List<Integer>> byEmployee = groupByEmployee(accessTimes);

        Map<String, List<String>> result = new TreeMap<>(); // sorted keys for stable output
        for (Map.Entry<String, List<Integer>> entry : byEmployee.entrySet()) {
            List<Integer> times = entry.getValue();
            Collections.sort(times);

            for (int i = 0; i < times.size(); i++) {
                // Grow the window anchored at scan i: keep adding later scans while
                // they are still within 60 minutes of the anchor.
                List<Integer> window = new ArrayList<>();
                window.add(times.get(i));
                for (int j = i + 1; j < times.size(); j++) {
                    if (times.get(j) - times.get(i) <= 60) {
                        window.add(times.get(j));
                    } else {
                        break; // sorted, so nothing later can fit either
                    }
                }

                if (window.size() >= 3) {
                    List<String> formatted = new ArrayList<>();
                    for (int t : window) formatted.add(formatTime(t));
                    result.put(entry.getKey(), formatted);
                    break; // only the first valid window per employee
                }
            }
        }
        return result;
    }

    // ---------- Shared helpers ----------

    // Group scan times per employee, converted to minutes since midnight.
    private static Map<String, List<Integer>> groupByEmployee(List<List<String>> accessTimes) {
        Map<String, List<Integer>> byEmployee = new HashMap<>();
        for (List<String> rec : accessTimes) {
            byEmployee.computeIfAbsent(rec.get(0), k -> new ArrayList<>())
                      .add(toMinutes(rec.get(1)));
        }
        return byEmployee;
    }

    // "HHMM" -> minutes. Integer parse (not substring) so "835", "5" and "0" also work.
    private static int toMinutes(String timeStr) {
        int time = Integer.parseInt(timeStr);
        return (time / 100) * 60 + (time % 100);
    }

    // minutes -> zero-padded "HHMM" for printing.
    private static String formatTime(int totalMinutes) {
        return String.format("%02d%02d", totalMinutes / 60, totalMinutes % 60);
    }

    private static List<List<String>> toList(String[][] rows) {
        List<List<String>> out = new ArrayList<>();
        for (String[] r : rows) out.add(Arrays.asList(r));
        return out;
    }

    public static void main(String[] args) {
        HighAccessEmployees sol = new HighAccessEmployees();

        // LeetCode example 1: expected names ["a"]
        List<List<String>> lc1 = toList(new String[][]{
                {"a", "0549"}, {"b", "0457"}, {"a", "0532"}, {"a", "0621"}, {"b", "0540"}
        });
        // LeetCode example 2: expected names ["c","d"]
        List<List<String>> lc2 = toList(new String[][]{
                {"d", "0002"}, {"c", "0808"}, {"c", "0829"}, {"e", "0215"},
                {"d", "1508"}, {"d", "1444"}, {"d", "1410"}, {"c", "0809"}
        });
        // Karat example. Zhang (0010, 0109, 0110 = exactly 60 min apart) shows the spec gap:
        // excluded by the strict <60 LeetCode rule, included by the inclusive <=60 Karat rule.
        List<List<String>> karat = toList(new String[][]{
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
        });

        String[] labels = {"LeetCode ex1", "LeetCode ex2", "Karat"};
        List<List<List<String>>> inputs = Arrays.asList(lc1, lc2, karat);
        for (int i = 0; i < inputs.size(); i++) {
            System.out.println("== " + labels[i] + " ==");
            List<String> names = sol.leetcodeNamesOnly(inputs.get(i));
            Collections.sort(names);
            System.out.println("  names only  (<60) : " + names);
            System.out.println("  first window(<=60): " + sol.karatFirstWindow(inputs.get(i)));
        }
    }
}
