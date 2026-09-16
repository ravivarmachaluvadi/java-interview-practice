import java.util.Scanner;

/**
 * Bridge Pattern is a structural design pattern that
 * <p>
 * decouples abstraction from its implementation,
 * <p>
 * so that both can vary independently.
 */
// Colour and Shape
interface PaymentMethod {
    void pay(int amount);
}

class CreditCardPayment implements PaymentMethod {

    @Override
    public void pay(int amount) {
        System.out.println("Paid " + amount + " using Credit Card");
    }
}

class UpiPayment implements PaymentMethod {

    @Override
    public void pay(int amount) {
        System.out.println("Paid " + amount + " using UPI");
    }
}

abstract class Payment {
    protected PaymentMethod paymentMethod;

    abstract void makePayment(int amount);
}

class OnlinePayment extends Payment {

    OnlinePayment(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    void makePayment(int amount) {
        System.out.println("Making online payment");
        paymentMethod.pay(amount);

    }
}

class InstorePayment extends Payment {

    InstorePayment(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    void makePayment(int amount) {
        System.out.println("Making instore payment");
        paymentMethod.pay(amount);

    }
}

public class BridgePatternExample {
    public static void main(String[] args) {
        Payment onlinePayment = new OnlinePayment(new UpiPayment());
        Payment instorePayment = new InstorePayment(new CreditCardPayment());

        Scanner sc = new Scanner(System.in);
        int amount1 = sc.nextInt();
        onlinePayment.makePayment(amount1);
        int amount2 = sc.nextInt();
        instorePayment.makePayment(amount2);
    }
}
