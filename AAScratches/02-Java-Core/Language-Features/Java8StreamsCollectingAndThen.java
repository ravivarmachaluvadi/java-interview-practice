
import java.util.*;
import java.util.stream.*;

class Employee {
    private final String name;
    private final String department;
    private final double salary;

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
}

class Java8StreamsCollectingAndThen {
    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
                new Employee("Alice", "HR", 4000),
                new Employee("Bob", "IT", 7000),
                new Employee("Charlie", "HR", 4500),
                new Employee("David", "IT", 8000),
                new Employee("Eve", "Finance", 6500)
        );

        Map<String, Double> maxSalaryByDept = employees.stream()
                .collect(Collectors.groupingBy(Employee::getDepartment,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparingDouble(Employee::getSalary)),
                                emp -> emp.map(Employee::getSalary).orElse(0.0)
                        )
                ));

        /*
        maxSalaryByDept.forEach((dept, salary) ->
                System.out.println(dept + " → " + salary)
        );
        */


        Map<String, List<Employee>> collect = employees
                .stream()
                .collect(Collectors
                        .groupingBy(Employee::getDepartment, Collectors
                                .filtering(employee -> employee.getSalary() > 7000,
                                        Collectors.toList())));

        collect.forEach((s, employees1) ->
                {
                    System.out.println(s);
                    System.out.println(employees1);
                }
        );
    }
}

