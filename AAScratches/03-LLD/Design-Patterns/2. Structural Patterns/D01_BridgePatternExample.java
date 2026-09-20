/*
 * =====================================================================
 *  Bridge Pattern - payment channel x payment method       Structural | Hard
 * =====================================================================
 *
 * PATTERN
 *   Bridge (Structural, object pattern). Two class hierarchies joined by a
 *   reference ("the bridge") instead of by inheritance.
 *
 * INTENT
 *   Split an abstraction from its implementation so the two can be extended
 *   independently, without the subclass explosion of a cartesian product.
 *
 * WHEN TO USE, WHEN NOT
 *   USE when a thing varies along two independent dimensions. Here: WHERE the
 *     payment happens (online / in-store) and HOW it is settled (card / UPI).
 *     Inheritance alone needs 2 x 2 = 4 classes, and 3 x 5 would need 15.
 *   USE when you want to swap the implementation at runtime, or ship the two
 *     hierarchies from different modules / teams.
 *   NOT when there is really only one dimension of change - a plain interface
 *     is enough and the extra layer is ceremony.
 *
 * ROLES IN THIS CODE
 *   PaymentMethod        Implementor. The "how" side of the bridge.
 *   CreditCardPayment,
 *   UpiPayment           ConcreteImplementor. One settlement channel each.
 *   Payment              Abstraction. Holds the PaymentMethod reference - that
 *                        field IS the bridge - and defines makePayment.
 *   OnlinePayment,
 *   InstorePayment       RefinedAbstraction. The "where" side; each adds its
 *                        own step, then delegates settlement across the bridge.
 *   BridgePatternExample Client. Picks one from each hierarchy and pairs them.
 *
 * KEY INSIGHT
 *   The moment you feel yourself naming a class OnlineUpiPayment, you have two
 *   dimensions and you want Bridge: make one of them a field, not a supertype.
 *   The counter-question ("isn't this just Strategy?") is about intent, not
 *   shape - Strategy swaps one algorithm inside one class; Bridge separates
 *   two whole hierarchies that each keep growing.
 *
 *   Fixed: main() used to read amounts from System.in, so the demo blocked
 *   (and crashed with NoSuchElementException on empty input). It now scans a
 *   fixed SAMPLE_INPUT string, and the pattern classes return a receipt so
 *   each case can print actual vs expected.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Bridge vs Strategy: same UML, different intent and lifetime (structure
 *     chosen up front vs algorithm swapped per call).
 *   - Bridge vs Adapter: Bridge is designed in before either side exists;
 *     Adapter is retrofitted to make an existing incompatible class fit.
 *   - Bridge vs Abstract Factory: they compose - a factory can hand the
 *     abstraction the right implementor.
 *   - Add a third dimension (currency). What does the design cost now?
 *
 * RUN
 *   main() runs 4 cases: online+UPI, in-store+card, the tricky one that proves
 *   the point (same abstraction, swapped implementor), and an edge case that
 *   adds a brand new implementor without touching either hierarchy.
 */

import java.util.Scanner;

/** Implementor: the "how" hierarchy. */
interface PaymentMethod {
    /** Settles the amount and returns a receipt line the caller can assert on. */
    String pay(int amount);
}

class CreditCardPayment implements PaymentMethod {
    @Override
    public String pay(int amount) {
        return "paid " + amount + " by CreditCard";
    }
}

class UpiPayment implements PaymentMethod {
    @Override
    public String pay(int amount) {
        return "paid " + amount + " by UPI";
    }
}

/** Abstraction: the "where" hierarchy. The field below is the bridge. */
abstract class Payment {
    protected final PaymentMethod paymentMethod;

    protected Payment(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /** Does the channel-specific work, then delegates across the bridge. */
    public abstract String makePayment(int amount);
}

class OnlinePayment extends Payment {
    OnlinePayment(PaymentMethod paymentMethod) {
        super(paymentMethod);
    }

    @Override
    public String makePayment(int amount) {
        return "online: " + paymentMethod.pay(amount);
    }
}

class InstorePayment extends Payment {
    InstorePayment(PaymentMethod paymentMethod) {
        super(paymentMethod);
    }

    @Override
    public String makePayment(int amount) {
        return "instore: " + paymentMethod.pay(amount);
    }
}

class BridgePatternExample {

    /** Amounts for the demo. Swap for System.in to drive it by hand. */
    private static final String SAMPLE_INPUT = "500 1200";

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(SAMPLE_INPUT);
        // Scanner sc = new Scanner(System.in); // interactive version

        // ---- case 1: typical - online channel settled over UPI --------------
        Payment onlinePayment = new OnlinePayment(new UpiPayment());
        int amount1 = sc.nextInt();
        print("case 1", onlinePayment.makePayment(amount1),
                "online: paid 500 by UPI");

        // ---- case 2: typical - in-store channel settled by card -------------
        Payment instorePayment = new InstorePayment(new CreditCardPayment());
        int amount2 = sc.nextInt();
        print("case 2", instorePayment.makePayment(amount2),
                "instore: paid 1200 by CreditCard");
        sc.close();

        // ---- case 3: the point of Bridge - same abstraction, other side -----
        // Nothing in OnlinePayment changed; only the implementor was swapped,
        // and no OnlineCardPayment class had to be written.
        Payment onlineByCard = new OnlinePayment(new CreditCardPayment());
        print("case 3", onlineByCard.makePayment(0),
                "online: paid 0 by CreditCard");

        // ---- case 4: edge - a new implementor added with zero changes above --
        PaymentMethod wallet = amount -> "paid " + amount + " by Wallet";
        print("case 4", new InstorePayment(wallet).makePayment(-50),
                "instore: paid -50 by Wallet");
    }
}
