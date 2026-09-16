// https://leetcode.com/problems/max-difference-you-can-get-from-changing-an-integer/description/
// 1432. Max Difference You Can Get From Changing an Integer
class MaxDifferenceChangingInteger {

    public static int maxDiff(int num) {
        String s = String.valueOf(num);

        // 1) Build the maximum possible value a by replacing the first non-‘9’ digit with ‘9’
        String a = s;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '9') {
                // replace all occurrences of c with '9'
                a = s.replace(c, '9');
                break;
            }
        }

        // 2) Build the minimum possible value b by replacing appropriately:
        // - If first digit != ‘1’, replace all occurrences of first digit with ‘1’
        // - Else (first digit is ‘1’), find first char after that which
        // is > '1', replace all its occurrences with '0'
        String b = s;
        if (s.charAt(0) != '1') {
            char first = s.charAt(0);
            b = s.replace(first, '1');
        } else {
            for (int i = 1; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c != '0' && c != '1') {
                    b = s.replace(c, '0');
                    break;
                }
            }
        }

        int maxVal = Integer.parseInt(a);
        int minVal = Integer.parseInt(b);
        return maxVal - minVal;
    }

    public static void main(String[] args) {
        // Example 1:
        int num1 = 555;
        int result1 = maxDiff(num1);
        System.out.println("Input: " + num1 + " → Output: " + result1);
        // Expected: 888 (999 − 111)

        // Example 2:
        int num2 = 9;
        int result2 = maxDiff(num2);
        System.out.println("Input: " + num2 + " → Output: " + result2);
        // Expected: 8 (9 − 1)

        // You can add more cases to test
    }
}
