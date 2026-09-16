/**
 * The Chain of Responsibility (CoR) pattern is a behavioral
 * <p>
 * design pattern that lets you pass a request along a chain of handlers.
 * <p>
 * Each handler decides either to process the request or pass it
 * <p>
 * to the next handler in the chain.
 */
// 1️⃣ Define the Handler
abstract class DispenseChain {
    protected DispenseChain nextChain;

    public void setDispenseChain(DispenseChain nextChain) {
        this.nextChain = nextChain;
    }

    public abstract void dispense(int amount);
}

class Rupee2000Dispenser extends DispenseChain {

    @Override
    public void dispense(int amount) {
        if (amount >= 2000) {
            int num = amount / 2000;
            int rem = amount % 2000;
            System.out.println("Dispensing " + num + " 2000 Notes");
            if (rem != 0 && nextChain != null) {
                nextChain.dispense(rem);
            }
        } else if (nextChain != null) {
            nextChain.dispense(amount);
        }
    }
}

class Rupee500Dispenser extends DispenseChain {

    @Override
    public void dispense(int amount) {
        if (amount >= 500) {
            int num = amount / 500;
            int rem = amount % 500;
            System.out.println("Dispensing " + num + " 500 Notes");
            if (rem != 0 && nextChain != null) {
                nextChain.dispense(rem);
            }
        } else if (nextChain != null) {
            nextChain.dispense(amount);
        }
    }
}

class Rupee200Dispenser extends DispenseChain {

    @Override
    public void dispense(int amount) {
        if (amount >= 200) {
            int num = amount / 200;
            int rem = amount % 200;
            System.out.println("Dispensing " + num + " 200 Notes");
            if (rem != 0 && nextChain != null) {
                nextChain.dispense(rem);
            }
        } else if (nextChain != null) {
            nextChain.dispense(amount);
        }
    }
}


class Rupee100Dispenser extends DispenseChain {

    @Override
    public void dispense(int amount) {
        if (amount >= 100) {
            int num = amount / 100;
            int rem = amount % 100;
            System.out.println("Dispensing " + num + " 100 Notes");
            if (rem != 0 && nextChain != null) {
                nextChain.dispense(rem);
            }
        } else if (nextChain != null) {
            nextChain.dispense(amount);
        }
    }
}


class ChainOfResponsibilityExample {

    private DispenseChain dispenseChain;

    public ChainOfResponsibilityExample() {
        this.dispenseChain = new Rupee2000Dispenser();
        Rupee500Dispenser rupee500Dispenser = new Rupee500Dispenser();
        Rupee200Dispenser rupee200Dispenser = new Rupee200Dispenser();
        Rupee100Dispenser rupee100Dispenser = new Rupee100Dispenser();
        this.dispenseChain.setDispenseChain(rupee500Dispenser);
        rupee500Dispenser.setDispenseChain(rupee200Dispenser);
        rupee200Dispenser.setDispenseChain(rupee100Dispenser);
    }

    public void dispenseCash(int amount) {
        if (amount % 100 != 0) {
            System.out.println("Amount should be multiple of 100.");
            return;
        }
        dispenseChain.dispense(amount);
    }

    public static void main(String[] args) {
        ChainOfResponsibilityExample atm = new ChainOfResponsibilityExample();

        atm.dispenseCash(8700);
        System.out.println("----");
        atm.dispenseCash(2500);
        System.out.println("----");
        atm.dispenseCash(3800);
    }
}
