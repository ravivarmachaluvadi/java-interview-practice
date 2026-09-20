/*
 * =====================================================================
 *  High-Access Employees / Badge Access      LeetCode 2933 | Medium  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Badge records arrive as [name, "HHMM"] pairs in arbitrary order. An employee is
 *   "high-access" if they badged in 3 or more times inside a single one-hour window.
 *   Return those employees. Times are within one day, so no midnight wrap-around.
 *   Two forms of the same question ship together here:
 *     LeetCode 2933 - return just the names, window is OPEN   (strictly under 60 min)
 *     Karat "badge access" - return the offending times too, window is CLOSED (<= 60)
 *
 * EXAMPLE
 *   [[a,0549] [b,0457] [a,0532] [a,0621] [b,0540]]  ->  [a]
 *     a's sorted scans are 0532 0549 0621; 0621 - 0532 = 49 min, three in one hour
 *   [[d,0002] [c,0808] [c,0829] [e,0215] [d,1508] [d,1444] [d,1410] [c,0809]] -> [c, d]
 *   Zhang badges at 0001 0010 0109 0110: 0110 - 0010 is exactly 60 minutes, so Zhang
 *     is EXCLUDED by the strict LeetCode rule and INCLUDED by the inclusive Karat rule
 *
 * APPROACH  (group by employee, sort times, check the i and i-2 pair)
 *   1. Parse each "HHMM" into minutes since midnight: (hhmm / 100) * 60 + hhmm % 100.
 *      Integer parsing rather than substrings, because Karat's input is not
 *      zero-padded - "835", "5" and "0" are all legal there.
 *   2. Group the minute values per employee in a HashMap<String, List<Integer>>.
 *   3. Sort each employee's list.
 *   4. leetcodeNamesOnly: slide a fixed window of THREE consecutive scans. For each
 *      i >= 2, test times[i] - times[i-2] < 60. One hit means high-access; stop.
 *   5. karatFirstWindow: the report form. Anchor at each scan i and grow forward while
 *      times[j] - times[i] <= 60. The first anchor collecting 3+ scans is reported,
 *      with its times formatted back to "HHMM".
 *
 * KEY INSIGHT
 *   Once the times are sorted, "three scans inside one hour" collapses to a single
 *   subtraction. The three tightest consecutive scans ending at index i are exactly
 *   times[i-2], times[i-1], times[i], and since they are sorted, the outer pair is the
 *   widest of the three gaps. So if times[i] - times[i-2] fits in an hour, everything
 *   between it does too - no inner loop, no sliding two-pointer, no bookkeeping.
 *   Recognise the pattern: a "k events within a window" question over sortable keys is
 *   always a one-line check on times[i] - times[i-k+1], which generalises for free.
 *   The second insight is a spec one: ALWAYS ask whether the hour is open or closed.
 *   Zhang above is the employee whose answer flips on that single word.
 *
 * COMPLEXITY
 *   Time  O(n log n)  sorting each employee's list; the scan afterwards is O(n)
 *   Space O(n)        the grouping map holds every record once
 *   Karat form adds O(n * k) in the worst case, k = scans inside one window.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Generalise to k scans in m minutes: the check becomes times[i] - times[i-k+1] < m.
 *   - Records stream in already sorted by time - can you avoid the sort? (yes: keep a
 *     per-employee deque, evict from the front while it is older than 60 min, and flag
 *     when the deque reaches size 3 - O(n) total)
 *   - Times cross midnight, or carry dates - what breaks? (parse to epoch minutes)
 *   - Report EVERY offending window instead of only the first per employee.
 *
 * RUN
 *   main() runs 3 datasets (two LeetCode examples, the full Karat example) through
 *   both implementations and prints actual vs expected on each line.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class HighAccessEmployees {

    private static final int WINDOW_MINUTES = 60;
    private static final int SCANS_NEEDED = 3;

    // ---------- Approach 1: LeetCode 2933 (names only, strict < 60) ----------

    public List<String> leetcodeNamesOnly(List<List<String>> accessTimes) {
        Map<String, List<Integer>> byEmployee = groupByEmployee(accessTimes);

        List<String> result = new ArrayList<>();
        for (Map.Entry<String, List<Integer>> entry : byEmployee.entrySet()) {
            List<Integer> times = entry.getValue();
            if (times.size() < SCANS_NEEDED) continue;
            Collections.sort(times);
            // Sorted, so times[i-2] <= times[i-1] <= times[i]: if the OUTER pair is
            // under an hour apart, all three scans share the same open hour.
            for (int i = SCANS_NEEDED - 1; i < times.size(); i++) {
                if (times.get(i) - times.get(i - (SCANS_NEEDED - 1)) < WINDOW_MINUTES) {
                    result.add(entry.getKey());
                    break; // one offending window is all it takes
                }
            }
        }
        return result;
    }

    // ---------- Approach 2: Karat form (first window plus its times, inclusive <= 60) ----------

    public Map<String, List<String>> karatFirstWindow(List<List<String>> accessTimes) {
        Map<String, List<Integer>> byEmployee = groupByEmployee(accessTimes);

        Map<String, List<String>> result = new TreeMap<>(); // sorted keys = stable output
        for (Map.Entry<String, List<Integer>> entry : byEmployee.entrySet()) {
            List<Integer> times = entry.getValue();
            Collections.sort(times);

            for (int i = 0; i < times.size(); i++) {
                // Grow the window anchored at scan i: keep adding later scans while
                // they are still within 60 minutes of the anchor.
                List<Integer> window = new ArrayList<>();
                window.add(times.get(i));
                for (int j = i + 1; j < times.size(); j++) {
                    if (times.get(j) - times.get(i) > WINDOW_MINUTES) {
                        break; // sorted, so nothing later can fit either
                    }
                    window.add(times.get(j));
                }

                if (window.size() >= SCANS_NEEDED) {
                    List<String> formatted = new ArrayList<>();
                    for (int minutes : window) formatted.add(formatTime(minutes));
                    result.put(entry.getKey(), formatted);
                    break; // only the first offending window per employee
                }
            }
        }
        return result;
    }

    // ---------- Shared helpers ----------

    /** Group the scan times per employee, each converted to minutes since midnight. */
    private static Map<String, List<Integer>> groupByEmployee(List<List<String>> accessTimes) {
        Map<String, List<Integer>> byEmployee = new HashMap<>();
        for (List<String> record : accessTimes) {
            byEmployee.computeIfAbsent(record.get(0), name -> new ArrayList<>())
                      .add(toMinutes(record.get(1)));
        }
        return byEmployee;
    }

    /** "HHMM" -> minutes. Integer parse, not substring, so "835", "5" and "0" also work. */
    private static int toMinutes(String timeStr) {
        int hhmm = Integer.parseInt(timeStr);
        return (hhmm / 100) * 60 + (hhmm % 100);
    }

    /** minutes since midnight -> zero-padded "HHMM" for the report. */
    private static String formatTime(int totalMinutes) {
        return String.format("%02d%02d", totalMinutes / 60, totalMinutes % 60);
    }

    // ---------- Test harness ----------

    private static List<List<String>> toList(String[][] rows) {
        List<List<String>> out = new ArrayList<>();
        for (String[] row : rows) out.add(Arrays.asList(row));
        return out;
    }

    private void check(String label, List<List<String>> input,
                       String expectedNames, String expectedWindows) {
        List<String> names = leetcodeNamesOnly(input);
        Collections.sort(names); // HashMap order is not guaranteed; sort to compare
        System.out.println(label + " names  (<60) : " + names + "   expected " + expectedNames);
        System.out.println(label + " window (<=60): " + karatFirstWindow(input)
                + "   expected " + expectedWindows);
    }

    public static void main(String[] args) {
        HighAccessEmployees sol = new HighAccessEmployees();

        // case 1 - LeetCode example 1
        sol.check("case 1", toList(new String[][]{
                        {"a", "0549"}, {"b", "0457"}, {"a", "0532"}, {"a", "0621"}, {"b", "0540"}}),
                "[a]",
                "{a=[0532, 0549, 0621]}");

        // case 2 - LeetCode example 2: two offenders, one of them near the end of the day
        sol.check("case 2", toList(new String[][]{
                        {"d", "0002"}, {"c", "0808"}, {"c", "0829"}, {"e", "0215"},
                        {"d", "1508"}, {"d", "1444"}, {"d", "1410"}, {"c", "0809"}}),
                "[c, d]",
                "{c=[0808, 0809, 0829], d=[1410, 1444, 1508]}");

        // case 3 - edge: nobody reaches three scans at all
        sol.check("case 3", toList(new String[][]{
                        {"solo", "0900"}, {"solo", "0910"}, {"other", "0905"}}),
                "[]",
                "{}");

        // case 4 - the full Karat example. Zhang (0010, 0109, 0110) is exactly 60 minutes
        // wide, so Zhang appears ONLY in the inclusive <=60 report - the spec gap made
        // visible. Note the non-zero-padded inputs "835", "5", "0", "1".
        sol.check("case 4", toList(new String[][]{
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
                        {"Amos", "601"}, {"Amos", "602"}, {"Paul", "1416"}}),
                "[Amos, Jose, Paul]",
                "{Amos=[0500, 0503, 0504], Jose=[0830, 0835, 0855, 0915, 0930], "
                        + "Paul=[1315, 1355, 1405], Zhang=[0010, 0109, 0110]}");
    }
}
