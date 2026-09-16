import java.util.*;

// https://leetcode.com/problems/permutation-sequence/description/
// 60. Permutation Sequence
class KthPermutation {
    // 213
    static String getPermutation(int n, int k) {
        // add 1 to n-1 numbers to list and find factorial
        // for n-1 elements exclude nth element ∋ k>factorial
        int fact = 1;
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 1; i < n; i++) {
            fact = fact * i;
            numbers.add(i);
        }
        numbers.add(n);
        // K is zero indexed
        StringBuilder ans = new StringBuilder();
        k = k - 1;
        while (true) {
            ans.append(numbers.get(k / fact));
            numbers.remove(k / fact);
            if (numbers.isEmpty()) {
                break;
            }
            k = k % fact;
            fact = fact / numbers.size();
        }
        return ans.toString();
    }

    public static void main(String[] args) {
        int n = 3, k = 3;
        String ans = getPermutation(n, k);
        System.out.println("The Kth permutation sequence is " + ans);
    }
}
