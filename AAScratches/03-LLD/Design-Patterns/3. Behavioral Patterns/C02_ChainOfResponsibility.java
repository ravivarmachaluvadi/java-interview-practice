/*
 * =====================================================================
 *  Chain of Responsibility - ATM cash dispenser      Behavioral | Medium
 * =====================================================================
 *
 * PROBLEM
 *   An ATM must pay Rs 8,700 with the fewest notes from 2000, 500, 200 and
 *   100 cassettes, and refuse amounts it cannot pay. Adding a Rs 50
 *   cassette should not mean rewriting one big if/else.
 *
 * KEY INSIGHT
 *   Each handler pays as many of its own notes as fit and forwards the
 *   remainder to the next (2000 -> 500 -> 200 -> 100) - partial handling,
 *   not all-or-nothing. Ordered high to low, that greedy step gives the
 *   fewest notes: the ordering IS the algorithm.
 *
 * ROLES IN THIS CODE
 *   DispenseChain                Handler - holds nextChain, shared forward()
 *   Rupee2000/500/200/100..      ConcreteHandler - one denomination each
 *   ChainOfResponsibilityExample Client - wires the chain, calls its head
 *
 * INTERVIEW FOLLOW-UPS
 *   - A cassette runs out: dispense min(needed, stock) and forward the rest;
 *     greedy then stops being optimal.
 *   - CoR vs Decorator: same linked shape; a decorator always calls the
 *     next link, a chain link may stop the request.
 *   - Seen in: servlet filters, Spring Security's filter chain, support
 *     ticket escalation, approval workflows by amount.
 *
 * RUN
 *   4 cases: 8700, 2500, 100, and 50 / 0 rejected.
 */

import java.util.ArrayList;
import java.util.List;

/** Handler: knows only its successor, never the whole chain. */
abstract class DispenseChain {
    protected DispenseChain nextChain;

    public void setDispenseChain(DispenseChain nextChain) {
        this.nextChain = nextChain;
    }

    /** Handle what this link can and forward the rest; record the plan in notes. */
    public abstract void dispense(int amount, List<String> notes);

    /**
     * Shared tail step. Without the else-branch a leftover amount at the end
     * of the chain would disappear silently, which is the classic CoR trap.
     */
    protected void forward(int amount, List<String> notes) {
        if (amount == 0) {
            return;
        }
        if (nextChain != null) {
            nextChain.dispense(amount, notes);
        } else {
            System.out.println("  WARNING: cannot dispense remaining " + amount);
            notes.add("unserved " + amount);
        }
    }
}

class Rupee2000Dispenser extends DispenseChain {

    @Override
    public void dispense(int amount, List<String> notes) {
        if (amount >= 2000) {
            int num = amount / 2000;
            System.out.println("  Dispensing " + num + " x 2000 notes");
            notes.add(num + " x 2000");
            forward(amount % 2000, notes);
        } else {
            forward(amount, notes); // nothing for this link, push it down
        }
    }
}

class Rupee500Dispenser extends DispenseChain {

    @Override
    public void dispense(int amount, List<String> notes) {
        if (amount >= 500) {
            int num = amount / 500;
            System.out.println("  Dispensing " + num + " x 500 notes");
            notes.add(num + " x 500");
            forward(amount % 500, notes);
        } else {
            forward(amount, notes);
        }
    }
}

class Rupee200Dispenser extends DispenseChain {

    @Override
    public void dispense(int amount, List<String> notes) {
        if (amount >= 200) {
            int num = amount / 200;
            System.out.println("  Dispensing " + num + " x 200 notes");
            notes.add(num + " x 200");
            forward(amount % 200, notes);
        } else {
            forward(amount, notes);
        }
    }
}

class Rupee100Dispenser extends DispenseChain {

    @Override
    public void dispense(int amount, List<String> notes) {
        if (amount >= 100) {
            int num = amount / 100;
            System.out.println("  Dispensing " + num + " x 100 notes");
            notes.add(num + " x 100");
            forward(amount % 100, notes);
        } else {
            forward(amount, notes);
        }
    }
}

/** Client: builds the chain once, then talks only to its head. */
class ChainOfResponsibilityExample {

    private final DispenseChain dispenseChain;

    public ChainOfResponsibilityExample() {
        Rupee2000Dispenser rupee2000Dispenser = new Rupee2000Dispenser();
        Rupee500Dispenser rupee500Dispenser = new Rupee500Dispenser();
        Rupee200Dispenser rupee200Dispenser = new Rupee200Dispenser();
        Rupee100Dispenser rupee100Dispenser = new Rupee100Dispenser();

        // Descending order is what makes the greedy split use fewest notes.
        rupee2000Dispenser.setDispenseChain(rupee500Dispenser);
        rupee500Dispenser.setDispenseChain(rupee200Dispenser);
        rupee200Dispenser.setDispenseChain(rupee100Dispenser);

        this.dispenseChain = rupee2000Dispenser;
    }

    /** Returns the note plan, or an empty list when the request is rejected. */
    public List<String> dispenseCash(int amount) {
        List<String> notes = new ArrayList<>();
        if (amount <= 0) {
            System.out.println("  Amount must be positive.");
            return notes;
        }
        if (amount % 100 != 0) {
            System.out.println("  Amount should be a multiple of 100.");
            return notes;
        }
        dispenseChain.dispense(amount, notes);
        return notes;
    }

    public static void main(String[] args) {
        ChainOfResponsibilityExample atm = new ChainOfResponsibilityExample();

        // Case 1: typical - three of the four links contribute, and the chain stops
        // early once the remainder hits 0, so the 100 link is never reached.
        System.out.println("case 1: 8700");
        print("  actual", atm.dispenseCash(8700), "[4 x 2000, 1 x 500, 1 x 200]");

        // Case 2: the 200 and 100 links forward without handling.
        System.out.println("case 2: 2500");
        print("  actual", atm.dispenseCash(2500), "[1 x 2000, 1 x 500]");

        // Case 3: edge - the smallest servable amount reaches the last link.
        System.out.println("case 3: 100");
        print("  actual", atm.dispenseCash(100), "[1 x 100]");

        // Case 4: edge - rejected before the chain is ever entered.
        System.out.println("case 4: 50 and 0 are rejected");
        print("  actual 50", atm.dispenseCash(50), "[]");
        print("  actual 0 ", atm.dispenseCash(0), "[]");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
