/**
 * Determines the strength of a password based on length and character composition.
 *
 * The algorithm checks for presence of lowercase letters, uppercase letters,
 * digits, and special characters (!@#$%^&*()). A password is classified as:
 * - "Strong" if it has at least 8 characters and all four categories.
 * - "Moderate" if it has at least 6 characters with lowercase, uppercase, and a special character.
 * - Otherwise, the password is considered "Weak".
 *
 * Time Complexity: O(n) where n is the length of the password (single pass).
 * Space Complexity: O(1) – only constant auxiliary variables are used.
 */
class PasswordStrengthChecker {
    public static void main(String[] args) {
        String password1 = "gfg!@12";
        String password2 = "SapientGlobalMarkets!@12";
        System.out.println("Password: " + password1 + " - Strength: " +
                checkPasswordStrength(password1));
        System.out.println("Password: " + password2 + " - Strength: " +
                checkPasswordStrength(password2));
    }

    public static String checkPasswordStrength(String password) {
        // Criteria flags
        boolean hasLower = false;
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        int length = password.length();

        for (char ch : password.toCharArray()) {
            // if else if setter for flags
            if (Character.isLowerCase(ch)) {
                hasLower = true;
            } else if (Character.isUpperCase(ch)) {
                hasUpper = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if ("!@#$%^&*(".indexOf(ch) >= 0) {
                hasSpecial = true;
            }
        }

        // if else if
        if (length >= 8 && hasLower && hasUpper && hasDigit && hasSpecial) {
            return "Strong";
        } else if (length >= 6 && hasLower && hasUpper && hasSpecial) {
            return "Moderate";
        } else {
            return "Weak";
        }
    }
}
