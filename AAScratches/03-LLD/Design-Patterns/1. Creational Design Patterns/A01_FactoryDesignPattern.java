/*
 * =====================================================================
 *  Factory - payment processor per checkout request   Creational | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Checkout receives {"method": "UPI", "amount": 1200}. The method code
 *   decides which processor runs: UPI, card (adds a 2% fee) or cash on
 *   delivery (refused above Rs 50,000). Checkout must not `new` any of them,
 *   and launching BNPL next quarter must not mean editing a switch.
 *
 * KEY INSIGHT
 *   The factory owns the "code -> class" decision, and a registry of
 *   Suppliers replaces the if/else chain: a new method is one register()
 *   call. The return type is the interface, so checkout never names a
 *   concrete processor.
 *
 * ROLES IN THIS CODE
 *   PaymentProcessor                  Product
 *   UpiProcessor, Card.., Cod..       ConcreteProduct
 *   PaymentProcessorFactory           Factory (registry of Suppliers)
 *   main()                            Client (the checkout)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Unknown code: throw in the factory; returning null moves the crash.
 *   - Spring does this for you: inject Map<String, PaymentProcessor> and
 *     the bean names become the keys.
 *   - Factory vs Abstract Factory: one product here, a matched family of
 *     products in C01_AbstractFactoryPattern.
 *
 * RUN
 *   5 cases: UPI, card fee, COD over limit, unknown code, BNPL registered.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Product: the only type checkout knows. */
interface PaymentProcessor {
    /** Returns a receipt line, or throws if this method cannot take the amount. */
    String pay(int amountRs);
}

class UpiProcessor implements PaymentProcessor {
    public String pay(int amountRs) {
        return "UPI collect request for Rs " + amountRs;
    }
}

class CardProcessor implements PaymentProcessor {
    public String pay(int amountRs) {
        int fee = amountRs * 2 / 100; // gateway fee passed on to the customer
        return "Card charged Rs " + (amountRs + fee) + " (fee Rs " + fee + ")";
    }
}

class CodProcessor implements PaymentProcessor {
    static final int LIMIT_RS = 50_000;

    public String pay(int amountRs) {
        if (amountRs > LIMIT_RS) {
            throw new IllegalStateException("COD not allowed above Rs " + LIMIT_RS);
        }
        return "COD booked, collect Rs " + amountRs + " at the door";
    }
}

/** Factory: the only place a method code turns into a class. */
class PaymentProcessorFactory {
    private final Map<String, Supplier<PaymentProcessor>> registry = new HashMap<>();

    PaymentProcessorFactory() {
        register("UPI", UpiProcessor::new);
        register("CARD", CardProcessor::new);
        register("COD", CodProcessor::new);
    }

    /** Open for extension: a new method is a register() call, not an edited switch. */
    void register(String code, Supplier<PaymentProcessor> supplier) {
        registry.put(code, supplier);
    }

    PaymentProcessor forMethod(String code) {
        Supplier<PaymentProcessor> supplier = registry.get(code);
        if (supplier == null) {
            throw new IllegalArgumentException("Unsupported payment method " + code);
        }
        return supplier.get();
    }
}

class FactoryDesignPattern {

    public static void main(String[] args) {
        PaymentProcessorFactory factory = new PaymentProcessorFactory();

        // Case 1-2: typical. The client holds a PaymentProcessor, never a UpiProcessor.
        print("case 1 UPI      ", factory.forMethod("UPI").pay(1200),
                "UPI collect request for Rs 1200");
        print("case 2 card fee ", factory.forMethod("CARD").pay(1000),
                "Card charged Rs 1020 (fee Rs 20)");

        // Case 3: the product enforces its own business rule.
        print("case 3 COD limit", attempt(() -> factory.forMethod("COD").pay(60_000)),
                "IllegalStateException COD not allowed above Rs 50000");

        // Case 4: edge. An unknown code fails here, at the real fault line.
        print("case 4 unknown  ", attempt(() -> factory.forMethod("BNPL").pay(500)),
                "IllegalArgumentException Unsupported payment method BNPL");

        // Case 5: BNPL launches. One register() call; factory and checkout untouched.
        factory.register("BNPL", () -> amount -> "BNPL 3 EMIs of Rs " + amount / 3);
        print("case 5 BNPL     ", factory.forMethod("BNPL").pay(3000), "BNPL 3 EMIs of Rs 1000");
    }

    private static String attempt(Supplier<String> call) {
        try {
            return call.get();
        } catch (RuntimeException e) {
            return e.getClass().getSimpleName() + " " + e.getMessage();
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
