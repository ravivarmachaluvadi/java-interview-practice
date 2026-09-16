/**
 * Demonstrates various Java Stream operations:
 * - Counts occurrences of integers in an array.
 * - Filters strings by prefix and converts to arrays/lists.
 * - Flattens a list of lists into a single list.
 * - Groups employees by department and finds the highest salary per group.
 *
 * Approach: Use stream pipelines with collectors such as groupingBy, counting,
 * filtering, flatMap, and maxBy. Convert between primitive arrays and boxed types
 * where necessary.
 *
 * Time Complexity: O(n) for each independent stream operation (n = number of elements).
 * Space Complexity: O(k) for the resulting collections (k ≤ n), plus overhead for intermediate streams.
 */
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Streams {
    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 3, 3, 4, 5};

        Map<Integer, Long> map = Arrays.stream(arr)
                .boxed()// int arr <-> boxed
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        System.out.println(map);

        String[] arr1 = {"Abc", "Bac"};

        String[] array = Arrays.stream(arr1).filter(s -> s.startsWith("A")).toArray(String[]::new);

        System.out.println(Arrays.toString(array));

        List<String> stringList = Stream.of(array).filter(string -> string.startsWith("A")).collect(Collectors.toList());
        String[] array1 = stringList.toArray(new String[0]);


        List<List<String>> listOfLists = Arrays.asList(
                Arrays.asList("Alice", "Bob"),
                Arrays.asList("Charlie", "David"),
                Arrays.asList("Eve", "Frank")
        );

        // Using flatMap to flatten the list of lists
        List<String> flatList = listOfLists.stream()
                .flatMap(strings -> strings.stream())
                .collect(Collectors.toList());

        System.out.println("Flattened List: " + flatList);

        List<Employee> emplList = Employee.getEmplList();

        Map<String, Optional<Employee>> maxSalaryByDept = emplList.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary))
                ));

        maxSalaryByDept.forEach((dept, emp) ->
                System.out.println("Department: " + dept + ", Max Salary: " + emp.get().getSalary()));

    }
}


class Employee {
    private String name;
    private String department;
    private double salary;

    // Constructor, getters, and setters
    public Employee(String name, String department, double salary) {
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public String getDepartment() {
        return department;
    }

    public double getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return name + " (" + department + ") - " + salary;
    }

    public static List<Employee> getEmplList() {
        List<Employee> employees = Arrays.asList(
                new Employee("Alice", "HR", 70000),
                new Employee("Bob", "IT", 85000),
                new Employee("Charlie", "IT", 95000),
                new Employee("David", "HR", 75000),
                new Employee("Eve", "Finance", 80000)
        );
        return employees;
    }
}
