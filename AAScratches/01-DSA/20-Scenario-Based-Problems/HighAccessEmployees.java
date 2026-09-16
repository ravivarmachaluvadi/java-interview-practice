import java.util.*;

// 2933. High-Access Employees
// https://leetcode.com/problems/high-access-employees/description/
class HighAccessEmployees {
    public List<String> findHighAccessEmployees(List<List<String>> access_times) {
        Map<String, List<Integer>> map = new HashMap<>();
        // Convert times to minutes and group by employee
        for (List<String> rec : access_times) {
            String name = rec.get(0);
            String ts = rec.get(1);
            int hours = Integer.parseInt(ts.substring(0, 2));
            int mins = Integer.parseInt(ts.substring(2));
            int total = hours * 60 + mins;
            map.computeIfAbsent(name, k -> new ArrayList<>()).add(total);
        }
        List<String> result = new ArrayList<>();
        // For each employee, sort and check if any three accesses fall within <60 mins
        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
            String name = entry.getKey();
            List<Integer> times = entry.getValue();
            if (times.size() < 3) continue;
            Collections.sort(times);
            for (int i = 2; i < times.size(); i++) {
                if (times.get(i) - times.get(i - 2) < 60) {
                    result.add(name);
                    break;
                }
            }
        }
        return result;
    }

    // Example main method
    public static void main(String[] args) {
        HighAccessEmployees sol = new HighAccessEmployees();
        List<List<String>> input1 = Arrays.asList(
                Arrays.asList("a", "0549"),
                Arrays.asList("b", "0457"),
                Arrays.asList("a", "0532"),
                Arrays.asList("a", "0621"),
                Arrays.asList("b", "0540")
        );
        List<String> output1 = sol.findHighAccessEmployees(input1);
        System.out.println("Output1: " + output1);
        // Expected: ["a"]

        List<List<String>> input2 = Arrays.asList(
                Arrays.asList("d", "0002"),
                Arrays.asList("c", "0808"),
                Arrays.asList("c", "0829"),
                Arrays.asList("e", "0215"),
                Arrays.asList("d", "1508"),
                Arrays.asList("d", "1444"),
                Arrays.asList("d", "1410"),
                Arrays.asList("c", "0809")
        );
        List<String> output2 = sol.findHighAccessEmployees(input2);
        System.out.println("Output2: " + output2);
        // Expected: ["c","d"]
    }
}
