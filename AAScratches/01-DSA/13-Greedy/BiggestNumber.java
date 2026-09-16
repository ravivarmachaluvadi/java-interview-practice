/**
 * Problem: Given an array of non‑negative integers, arrange them to form the largest possible integer when concatenated.
 *
 * Approach: Convert each integer to a string and sort using a custom comparator that orders two strings
 * by comparing the concatenations "a+b" vs. "b+a". After sorting in descending order, concatenate all strings.
 * If the highest value is "0", return "0" to avoid leading zeros.
 *
 * Time Complexity: O(n log n) for sorting, where n is the number of integers.
 * Space Complexity: O(n) additional space for string array and result builder.
 */
import java.util.*;

class BiggestNumber {
    public static String largestNumber(Integer[] numbers) {
        String[] strNumbers = Arrays.stream(numbers)
                .map(String::valueOf)
                .toArray(String[]::new);

        // Sort numbers based on custom comparator
        Arrays.sort(strNumbers, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                String order1 = a + b;
                String order2 = b + a;
                // Descending order
                return order2.compareTo(order1);
            }
        });

        // If the largest number is "0", the result is "0"
        if (strNumbers[0].equals("0"))
            return "0";

        StringBuilder largestNumber = new StringBuilder();
        for (String num : strNumbers) {
            largestNumber.append(num);
        }

        return largestNumber.toString();
    }

    public static void main(String[] args) {
        Integer[] numbers = {1, 34, 3, 98, 9, 76, 45, 4};
        String result = largestNumber(numbers);
        System.out.println("Largest number: " + result); // 998764543431
    }
}
