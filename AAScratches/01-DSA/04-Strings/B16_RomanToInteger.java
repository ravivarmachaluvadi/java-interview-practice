import java.util.*;

class RomanToInteger {
    public int romanToInt(String s) {
// Map Roman numerals to their integer values
        Map<Character, Integer> romanMap = new HashMap<>();
        romanMap.put('I', 1);
        romanMap.put('V', 5);
        romanMap.put('X', 10);
        romanMap.put('L', 50);
        romanMap.put('C', 100);
        romanMap.put('D', 500);
        romanMap.put('M', 1000);
        int total = 0;
        int prevValue = 0;
// Traverse the string from right to left
        for (int i = s.length() - 1; i >= 0; i--) {
            int currentValue = romanMap.get(s.charAt(i));
            if (currentValue < prevValue) {
// Subtract if current is less than previous
                total -= currentValue;
            } else {
// Add if current is greater or equal to previous
                total += currentValue;
            }
            prevValue = currentValue;
        }
        return total;
    }

    public static void main(String[] args) {
        RomanToInteger converter = new RomanToInteger();

        String roman = "MCMXCIV"; // 1994
        int result = converter.romanToInt(roman);

        System.out.println("The integer value of Roman numeral " + roman + " is: " + result);
    }
}
