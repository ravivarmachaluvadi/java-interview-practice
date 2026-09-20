/*
 * =====================================================================
 *  Alien Dictionary (order of letters)      LeetCode 269 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   You are given N words that are already sorted according to the rules of an
 *   alien language whose alphabet is the first K letters 'a'..'a'+K-1. Recover
 *   one valid ordering of those K letters, as a string. Return "" if the given
 *   word list contradicts itself (no ordering can explain it).
 *
 * EXAMPLE
 *   dict = ["baa","abcd","abca","cab","cad"], K = 4  ->  "bdac"
 *   dict = ["caa","aaa","aab"],               K = 3  ->  "cab"
 *   dict = ["abc","ab"],                      K = 3  ->  ""   prefix comes later
 *   dict = ["ab","ba","ac"],                  K = 3  ->  ""   a<b and b<a
 *
 * APPROACH  (derive edges from adjacent words, then Kahn's topological sort)
 *   1. Compare each adjacent pair of words (dict[i], dict[i+1]) only.
 *      Sorted order is transitive, so non-adjacent pairs add nothing new.
 *   2. Walk both words together to the first position where they differ. That
 *      single character pair is the only fact the pair proves: s1[p] < s2[p].
 *      Record it as a directed edge s1[p] -> s2[p].
 *   3. If they never differ inside the common prefix and s1 is the longer word
 *      ("abc" listed before "ab"), the input is impossible: return "".
 *   4. Topologically sort the K letters with Kahn's algorithm: push every letter
 *      whose indegree is 0, pop one, and decrement the indegree of its successors.
 *   5. If the sort emits fewer than K letters there is a cycle, so return "".
 *      Otherwise map each index back to a character and join them.
 *
 * KEY INSIGHT
 *   The sort is the easy half; the interview is testing whether you can BUILD the
 *   graph. Two facts do it: only adjacent words are informative, and a word pair
 *   proves exactly one ordering - the first mismatching character - after which
 *   you must break. Keep comparing past the mismatch and you invent edges the
 *   input never justified. Recognise this shape whenever a problem hands you
 *   ordered data and asks for the underlying order.
 *
 * COMPLEXITY
 *   Time  O(N * L + K + E)   L = word length; each pair scanned once, then Kahn's.
 *   Space O(K + E)           adjacency lists, indegree array, queue. E <= N-1.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return every valid ordering instead of one (backtracking over the queue).
 *   - Detect which pair of words caused the contradiction, for an error message.
 *   - Same question with a full unicode alphabet: swap arrays for hash maps.
 *   - How would you answer "is X before Y" for many queries after one sort?
 *
 * FIXED
 *   Original code never rejected invalid input: a prefix appearing after the
 *   longer word, or a cycle among the letters, both returned a bogus string.
 *
 * RUN
 *   main() runs 4 cases: typical, second valid dict, prefix violation, cycle.
 */

import java.util.*;

class Solution {

    /** Kahn's algorithm. Returns fewer than V entries when the graph has a cycle. */
    private List<Integer> topoSort(int V, List<List<Integer>> adj) {
        int[] indegree = new int[V];
        for (int u = 0; u < V; u++) {
            for (int v : adj.get(u)) {
                indegree[v]++;
            }
        }

        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < V; i++) {
            if (indegree[i] == 0) {
                q.add(i);
            }
        }

        List<Integer> topo = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.remove();
            topo.add(u);
            // u is placed, so it no longer constrains its successors
            for (int v : adj.get(u)) {
                indegree[v]--;
                if (indegree[v] == 0) q.add(v);
            }
        }
        return topo;
    }

    /**
     * @param dict words already sorted in the alien order
     * @param N    number of words
     * @param K    alphabet size; letters are 'a' .. 'a'+K-1
     * @return a valid letter ordering, or "" when the input is self-contradictory
     */
    public String findOrder(String[] dict, int N, int K) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < K; i++) {
            adj.add(new ArrayList<>());
        }

        for (int i = 0; i < N - 1; i++) {
            String s1 = dict[i];
            String s2 = dict[i + 1];
            int len = Math.min(s1.length(), s2.length());
            boolean foundDifference = false;

            for (int ptr = 0; ptr < len; ptr++) {
                if (s1.charAt(ptr) != s2.charAt(ptr)) {
                    // first mismatch is the ONLY ordering this pair proves
                    adj.get(s1.charAt(ptr) - 'a').add(s2.charAt(ptr) - 'a');
                    foundDifference = true;
                    break;
                }
            }

            /* s2 is a prefix of s1 yet listed after it - no alphabet can
               make "abc" sort before "ab", so the input is impossible. */
            if (!foundDifference && s1.length() > s2.length()) {
                return "";
            }
        }

        List<Integer> topo = topoSort(K, adj);
        // fewer than K letters emitted means the constraints form a cycle
        if (topo.size() < K) {
            return "";
        }

        StringBuilder ans = new StringBuilder();
        for (int u : topo) {
            ans.append((char) ('a' + u));
        }
        return ans.toString();
    }
}

class AlienDictionaryOrder {

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        Solution obj = new Solution();

        String[] d1 = {"baa", "abcd", "abca", "cab", "cad"};
        print("case 1 typical      ", quote(obj.findOrder(d1, d1.length, 4)), quote("bdac"));

        String[] d2 = {"caa", "aaa", "aab"};
        print("case 2 small dict   ", quote(obj.findOrder(d2, d2.length, 3)), quote("cab"));

        // edge: a longer word listed before its own prefix can never be sorted
        String[] d3 = {"abc", "ab"};
        print("case 3 bad prefix   ", quote(obj.findOrder(d3, d3.length, 3)), quote(""));

        // tricky: a < b from the first pair, b < a from the second -> cycle
        String[] d4 = {"ab", "ba", "ac"};
        print("case 4 cyclic rules ", quote(obj.findOrder(d4, d4.length, 3)), quote(""));
    }

    /** Makes the empty-string answers visible in the output. */
    private static String quote(String s) {
        return "\"" + s + "\"";
    }
}
