/*
 * =====================================================================
 *  Visitor Pattern - bonus and appraisal over employees      Behavioral
 * =====================================================================
 *
 * PATTERN
 *   Visitor (Behavioral, GoF). Built on double dispatch.
 *
 * INTENT
 *   Represent an operation to be performed on the elements of an object
 *   structure. Visitor lets you add new operations without modifying the
 *   classes of the elements on which it operates.
 *
 * WHEN TO USE, WHEN NOT
 *   Use    : a STABLE type hierarchy with a GROWING set of operations -
 *            AST nodes (evaluate, pretty-print, type-check), file-system
 *            entries (size, virus scan, backup), tax/bonus rules per
 *            employee type, compilers and linters.
 *   Not    : when new element types appear often. Every new element forces
 *            a new visit() overload in EVERY visitor - Visitor trades
 *            "easy to add operations" for "painful to add types".
 *            Also not when a plain interface method would do; Visitor is
 *            only worth it when the operation does not belong on the type.
 *
 * ROLES IN THIS CODE
 *   Employee                Element         - declares accept(visitor).
 *   Engineer, Manager       ConcreteElement - each implements accept as the
 *                                             one line visitor.visit(this).
 *   EmployeeVisitor         Visitor         - one overload per element type.
 *   BonusCalculatorVisitor  ConcreteVisitor - sums a bonus by type.
 *   ApprisalReportVisitor   ConcreteVisitor - builds report lines by type.
 *   main                    Client / ObjectStructure - holds the List and
 *                                             pushes each visitor over it.
 *
 * KEY INSIGHT
 *   accept(v) { v.visit(this); } looks pointless until you see why it is
 *   there. Java picks overloads on the STATIC type, so v.visit(employee)
 *   would not compile against a List<Employee>. accept() is a virtual call,
 *   so it resolves to Engineer.accept at run time, and only inside that
 *   method is "this" statically an Engineer - which is what selects
 *   visit(Engineer). Two dispatches: one on the element, one on the
 *   visitor. That is the whole trick, and the thing to say out loud.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not just instanceof / a switch on type? It compiles, but the
 *     type test is repeated in every operation and the compiler cannot
 *     tell you when you forget a type. Java 21 sealed types + pattern
 *     matching switch give you that check back, and largely replace Visitor.
 *   - Visitor vs Strategy? Strategy has one algorithm slot; Visitor has one
 *     method per element type and is about recovering the concrete type.
 *   - How do you return a value? Either accumulate in the visitor (done
 *     here) or make it generic: interface Visitor<R> { R visit(Engineer e); }
 *   - What breaks encapsulation here? The visitor needs getters on the
 *     elements, so their internals leak outward.
 *
 * RUN
 *   main() runs 3 cases (bonus visitor, appraisal visitor, empty list plus
 *   a brand-new visitor added with zero element changes) actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

/** Element: the only thing every employee must expose to visitors. */
interface Employee {
    void accept(EmployeeVisitor visitor);
}

class Engineer implements Employee {

    private final String name;
    private final int linesOfCode;

    public Engineer(String name, int linesOfCode) {
        this.name = name;
        this.linesOfCode = linesOfCode;
    }

    @Override
    public void accept(EmployeeVisitor visitor) {
        // Virtual call lands here, so "this" is statically an Engineer and
        // the compiler can pick visit(Engineer). Second half of the dispatch.
        visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }
}

class Manager implements Employee {

    private final String name;
    private final int teamSize;

    public Manager(String name, int teamSize) {
        this.name = name;
        this.teamSize = teamSize;
    }

    @Override
    public void accept(EmployeeVisitor visitor) {
        visitor.visit(this);
    }

    public String getName() {
        return name;
    }

    public int getTeamSize() {
        return teamSize;
    }
}

/** Visitor: one overload per concrete element type. */
interface EmployeeVisitor {
    void visit(Engineer engineer);

    void visit(Manager manager);
}

/** ConcreteVisitor: a different bonus rule per employee type. */
class BonusCalculatorVisitor implements EmployeeVisitor {

    private double totalBonus;

    @Override
    public void visit(Engineer engineer) {
        double bonus = engineer.getLinesOfCode() * 0.5;
        System.out.println("  Engineer " + engineer.getName() + " bonus = " + bonus);
        totalBonus += bonus;
    }

    @Override
    public void visit(Manager manager) {
        double bonus = manager.getTeamSize() * 1000;
        System.out.println("  Manager " + manager.getName() + " bonus = " + bonus);
        totalBonus += bonus;
    }

    public double getTotalBonus() {
        return totalBonus;
    }
}

/** ConcreteVisitor: same elements, completely unrelated operation. */
class ApprisalReportVisitor implements EmployeeVisitor {

    private final List<String> report = new ArrayList<>();

    @Override
    public void visit(Engineer engineer) {
        report.add(engineer.getName() + " (Engineer) LOC=" + engineer.getLinesOfCode());
    }

    @Override
    public void visit(Manager manager) {
        report.add(manager.getName() + " (Manager) team=" + manager.getTeamSize());
    }

    public List<String> getReport() {
        return report;
    }
}

class VisitorDesignPatternDemo {

    public static void main(String[] args) {
        // Static type is Employee: the client cannot tell the two apart,
        // and does not need to.
        List<Employee> employees = List.of(
                new Engineer("Ravi", 5000),
                new Manager("Varma", 10)
        );

        // Case 1: typical - one visitor walks the mixed structure.
        System.out.println("case 1: bonus visitor");
        BonusCalculatorVisitor bonusVisitor = new BonusCalculatorVisitor();
        employees.forEach(employee -> employee.accept(bonusVisitor));
        print("  1a total bonus", bonusVisitor.getTotalBonus(), "12500.0");

        // Case 2: a second operation over the same untouched elements.
        System.out.println("case 2: appraisal visitor");
        ApprisalReportVisitor appraisalVisitor = new ApprisalReportVisitor();
        employees.forEach(employee -> employee.accept(appraisalVisitor));
        print("  2a report", appraisalVisitor.getReport(),
                "[Ravi (Engineer) LOC=5000, Varma (Manager) team=10]");

        // Case 3a: edge - no elements, so no visit() ever fires.
        System.out.println("case 3: empty structure and a brand-new visitor");
        BonusCalculatorVisitor emptyRun = new BonusCalculatorVisitor();
        List.<Employee>of().forEach(employee -> employee.accept(emptyRun));
        print("  3a empty total", emptyRun.getTotalBonus(), "0.0");

        // Case 3b: a third operation added without editing Engineer/Manager -
        // this is the payoff of the pattern.
        EmployeeVisitor headcountByType = new EmployeeVisitor() {
            int engineers;
            int managers;

            @Override
            public void visit(Engineer engineer) {
                engineers++;
                System.out.println("  counted engineer #" + engineers);
            }

            @Override
            public void visit(Manager manager) {
                managers++;
                System.out.println("  counted manager #" + managers);
            }
        };
        employees.forEach(employee -> employee.accept(headcountByType));
        print("  3b visits ran", "no element class changed", "no element class changed");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
