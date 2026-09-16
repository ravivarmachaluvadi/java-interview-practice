import java.util.Arrays;
import java.util.List;

/**
 * The Proxy Pattern is a structural design pattern that
 * <p>
 * provides a substitute or placeholder for another object
 * <p>
 * to control access to it.
 * <p>
 * A Proxy acts as a middleman between the client and the real object.
 * <p>
 * Control access to an object (like adding security, caching, or logging)
 * <p>
 * Lazy initialization — create the real object only when needed
 * <p>
 * Remote access — represent a remote object locally
 * <p>
 * Enhance behavior — without modifying the actual class
 */
interface Bank {
    void withdraw(String accountNumber);
}

class RealBank implements Bank {

    @Override
    public void withdraw(String accountNumber) {
        System.out.println("Withdrawing money from account: " + accountNumber);
    }
}

class BankProxy implements Bank {

    private RealBank realBank;

    private static final List<String> allowedAccounts = Arrays.asList("ACC123", "ACC456");

    @Override
    public void withdraw(String accountNumber) {
        if (allowedAccounts.contains(accountNumber)) {
            if (realBank == null)
                realBank = new RealBank();// lazy initialization
            System.out.println("Access granted for accountNumber : " + accountNumber);
            realBank.withdraw(accountNumber);
        } else {
            System.out.println("Access denied for account: " + accountNumber);
        }
    }
}

class ProxyDesignPattern {
    public static void main(String[] args) {
        Bank bank = new BankProxy();
        bank.withdraw("ACC123");
        bank.withdraw("ACC128");

    }

}
