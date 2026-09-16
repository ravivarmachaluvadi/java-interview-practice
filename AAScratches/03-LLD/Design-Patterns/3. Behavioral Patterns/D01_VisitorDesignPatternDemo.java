import java.util.List;

/**
 * The Visitor Pattern is a behavioral design pattern that lets you
 * <p>
 * add new operations to a group of objects without modifying their classes.
 * <p>
 * It achieves this by:
 * <p>
 * Separating the operation (behavior) from the objects (data structure).
 * <p>
 * Defining a Visitor object that “visits” each element and
 * <p>
 * performs the required operation.
 * <p>
 * So instead of adding logic inside each class, we put that logic in
 * <p>
 * a Visitor class — making the system more extensible and
 * <p>
 * adhering to the Open/Closed Principle.
 */
interface Employee {
    void accept(EmployeeVisitor visitor);
}

class Engineer implements Employee {

    private String name;
    private int linesOfCode;

    public Engineer(String name, int linesOfCode) {
        this.name = name;
        this.linesOfCode = linesOfCode;
    }

    @Override
    public void accept(EmployeeVisitor visitor) {
        visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }
}

class Manager implements Employee {

    private String name;
    private int teamSize;

    public Manager(String name, int teamSize) {
        this.name = name;
        this.teamSize = teamSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    @Override
    public void accept(EmployeeVisitor visitor) {
        visitor.visit(this);
    }
}

interface EmployeeVisitor {
    void visit(Engineer engineer);

    void visit(Manager manager);
}

class BonusCalculatorVisitor implements EmployeeVisitor {

    @Override
    public void visit(Engineer engineer) {
        System.out.println("Calculating bonus for Engineer: " + engineer.getName());
        double bonus = engineer.getLinesOfCode() * 0.5;
        System.out.println("Bonus: " + bonus);
    }

    @Override
    public void visit(Manager manager) {
        System.out.println("Calculating bonus for Manager: " + manager.getName());
        double bonus = manager.getTeamSize() * 1000;
        System.out.println("Bonus: " + bonus);
    }
}

class ApprisalReportVisitor implements EmployeeVisitor {

    @Override
    public void visit(Engineer engineer) {
        System.out.println("Appraisal report for Engineer " + engineer.getName() +
                ": Code Quality Excellent, Lines of code = " + engineer.getLinesOfCode());
    }

    @Override
    public void visit(Manager manager) {
        System.out.println("Appraisal report for Manager " + manager.getName() +
                ": Leadership Strong, Team size = " + manager.getTeamSize());
    }
}

class VisitorDesignPatternDemo {
    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Engineer("Ravi", 5000),
                new Manager("Varma", 10)
        );

        EmployeeVisitor bonusVisitor = new BonusCalculatorVisitor();
        EmployeeVisitor appraisalVisitor = new ApprisalReportVisitor();

        System.out.println("=== Bonus Calculation ===");
        employees.forEach(emp -> emp.accept(bonusVisitor));

        System.out.println("\n=== Appraisal Report ===");
        employees.forEach(emp -> emp.accept(appraisalVisitor));
    }
}
