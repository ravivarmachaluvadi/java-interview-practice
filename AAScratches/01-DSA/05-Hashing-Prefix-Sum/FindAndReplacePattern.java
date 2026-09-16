import java.util.ArrayList;
import java.util.List;

// https://leetcode.com/problems/find-and-replace-pattern/description/
// 890. Find and Replace Pattern
class FindAndReplacePattern {
    // Output: [mee, aqq]
    public static void main(String[] args) {
        FindAndReplacePattern solution = new FindAndReplacePattern();
        String[] words = {"xyz","mee", "deq", "abc", "aqq", "dkd", "ccc"};
        String pattern = "abb";
        List<String> result = solution.findAndReplacePattern(words, pattern);
        System.out.println(result);
    }

    public List<String> findAndReplacePattern(String[] words, String pattern) {
        List<String> res = new ArrayList<>();
        for (String word : words) {
            if (check(word, pattern)) res.add(word);
        }
        return res;
    }

    boolean check(String a, String b) {
        for (int i = 0; i < a.length(); i++) {
            if (a.indexOf(a.charAt(i)) != b.indexOf(b.charAt(i))) return false;
        }
        return true;
    }
}
