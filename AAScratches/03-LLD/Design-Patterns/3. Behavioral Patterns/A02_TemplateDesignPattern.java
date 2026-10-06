/*
 * =====================================================================
 *  Template Method - order fulfilment                Behavioral | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Every order goes validate -> reserve stock -> charge -> deliver ->
 *   notify. But a physical order ships by courier, an e-book has no stock
 *   and is emailed, and a pre-order only authorises the card until release.
 *   Copying the whole flow per order type lets the copies drift apart.
 *
 * KEY INSIGHT
 *   The base class owns the order of steps in one final method; subclasses
 *   fill in only the steps that differ. A boolean hook (needsStock) lets a
 *   subclass skip a step without touching the skeleton. Control is
 *   inverted: the base class calls the subclass, not the other way round.
 *
 * ROLES IN THIS CODE
 *   Fulfilment                 AbstractClass - fulfil() is the template method
 *   deliver()                  primitive operation (every subclass must write)
 *   charge(), needsStock()     default step and hook (override if needed)
 *   Physical.., Digital.., PreOrder..  ConcreteClass
 *
 * INTERVIEW FOLLOW-UPS
 *   - Template vs Strategy: inheritance fixes the skeleton at compile time;
 *     Strategy swaps a whole algorithm by composition at runtime.
 *   - Why final? So no subclass can reorder or skip the mandatory steps.
 *   - Seen in: Spring's JdbcTemplate and AbstractController, JUnit's
 *     setUp/test/tearDown, java.io.InputStream.read(byte[]).
 *
 * RUN
 *   4 cases: physical, digital (no stock step), pre-order (authorise only),
 *   quantity 0 rejected at validation.
 */

import java.util.ArrayList;
import java.util.List;

record Order(String id, int qty) {
}

abstract class Fulfilment {

    /** The template method. final: the step order is not negotiable. */
    final List<String> fulfil(Order order) {
        List<String> steps = new ArrayList<>();
        if (order.qty() <= 0) {
            steps.add("rejected qty " + order.qty());
            return steps;
        }
        steps.add("validated " + order.id());
        if (needsStock()) {
            steps.add("reserved stock x" + order.qty());
        }
        steps.add(charge());
        steps.add(deliver(order));
        steps.add("notified customer"); // invariant: every type ends here
        return steps;
    }

    /** Hook: subclasses may switch the stock step off. */
    protected boolean needsStock() {
        return true;
    }

    /** Default step: subclasses may override. */
    protected String charge() {
        return "charged card";
    }

    /** Primitive operation: every subclass must say how it delivers. */
    protected abstract String deliver(Order order);
}

class PhysicalFulfilment extends Fulfilment {
    protected String deliver(Order order) {
        return "shipped by courier";
    }
}

class DigitalFulfilment extends Fulfilment {
    protected boolean needsStock() {
        return false; // an e-book never runs out
    }

    protected String deliver(Order order) {
        return "emailed download link";
    }
}

class PreOrderFulfilment extends Fulfilment {
    protected String charge() {
        return "authorised card, charge on release";
    }

    protected String deliver(Order order) {
        return "queued for release day";
    }
}

class TemplateDesignPattern {

    public static void main(String[] args) {
        print("case 1 physical", new PhysicalFulfilment().fulfil(new Order("OD-1", 2)),
                "[validated OD-1, reserved stock x2, charged card, shipped by courier,"
                        + " notified customer]");
        print("case 2 digital ", new DigitalFulfilment().fulfil(new Order("OD-2", 1)),
                "[validated OD-2, charged card, emailed download link, notified customer]");
        print("case 3 preorder", new PreOrderFulfilment().fulfil(new Order("OD-3", 1)),
                "[validated OD-3, reserved stock x1, authorised card, charge on release,"
                        + " queued for release day, notified customer]");

        // Case 4: edge. The skeleton stops at validation for every type.
        print("case 4 qty 0   ", new DigitalFulfilment().fulfil(new Order("OD-4", 0)),
                "[rejected qty 0]");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
