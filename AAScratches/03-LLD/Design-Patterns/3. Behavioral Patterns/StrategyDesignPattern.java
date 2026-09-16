/**
 * The Strategy pattern is a behavioral design pattern that
 * <p>
 * allows you to define a family of algorithms, encapsulate
 * <p>
 * each one, and make them interchangeable.
 * <p>
 * It lets the algorithm vary independently of the clients that use it.
 */
// Real-Time Use Cases in Industry
//Spring Security: Different authentication mechanisms (JWT, OAuth2, LDAP) use Strategy.
//Sorting algorithms: Different comparator strategies for sorting collections.
//Compression utilities: Different compression strategies (ZIP, GZIP).
//Logging frameworks: Different logging output strategies (Console, File, DB).
interface PaymentStrategy {
    void pay(double amount);
}

class CreditCardPayment implements PaymentStrategy {

    private final String name;
    private final String cardNumber;

    public CreditCardPayment(String name, String cardNumber) {
        this.name = name;
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(double amount) {
        System.out.println(amount + " paid using Credit Card: " + cardNumber);
    }
}

class PayPalPayment implements PaymentStrategy {

    private final String email;

    public PayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public void pay(double amount) {
        System.out.println(amount + " paid using PayPal account: " + email);
    }
}

class UpiPayment implements PaymentStrategy {
    private final String upiId;

    public UpiPayment(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public void pay(double amount) {
        System.out.println(amount + " paid using UPI ID: " + upiId);
    }
}

class PaymentContext {
    private PaymentStrategy paymentStrategy;

    void payBill(Double amount) {
        if (paymentStrategy == null) {
            throw new IllegalStateException("Payment method not selected");
        }
        paymentStrategy.pay(amount);
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
}

public class StrategyDesignPattern {
    public static void main(String[] args) {
        PaymentContext context = new PaymentContext();

        // Pay using Credit Card
        context.setPaymentStrategy(new CreditCardPayment("1234-5678-9876-5432", "Ravi Varma"));
        context.payBill(2500d);

        // Pay using PayPal
        context.setPaymentStrategy(new PayPalPayment("ravi.varma@gmail.com"));
        context.payBill(1500.0);

        // Pay using UPI
        context.setPaymentStrategy(new UpiPayment("ravi@okicici"));
        context.payBill(500D);
    }
}
