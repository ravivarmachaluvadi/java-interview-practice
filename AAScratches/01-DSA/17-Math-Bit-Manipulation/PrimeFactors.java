/**
 * Problem: Return the distinct prime factors of a given integer n.
 *
 * Approach: Iterate i from 2 to n; if i divides n, add it to the list and
 * repeatedly divide n by i until no longer divisible. This yields each prime
 * factor exactly once.
 *
 * Time Complexity: O(√n) in practice (worst‑case O(n) when n is prime).
 * Space Complexity: O(k), where k is the number of distinct prime factors,
 * since we store them in an ArrayList.
 */
import java.util.ArrayList;

class PrimeFactors {
    public static ArrayList<Integer> getPrimeFactors(int n) {
        ArrayList<Integer> primeFactors = new ArrayList<>();
        for (int i = 2; i <= n; i++) {
            if (n % i == 0) primeFactors.add(i);
            while (n % i == 0) n = n / i;
        }
        return primeFactors;
    }

    public static void main(String[] args) {
        int n = 60;
        ArrayList<Integer> ans = getPrimeFactors(n);
        System.out.print("Prime Factors for " + n + ": ");
        for (int factor : ans) {
            System.out.print(factor + " ");
        }
        System.out.println();
    }
}
