/*
 * =====================================================================
 *  Password Strength Checker                        GeeksforGeeks | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a password string, classify it as "Strong", "Moderate" or "Weak".
 *   Strong   = length >= 8 and has lowercase, uppercase, digit AND special char.
 *   Moderate = length >= 6 and has lowercase, uppercase AND special char.
 *   Weak     = anything else. Special characters are ! @ # $ % ^ & * ( ).
 *
 * EXAMPLE
 *   "SapientGlobalMarkets!@12"  ->  Strong     all four kinds, length 24
 *   "gfg!@12"                   ->  Weak       no uppercase letter
 *   "Abc!@x"                    ->  Moderate   length 6, lower+upper+special, no digit
 *   ""                          ->  Weak       nothing present
 *   "Abcdef1)"                  ->  Strong     ')' counts as special (see Fixed)
 *
 * APPROACH  (single-pass boolean flags)
 *   1. Start four flags at false: hasLower, hasUpper, hasDigit, hasSpecial.
 *   2. Scan the password once; each character flips at most one flag.
 *   3. After the scan, test the rules from strongest to weakest and return the
 *      first one that matches. Order matters: Strong implies Moderate.
 *
 * KEY INSIGHT
 *   Accumulate facts in one pass, decide at the end. A flag is "sticky": once
 *   true it never goes back, so the scan never needs to look backwards.
 *   The same shape scales to ValidNumber (D01_ValidNumber), where flags become states.
 *
 * Fixed: the special-character set was "!@#$%^&*(" and silently dropped ')',
 *   so a password whose only special char was ')' was rated Weak.
 *
 * COMPLEXITY
 *   Time  O(n)  one scan of the password
 *   Space O(1)  four booleans and a length
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the minimum number of edits to make a password strong (LC 420):
 *     that adds run-length rules (no 3 repeats) and becomes a hard greedy.
 *   - Should Character.isLetterOrDigit and Unicode categories replace the
 *     hard-coded special set? Depends on whether non-ASCII input is allowed.
 *   - Compute a numeric score instead of three buckets (entropy estimate).
 *
 * RUN
 *   main() runs 5 cases (typical, edge, tricky) and prints actual vs expected.
 */
class PasswordStrengthChecker {

    private static final String SPECIAL_CHARS = "!@#$%^&*()";

    public static String checkPasswordStrength(String password) {
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char ch : password.toCharArray()) {
            // each character belongs to at most one category, so else-if is enough
            if (Character.isLowerCase(ch)) {
                hasLower = true;
            } else if (Character.isUpperCase(ch)) {
                hasUpper = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (SPECIAL_CHARS.indexOf(ch) >= 0) {
                hasSpecial = true;
            }
        }

        int length = password.length();
        // strongest rule first, since Strong also satisfies Moderate
        if (length >= 8 && hasLower && hasUpper && hasDigit && hasSpecial) {
            return "Strong";
        }
        if (length >= 6 && hasLower && hasUpper && hasSpecial) {
            return "Moderate";
        }
        return "Weak";
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        String strong = "SapientGlobalMarkets!@12";
        print("case 1 \"" + strong + "\"", checkPasswordStrength(strong), "Strong");
        print("case 2 \"gfg!@12\" (no upper)", checkPasswordStrength("gfg!@12"), "Weak");
        print("case 3 \"Abc!@x\" (len 6, no digit)", checkPasswordStrength("Abc!@x"), "Moderate");
        print("case 4 \"\" (empty)", checkPasswordStrength(""), "Weak");
        print("case 5 \"Abcdef1)\" (special is ')')", checkPasswordStrength("Abcdef1)"), "Strong");
    }
}
