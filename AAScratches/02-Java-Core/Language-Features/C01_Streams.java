/**
 * Problem: Everyday Java 8 Stream/Collector recipes, ending with "max salary per department"
 *          done two ways (raw Optional vs collectingAndThen) plus groupingBy + filtering.
 *
 * Approaches:
 *  - countOccurrences      : int[] -> boxed() -> groupingBy(identity, counting())
 *  - filterToArrayAndList  : filter + toArray(String[]::new) and filter + toList + list.toArray(new String[0])
 *  - flattenListOfLists    : flatMap(List::stream)
 *  - maxSalaryWithOptional : groupingBy(dept, maxBy(...)) -> Map<String, Optional<Employee>>  (caller unwraps)
 *  - maxSalaryUnwrapped    : groupingBy(dept, collectingAndThen(maxBy(...), opt -> salary)) -> Map<String, Double>
 *  - highEarnersByDept     : groupingBy(dept, filtering(salary > 7000, toList())) keeps EMPTY groups;
 *                            filter-then-groupingBy drops them - shown side by side.
 *
 * Time: O(n) per pipeline. Space: O(k) for each result map/list.
 */
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Streams {

    // int[] cannot be collected directly - boxed() turns IntStream into Stream<Integer>.
    static Map<Integer, Long> countOccurrences(int[] arr) {
        return Arrays.stream(arr)
                .boxed()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    // Two ways to end up with a String[]: toArray(generator) straight from the stream,
    // or collect to a List and then list.toArray(new String[0]).
    static String[] filterToArrayAndList(String[] words, String prefix) {
        String[] direct = Arrays.stream(words)
                .filter(s -> s.startsWith(prefix))
                .toArray(String[]::new);

        List<String> viaList = Stream.of(words)
                .filter(s -> s.startsWith(prefix))
                .collect(Collectors.toList());
        String[] fromList = viaList.toArray(new String[0]);   // new String[0] is the idiomatic size hint

        if (!Arrays.equals(direct, fromList)) throw new AssertionError("both routes must agree");
        return direct;
    }

    // flatMap: each inner list becomes a stream, and the streams are concatenated.
    static List<String> flattenListOfLists(List<List<String>> listOfLists) {
        return listOfLists.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    // Approach 1: maxBy returns Optional<Employee> (a group could theoretically be empty),
    // so the map value is Optional and the caller has to unwrap it.
    static Map<String, Optional<Employee>> maxSalaryWithOptional(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary))
                ));
    }

    // Approach 2: collectingAndThen(downstream, finisher) runs the finisher on each group's
    // result, so the Optional is unwrapped inside the collector and the map is Map<String, Double>.
    static Map<String, Double> maxSalaryUnwrapped(List<Employee> employees) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary)),
                                opt -> opt.map(Employee::getSalary).orElse(0.0)
                        )
                ));
    }

    // Collectors.filtering (Java 9+) filters INSIDE each group -> departments with no match
    // still appear with an empty list.
    static Map<String, List<Employee>> highEarnersByDeptKeepEmptyGroups(List<Employee> employees, double threshold) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.filtering(e -> e.getSalary() > threshold, Collectors.toList())
                ));
    }

    // Stream.filter BEFORE groupingBy -> departments with no match are absent from the map.
    static Map<String, List<Employee>> highEarnersByDeptDropEmptyGroups(List<Employee> employees, double threshold) {
        return employees.stream()
                .filter(e -> e.getSalary() > threshold)
                .collect(Collectors.groupingBy(Employee::getDepartment));
    }

    public static void main(String[] args) {
        System.out.println("countOccurrences      : " + countOccurrences(new int[]{1, 2, 3, 3, 3, 4, 5}));
        System.out.println("filterToArrayAndList  : " + Arrays.toString(filterToArrayAndList(new String[]{"Abc", "Bac", "Axy"}, "A")));
        System.out.println("flattenListOfLists    : " + flattenListOfLists(Arrays.asList(
                Arrays.asList("Alice", "Bob"),
                Arrays.asList("Charlie", "David"),
                Arrays.asList("Eve", "Frank"))));

        List<Employee> employees = Employee.sample();
        System.out.println("employees             : " + employees);

        Map<String, Optional<Employee>> withOptional = maxSalaryWithOptional(employees);
        withOptional.forEach((dept, opt) ->
                System.out.println("maxSalaryWithOptional : " + dept + " -> " + opt.map(Employee::getSalary).orElse(0.0)));

        maxSalaryUnwrapped(employees).forEach((dept, salary) ->
                System.out.println("maxSalaryUnwrapped    : " + dept + " -> " + salary));

        System.out.println("highEarners keepEmpty : " + highEarnersByDeptKeepEmptyGroups(employees, 7000));
        System.out.println("highEarners dropEmpty : " + highEarnersByDeptDropEmptyGroups(employees, 7000));
    }
}

class Employee {
    private final String name;
    private final String department;
    private final double salary;

    Employee(String name, String department, double salary) {
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    String getDepartment() { return department; }

    double getSalary() { return salary; }

    @Override
    public String toString() { return name + "(" + department + "," + salary + ")"; }

    // Salaries chosen so the 7000 threshold splits them: only David passes, Bob is exactly 7000 (not >).
    static List<Employee> sample() {
        return Arrays.asList(
                new Employee("Alice", "HR", 4000),
                new Employee("Bob", "IT", 7000),
                new Employee("Charlie", "HR", 4500),
                new Employee("David", "IT", 8000),
                new Employee("Eve", "Finance", 6500)
        );
    }
}
