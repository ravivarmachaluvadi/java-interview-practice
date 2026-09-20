/*
 * =====================================================================
 *  Strategy Design Pattern                  LLD | Easy  MUST-KNOW
 * =====================================================================
 *
 * PATTERN
 *   Strategy - Behavioral family (GoF). Also called Policy.
 *
 * INTENT
 *   Define a family of interchangeable algorithms, put each one in its own class,
 *   and let the client swap them at runtime without touching the code that uses them.
 *   The algorithm varies independently of the context that runs it.
 *
 * WHEN TO USE, WHEN NOT
 *   Use when: one operation has several interchangeable implementations chosen at
 *     runtime (payment mode, compression codec, pricing rule, retry policy), and you
 *     want to kill the if/else-on-type ladder that keeps growing.
 *   Do not use when: the variants never change, or there are only two and they will
 *     stay two - a lambda or a boolean is cheaper than four extra classes.
 *
 * ROLES IN THIS CODE
 *   PaymentStrategy      -> Strategy       (the common interface: pay(amount))
 *   CreditCardPayment    -> ConcreteStrategy
 *   PayPalPayment        -> ConcreteStrategy
 *   UpiPayment           -> ConcreteStrategy
 *   PaymentContext       -> Context        (holds a strategy, delegates to it,
 *                                           never knows which one it holds)
 *   StrategyDesignPattern.main -> Client   (picks the strategy and injects it)
 *
 * KEY INSIGHT
 *   Behaviour becomes a field. Once "how to pay" is an object you can assign,
 *   adding a new payment mode is a new class, not an edit to PaymentContext -
 *   open/closed in one move. Every later behavioural pattern is Strategy plus
 *   one twist: Command binds a receiver, State lets the strategy pick its own
 *   successor, Chain lines several of them up.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Strategy vs Template Method: composition (swap at runtime) vs inheritance
 *     (subclass fills fixed hooks, chosen at compile time).
 *   - Strategy vs State: the client chooses the Strategy; a State chooses the next State.
 *   - In modern Java, a single-method Strategy is just a lambda or Comparator - when
 *     is a named class still worth it? (it carries fields, a name, and unit tests)
 *   - Where in Spring? Different AuthenticationProvider / PasswordEncoder beans.
 *
 * RUN
 *   main() runs 4 cases (three payment modes plus the "no strategy selected" edge)
 *   and prints actual vs expected.
 *
 * Fixed: main() passed the constructor arguments to CreditCardPayment in the wrong
 *        order, so the receipt printed the holder's name where the card number belongs.
 */

interface PaymentStrategy {
    /** Returns the receipt line instead of printing, so main() can verify it. */
    String pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {

    private final String name;
    private final String cardNumber;

    public CreditCardPayment(String name, String cardNumber) {
        this.name = name;
        this.cardNumber = cardNumber;
    }

    @Override
    public String pay(double amount) {
        return String.format("%.2f paid using Credit Card %s (%s)", amount, cardNumber, name);
    }
}

class PayPalPayment implements PaymentStrategy {

    private final String email;

    public PayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public String pay(double amount) {
        return String.format("%.2f paid using PayPal account %s", amount, email);
    }
}

class UpiPayment implements PaymentStrategy {

    private final String upiId;

    public UpiPayment(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public String pay(double amount) {
        return String.format("%.2f paid using UPI ID %s", amount, upiId);
    }
}

/** Context: owns a strategy reference and delegates. It never names a concrete mode. */
class PaymentContext {

    private PaymentStrategy paymentStrategy;

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    String payBill(double amount) {
        // Guard: the context is useless until the client injects a strategy.
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment method not selected");
        }
        return paymentStrategy.pay(amount);
    }
}

class StrategyDesignPattern {

    public static void main(String[] args) {
        PaymentContext context = new PaymentContext();

        // Case 1: credit card. Constructor order is (name, cardNumber).
        context.setPaymentStrategy(new CreditCardPayment("A. Customer", "4111-1111-1111-1111"));
        print("case 1 credit card", context.payBill(2500d),
                "2500.00 paid using Credit Card 4111-1111-1111-1111 (A. Customer)");

        // Case 2: same context, different algorithm - swapped at runtime.
        context.setPaymentStrategy(new PayPalPayment("a.customer@example.com"));
        print("case 2 paypal", context.payBill(1500.0),
                "1500.00 paid using PayPal account a.customer@example.com");

        // Case 3: a third mode; PaymentContext was not edited to add any of them.
        context.setPaymentStrategy(new UpiPayment("a.customer@examplebank"));
        print("case 3 upi", context.payBill(500D),
                "500.00 paid using UPI ID a.customer@examplebank");

        // Case 4 (edge): a fresh context with no strategy injected must fail loudly.
        String result;
        try {
            result = new PaymentContext().payBill(100.0);
        } catch (IllegalStateException e) {
            result = "IllegalStateException: " + e.getMessage();
        }
        print("case 4 no strategy", result, "IllegalStateException: Payment method not selected");
    }

    private static void print(String label, Object actual, Object expected) {
        boolean ok = String.valueOf(actual).equals(String.valueOf(expected));
        System.out.println(label + ": " + actual + "   expected " + expected
                + "   " + (ok ? "[OK]" : "[FAIL]"));
    }
}
