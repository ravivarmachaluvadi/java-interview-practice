import java.util.ArrayList;
import java.util.List;

/**
 * At a lemonade stand, each lemonade costs $5. Customers are standing in a queue to
 * buy from you and order one at a time (in the order specified by bills).
 * Each customer will only buy one lemonade and pay with either a $5, $10, or $20 bill.
 * You must provide the correct change to each customer so that the net transaction is that
 * the customer pays $5.
 * <p>
 * Note that you do not have any change in hand at first.
 * <p>
 * Given an integer array bills where bills[i] is the bill the ith customer pays,
 * return true if you can provide every customer with the correct change, or false otherwise.
 */
// variation of fizz-buzz(nested) logic
class LemonadeChange {

    public static boolean lemonadeChange(List<Integer> bills) {
        int fivesInGallaPette = 0;
        int tensInGallaPette = 0;
        for (int i = 0; i < bills.size(); i++) {
            if (bills.get(i) == 5) {
                fivesInGallaPette++;
            } else if (bills.get(i) == 10) {
                if (fivesInGallaPette > 0) {
                    fivesInGallaPette--;
                    tensInGallaPette++;
                } else return false;
            } else {
                if (fivesInGallaPette > 0 && tensInGallaPette > 0) {
                    fivesInGallaPette--;
                    tensInGallaPette--;
                } else if (fivesInGallaPette >= 3) {
                    fivesInGallaPette -= 3;
                } else return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        List<Integer> bills = new ArrayList<>();
        bills.add(5);
        bills.add(5);
        bills.add(5);
        bills.add(10);
        bills.add(20);

        System.out.print("Queues of customers: ");
        for (int bill : bills) {
            System.out.print(bill + " ");
        }
        System.out.println();

        boolean ans = lemonadeChange(bills);
        if (ans)
            System.out.println("It is possible to provide change for all customers.");
        else
            System.out.println("It is not possible to provide change for all customers.");
    }
}

