/*
 * =====================================================================
 *  Count Distinct Substrings Using a Trie            Coding Ninjas | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given a lowercase string s, count how many DISTINCT substrings it contains.
 *   Following the usual statement for this problem, the empty substring counts too,
 *   so the answer for "abc" is 7: "", a, b, c, ab, bc, abc.
 *   s is a contiguous slice of the string - subsequences do not count.
 *
 * EXAMPLE
 *   "abab"     ->  8    "", a, b, ab, ba, aba, bab, abab
 *   "aaa"      ->  4    "", a, aa, aaa            (heavy duplication)
 *   "striver"  ->  28   28 slices exist, "r" appears twice, so 27 are distinct, +1
 *   ""         ->  1    only the empty substring
 *
 * APPROACH  (insert every suffix, count the nodes you allocate)
 *   1. Start an empty trie. Loop i over every start index of s.
 *   2. From the root, walk s[i..n-1] one character at a time, creating a child when
 *      the link is missing - this inserts the whole suffix starting at i.
 *   3. Every time you CREATE a node, increment the counter. Never count a reused node.
 *   4. Return counter + 1 to include the empty substring (the root itself).
 *   No end flags are needed anywhere: this is a counting argument about node
 *   allocation, not a membership query, so the Node here has links only.
 *
 * KEY INSIGHT
 *   Every substring of s is a PREFIX of some suffix of s. Insert all n suffixes and
 *   the trie's node set becomes exactly the set of distinct substrings - one node per
 *   distinct non-empty substring, with the root standing for the empty one. So you do
 *   not traverse to count; you count allocations during the build, and duplicates
 *   cost nothing because they reuse an existing node.
 *   Recognise the leap "substrings = prefixes of suffixes" whenever a problem asks
 *   about distinct substrings; a suffix trie (or suffix automaton for big inputs) is
 *   the standard answer.
 *
 * COMPLEXITY
 *   Time  O(n^2)  n suffixes, each up to n characters, one O(1) step per character
 *   Space O(n^2 * 26)  worst case (all characters distinct) every one of the ~n^2/2
 *         substrings gets its own node, each holding a 26-slot child array. This is
 *         why a suffix automaton or suffix array is used for large n.
 *
 * INTERVIEW FOLLOW-UPS
 *   - n up to 10^5: O(n^2) is dead. Use a suffix array with LCP - the answer is
 *     n(n+1)/2 minus the sum of the LCP array - or a suffix automaton in O(n).
 *   - Count distinct substrings of a fixed length k: count nodes at depth k.
 *   - Longest repeated substring: deepest trie node that has more than one suffix
 *     passing through it (keep a visit counter per node).
 *   - Why is the empty substring included here? It is the problem's convention -
 *     always confirm the off-by-one with the interviewer before coding.
 *
 * RUN
 *   main() runs 6 cases (typical, all-identical, all-distinct, single char, empty)
 *   through the trie method AND a brute-force HashSet method, printing both against
 *   the expected count so the counting argument is visibly verified.
 */
import java.util.HashSet;
import java.util.Set;

class Node {

    // links[c - 'a'] is the child reached by walking the letter c from this node.
    // No end flag: this problem counts nodes, it never asks "is X a stored word?".
    Node[] links = new Node[26];

    public boolean containsKey(char ch) {
        return links[ch - 'a'] != null;
    }

    public Node get(char ch) {
        return links[ch - 'a'];
    }

    public void put(char ch) {
        links[ch - 'a'] = new Node();
    }
}

class CountDistinctSubstringsUsingTrie {

    /**
     * Inserts every suffix of s into one trie and counts the nodes created.
     * Each created node is exactly one distinct non-empty substring.
     */
    public static int countDistinctSubstrings(String s) {
        Node root = new Node();
        int cnt = 0;
        int n = s.length();

        for (int i = 0; i < n; i++) {
            Node node = root;
            for (int j = i; j < n; j++) { // the suffix s[i..n-1], prefix by prefix
                char currentChar = s.charAt(j);
                if (!node.containsKey(currentChar)) {
                    node.put(currentChar);
                    cnt++; // a brand new node == a substring never seen before
                }
                node = node.get(currentChar);
            }
        }
        return cnt + 1; // + 1 for the empty substring, which the root represents
    }

    /**
     * Brute force cross-check: materialise every substring in a HashSet.
     * Same O(n^2) substrings but O(n^3) work because each substring is copied.
     */
    public static int countDistinctSubstringsBruteForce(String s) {
        Set<String> seen = new HashSet<>();
        seen.add(""); // same convention as the trie method
        for (int i = 0; i < s.length(); i++) {
            for (int j = i + 1; j <= s.length(); j++) {
                seen.add(s.substring(i, j));
            }
        }
        return seen.size();
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    private static void run(int caseNo, String s, int expected) {
        print("case " + caseNo + " trie  \"" + s + "\"", countDistinctSubstrings(s), expected);
        print("case " + caseNo + " brute \"" + s + "\"",
                countDistinctSubstringsBruteForce(s), expected);
    }

    public static void main(String[] args) {
        // typical: 28 slices, "r" repeats so 27 are distinct, +1 for the empty one
        run(1, "striver", 28);

        // tricky: duplicates everywhere - "", a, b, ab, ba, aba, bab, abab
        run(2, "abab", 8);

        // edge: maximum duplication - only "", a, aa, aaa survive
        run(3, "aaa", 4);

        // edge: all characters distinct, so nothing collapses: 3*4/2 = 6, +1
        run(4, "abc", 7);

        // edge: single character, then the empty string
        run(5, "a", 2);
        run(6, "", 1);
    }
}
