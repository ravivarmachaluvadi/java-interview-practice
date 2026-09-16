/**
 * Problem: Convert a fraction (numerator / denominator) into its decimal string representation,
 * inserting parentheses around the repeating part if the decimal is recurring.
 *
 * Approach: Perform long division manually, tracking each remainder and its position in the result
 * using a HashMap. When a remainder repeats, insert '(' at the first occurrence and ')' at the end.
 *
 * Time Complexity: O(k) where k is the number of digits produced until termination or repetition,
 * bounded by the denominator's size.
 * Space Complexity: O(k) for the output string plus O(min(denominator, k)) for the remainder map.
 */
import java.util.HashMap;
import java.util.Map;

class RepeatedNumberInFractionAfterDecimal {

    public static String fractionRepresentation(int num, int den) {
        if (num == 0) return "0";
        StringBuilder result = new StringBuilder();
        // Handle negative results
        if (num < 0 ^ den < 0) {
            result.append("-");
        }
        num = Math.abs(num);
        den = Math.abs(den);
        result.append(num / den);

        // Get the remainder
        int remainder = num % den;
        if (remainder == 0) {
            return result.toString();  // No fractional part
        }
        result.append(".");

        // Map to store remainder positions
        Map<Integer, Integer> remainderMap = new HashMap<>();
        while (remainder != 0) {
            // If the remainder repeats, there's a repeating decimal part
            if (remainderMap.containsKey(remainder)) {
                int start = remainderMap.get(remainder);
                // remember insert
                result.insert(start, "(");
                result.append(")");
                break;
            }
            // Store remainder position 1/3
            // 10/3=3, 1-> (1*10)/3
            remainderMap.put(remainder, result.length());
            // now deal with remainder as num
            remainder *= 10;
            result.append(remainder / den);
            remainder %= den;
        }
        return result.toString();
    }

    public static void main(String[] args) {
        System.out.println(fractionRepresentation(6, 11)); // Expected: "0.(54)"
        System.out.println(fractionRepresentation(1, 6)); // Expected: "0.1(6)"
        System.out.println(fractionRepresentation(1, 2));  // Expected: "0.5"
        System.out.println(fractionRepresentation(1, 3));  // Expected: "0.(3)"
    }
}

