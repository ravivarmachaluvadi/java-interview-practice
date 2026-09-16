import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 187. Repeated DNA Sequences
// https://leetcode.com/problems/repeated-dna-sequences/
class RepeatedDnaSequences {
    public static List<String> findRepeatedDnaSequences(String s) {

        int n = s.length();
        if (n < 10 || n > 10000)
            return new ArrayList<>();

        Set<String> seen = new HashSet<>();
        Set<String> repeats = new HashSet<>();
        // i ∈ 0..9 -> represents 10 characters
        for (int i = 0; i + 9 < n; i++) {
            // i+10 -> endBound is exclusive
            String sub = s.substring(i, i + 10);
            // if sub-string not added returned false
            // means already added so add to repeats
            if (!seen.add(sub))
                repeats.add(sub);
        }
        return new ArrayList<>(repeats);
    }

    public static void main(String[] args) {
        List<String> dnaSequences = findRepeatedDnaSequences("AAAAACCCCCAAAAACCCCCCAAAAAGGGTTT");
        System.out.println(dnaSequences);
    }
}