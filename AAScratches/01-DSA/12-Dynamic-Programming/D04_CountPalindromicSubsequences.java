class CountPalindromicSubsequences {
    //allowed length of palindrom 5
    static int MOD = 1000000007;

    public int countPalindromes(String s) {
        Integer[][][][] memo = new Integer[s.length()][10][10][5];
        //index can be till string length, 10 digits so use 10 in for first and second character and 5 is only allowed length.
        return solve(s, 0, 0, 0, 0, memo);
    }

    int solve(String s, int index, int first, int second, int length, Integer[][][][] memo) {
        if (length == 5) {
            return 1;//palindrome achived count it
        }
        if (index == s.length()) {
            return 0;//not possible to achive palindrome
        }
        if (memo[index][first][second][length] != null) {
            return memo[index][first][second][length];
        }
        int value = s.charAt(index) - '0';

        int include = 0;
        if (length == 0) {
            include = solve(s, index + 1, value, second, length + 1, memo);//will be matched with 5th char
        } else if (length == 1) {
            include = solve(s, index + 1, first, value, length + 1, memo);//will be matched with 4th char
        } else if (length == 2) {
            include = solve(s, index + 1, first, second, length + 1, memo);// middle char of 5 letter palindrom, doe s not need matching
        } else if (length == 3 && value == second) {//check if same as second char
            include = solve(s, index + 1, first, second, length + 1, memo);
        } else if (length == 4 && value == first) {//check if same as first char
            include = solve(s, index + 1, first, value, length + 1, memo);
        }

        //ignore the current char and move to next char like we do not subsequences matching
        int exclude = solve(s, index + 1, first, second, length, memo);

        int answer = (include % MOD) + (exclude % MOD);//add both results
        return memo[index][first][second][length] = answer % MOD;
    }

    public static void main(String[] args) {
        CountPalindromicSubsequences solver = new CountPalindromicSubsequences();
        String input = "12321";
        int result = solver.countPalindromes(input);
        System.out.println("Input string: " + input);
        System.out.println("Number of palindromic subsequences of length 5: " + result);
    }
}