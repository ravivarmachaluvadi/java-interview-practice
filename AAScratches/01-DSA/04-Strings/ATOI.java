import static java.lang.Character.isDigit;

/**
 * Input: s = "-1337c0d3"
 * <p>
 * Output: 1337
 */
class ATOI {
    public static int myAtoi(String s) {
        s = s.strip();
        if (s.isEmpty()) return 0;
        boolean isNegative = false;
        int index = 0, result = 0;
        char charAt = s.charAt(0);
        if (charAt == '-' || charAt == '+') {
            if (charAt == '-') isNegative = true;
            index++;
        }

        while (index < s.length()) {
            char ch = s.charAt(index);
            if (!isDigit(ch)) {
                break;
            }
            int intVal = ch - '0';
            // by 10 as we have to multiply by 10 -intVal as we do +intVal
            // (Integer.MAX_VALUE - intVal) / 10) remember
            // ans is ovlerflowed
            if (result > (Integer.MAX_VALUE - intVal) / 10) {
                return isNegative ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            }
            // better use full operator rather shortHand
            result = (result * 10) + intVal;
            index++;
        }
        return isNegative ? -result : result;
    }

    public static void main(String[] args) {
        System.out.println(myAtoi("+1")); // 1
        System.out.println(myAtoi("1337c0d3")); // 1337
        System.out.println(myAtoi(" -042")); // -42
    }
}
