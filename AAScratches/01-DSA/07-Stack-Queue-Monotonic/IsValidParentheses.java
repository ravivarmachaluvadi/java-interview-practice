class IsValidParentheses {
    public static void main(String[] args) {
        System.out.println(isValid("()()()()"));
    }

    private static boolean isValid(String s) {
        int balance = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
            }
            // balance is negative because we got
            // closed one with out opening one
            // opening one not a issue at start because
            // closed one may appear in  future
            if (balance < 0) {
                return false;
            }
        }
        return balance == 0;
    }
}