/*
 * =====================================================================
 *  Stream and Collector recipes          Java Core | Medium   MUST-KNOW
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   The six stream pipelines that come up again and again in interviews and in
 *   real service code: frequency counting, filtering into an array or a list,
 *   flattening nested lists, "max per group" done two ways, and the difference
 *   between filtering inside a group and filtering before grouping.
 *
 * WHAT YOU WILL SEE
 *   countOccurrences([1,2,3,3,3,4,5])       -> {1=1, 2=1, 3=3, 4=1, 5=1}
 *   filter "A" from [Abc, Bac, Axy]         -> [Abc, Axy]
 *   flatten [[Alice,Bob],[Charlie,David]]   -> [Alice, Bob, Charlie, David]
 *   max salary per dept (Optional form)     -> {Finance=6500.0, HR=4500.0, IT=8000.0}
 *   max salary per dept (unwrapped form)    -> same numbers, no Optional to peel
 *   high earners > 7000, Collectors.filtering -> {Finance=[], HR=[], IT=[David]}
 *   high earners > 7000, filter first        -> {IT=[David]}   empty groups vanish
 *
 * HOW IT WORKS
 *   1. int[] has no stream of objects, so Arrays.stream(...).boxed() turns an
 *      IntStream into Stream<Integer> before any Collector can be used.
 *   2. groupingBy(classifier, downstream) buckets elements by the classifier and
 *      feeds each bucket to the downstream collector - counting(), toList(),
 *      maxBy(), filtering(), whatever reduces a group to one value.
 *   3. maxBy returns Optional because a reduction over a group has no identity
 *      value; collectingAndThen(maxBy(...), finisher) runs the finisher on each
 *      group's result, so the Optional is unwrapped inside the collector.
 *   4. flatMap replaces each element with the contents of a stream, so a
 *      Stream<List<String>> becomes a Stream<String>.
 *   5. Collectors.filtering (Java 9+) filters AFTER bucketing, so a group with
 *      no survivors still exists with an empty list. stream.filter before
 *      groupingBy removes the elements first, so that key never appears.
 *
 * KEY INSIGHT
 *   A stream pipeline is source -> intermediate ops -> one terminal op, and the
 *   whole shape of the result is decided by the terminal collector. So think
 *   in terms of the downstream collector: "what do I want each group reduced
 *   to?" - a count, a list, a max, a filtered list. And remember where the
 *   filter sits, because that alone decides whether empty groups survive.
 *
 * GOTCHAS
 *   - groupingBy returns a HashMap, so iteration order is unspecified. Wrap in
 *     a TreeMap (as main does) or pass a map factory when order matters.
 *   - groupingBy throws NullPointerException if the classifier returns null.
 *   - Collectors.toList() gives no guarantee of mutability; use toCollection or
 *     Stream.toList() (Java 16+, unmodifiable) when you care.
 *
 * INTERVIEW FOLLOW-UPS
 *   - groupingBy vs partitioningBy? (arbitrary keys vs exactly true/false)
 *   - How do you get a sorted or insertion-ordered result map? (three-arg
 *     groupingBy with TreeMap::new or LinkedHashMap::new)
 *   - Why does maxBy return Optional but counting() does not? (no identity)
 *   - When is parallelStream a win? (big CPU-bound work, splittable source,
 *     stateless associative reduction)
 *
 * RUN
 *   main() runs the six pipelines on a typical dataset plus an empty-input edge
 *   case, and prints actual vs expected.
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
        // new String[0] is the idiomatic size hint
        String[] fromList = viaList.toArray(new String[0]);

        if (!Arrays.equals(direct, fromList)) {
            throw new AssertionError("both routes must agree");
        }
        return direct;
    }

    // flatMap: each inner list becomes a stream, and the streams are concatenated.
    static List<String> flattenListOfLists(List<List<String>> listOfLists) {
        return listOfLists.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    // Approach 1: maxBy returns Optional<Employee> (a reduction has no identity value),
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
    static Map<String, List<Employee>> highEarnersByDeptKeepEmptyGroups(List<Employee> employees,
                                                                       double threshold) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.filtering(e -> e.getSalary() > threshold, Collectors.toList())
                ));
    }

    // Stream.filter BEFORE groupingBy -> departments with no match are absent from the map.
    static Map<String, List<Employee>> highEarnersByDeptDropEmptyGroups(List<Employee> employees,
                                                                       double threshold) {
        return employees.stream()
                .filter(e -> e.getSalary() > threshold)
                .collect(Collectors.groupingBy(Employee::getDepartment));
    }

    /** groupingBy hands back a HashMap; sort it so the printed output is deterministic. */
    private static <K, V> Map<K, V> sorted(Map<K, V> map) {
        return new TreeMap<>(map);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // ---- typical cases -------------------------------------------------
        print("countOccurrences        : ",
                sorted(countOccurrences(new int[]{1, 2, 3, 3, 3, 4, 5})),
                "{1=1, 2=1, 3=3, 4=1, 5=1}");

        print("filterToArrayAndList    : ",
                Arrays.toString(filterToArrayAndList(new String[]{"Abc", "Bac", "Axy"}, "A")),
                "[Abc, Axy]");

        print("flattenListOfLists      : ", flattenListOfLists(Arrays.asList(
                        Arrays.asList("Alice", "Bob"),
                        Arrays.asList("Charlie", "David"),
                        Arrays.asList("Eve", "Frank"))),
                "[Alice, Bob, Charlie, David, Eve, Frank]");

        List<Employee> employees = Employee.sample();
        System.out.println("employees               : " + employees);

        // Approach 1: the caller peels the Optional off every value.
        Map<String, Double> peeledByCaller = new TreeMap<>();
        maxSalaryWithOptional(employees).forEach((dept, best) ->
                peeledByCaller.put(dept, best.map(Employee::getSalary).orElse(0.0)));
        print("maxSalaryWithOptional   : ", peeledByCaller,
                "{Finance=6500.0, HR=4500.0, IT=8000.0}");

        // Approach 2: same numbers, but the collector already unwrapped them.
        print("maxSalaryUnwrapped      : ", sorted(maxSalaryUnwrapped(employees)),
                "{Finance=6500.0, HR=4500.0, IT=8000.0}");

        // The two filtering positions, side by side. Bob is exactly 7000, so he fails "> 7000".
        print("highEarners keepEmpty   : ",
                sorted(highEarnersByDeptKeepEmptyGroups(employees, 7000)),
                "{Finance=[], HR=[], IT=[David(IT,8000.0)]}");
        print("highEarners dropEmpty   : ",
                sorted(highEarnersByDeptDropEmptyGroups(employees, 7000)),
                "{IT=[David(IT,8000.0)]}");

        // ---- edge case: empty input ---------------------------------------
        // Every collector still returns an empty container, never null.
        print("empty countOccurrences  : ", countOccurrences(new int[]{}), "{}");
        print("empty flatten           : ", flattenListOfLists(new ArrayList<>()), "[]");
        print("empty maxSalary         : ", maxSalaryUnwrapped(new ArrayList<>()), "{}");
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

    String getDepartment() {
        return department;
    }

    double getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return name + "(" + department + "," + salary + ")";
    }

    // Salaries chosen so the 7000 threshold splits them: only David passes,
    // Bob is exactly 7000 (not >).
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
