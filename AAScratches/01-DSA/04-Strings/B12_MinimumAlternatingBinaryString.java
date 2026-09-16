// 1758. Minimum Changes To Make Alternating Binary String
// https://leetcode.com/problems/minimum-changes-to-make-alternating-binary-string/description/
class MinimumAlternatingBinaryString {
    public static int minOperations(String s) {
        int n = s.length();
        // cost0 = cost to make pattern "0101…"
        // cost1 = cost to make pattern "1010…"
        int cost0 = 0, cost1 = 0;

        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
//            System.out.println('1' % 2);// 1
//            System.out.println('0' % 2);// 0
            char expected0 = (i % 2 == 0) ? '0' : '1';
            char expected1 = (i % 2 == 0) ? '1' : '0';

            if (c != expected0) cost0++;
            if (c != expected1) cost1++;
        }
        return Math.min(cost0, cost1);
    }

    public static void main(String[] args) {
        // Example tests
        System.out.println(minOperations("0100"));   // 1
        System.out.println(minOperations("10"));     // 0
        System.out.println(minOperations("1111"));   // 2
        System.out.println(minOperations("0001010111")); // 2
    }

}
