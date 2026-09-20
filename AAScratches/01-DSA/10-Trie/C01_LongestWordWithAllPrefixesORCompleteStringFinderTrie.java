/*
 * =====================================================================
 *  Longest Word With All Prefixes (Complete String)   LeetCode 1858 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a list of lowercase words, find the longest word whose EVERY prefix is
 *   also present in the list. "apple" qualifies only if a, ap, app and appl are all
 *   in the list too. On a length tie return the lexicographically smallest word,
 *   and if no word qualifies return the string "None".
 *
 * EXAMPLE
 *   ["n", "ni", "nin", "ninj", "ninja", "nz"]      ->  "ninja"  (every prefix present)
 *   ["ab", "bp", "bmnn", "bpo", "bpom", "b"]       ->  "bpom"   ("bmnn" fails at "bm")
 *   ["a", "ac", "ab"]                              ->  "ab"     (tie on length, ab < ac)
 *   ["ab", "bc"]                                   ->  "None"   ("a" and "b" missing)
 *
 * APPROACH  (trie walk that asserts the end flag at EVERY node)
 *   1. Insert all words into a 26-way trie, setting the end flag at each word's
 *      last node - exactly the standard insert.
 *   2. For each word, walk it again from the root. After stepping onto each node,
 *      demand node.isEnd(): that node is a prefix of the word, and the flag says
 *      that prefix was itself inserted as a word.
 *   3. The moment a node has no end flag (or the letter is missing) the word fails.
 *   4. Among the words that pass, keep the longest; break ties by compareTo < 0.
 *
 * KEY INSIGHT
 *   This is startsWith() with one extra assertion moved inside the loop. Plain
 *   search checks isEnd() once, at the final node; here you check it at every node,
 *   which is how you ask "is every prefix of this word also a word?" in one pass.
 *   Pattern to recognise: whenever the question is about ALL prefixes of a string
 *   rather than the string itself, the answer is a flag test inside the walk, not
 *   an extra data structure.
 *
 * COMPLEXITY
 *   Time  O(sum of word lengths)  one build pass plus one validation pass, each
 *         touching every character at most once
 *   Space O(sum of word lengths * 26)  worst case one trie node per character,
 *         each node holding a fixed 26-slot child array
 *
 * INTERVIEW FOLLOW-UPS
 *   - LeetCode 720 is the same problem with the same lexicographic tie-break.
 *   - Can you skip the second pass? Yes - DFS the trie once, only descending into
 *     children whose flag is set, and record the deepest node reached.
 *   - Why not a HashSet plus substring checks? It works in O(sum of L^2) because
 *     every prefix is materialised as a new String; the trie reuses the walk.
 *   - Return ALL complete strings, or the count of them, instead of just the best.
 *
 * RUN
 *   main() runs 5 cases: typical, a word that fails mid-way, a lexicographic tie,
 *   a no-answer case, and a single-word edge case. Actual vs expected per line.
 */
import java.util.List;

class Node {

    // links[c - 'a'] is the child reached by walking the letter c from this node.
    Node[] links = new Node[26];

    // true only if some inserted word ends exactly here.
    boolean flag = false;

    public boolean containsKey(char ch) {
        return links[ch - 'a'] != null;
    }

    public Node get(char ch) {
        return links[ch - 'a'];
    }

    public void put(char ch, Node node) {
        links[ch - 'a'] = node;
    }

    public void setEnd() {
        flag = true;
    }

    public boolean isEnd() {
        return flag;
    }
}

class Trie {

    private final Node root;

    public Trie() {
        root = new Node();
    }

    public void insert(String word) {
        Node node = root;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (!node.containsKey(ch)) {
                node.put(ch, new Node());
            }
            node = node.get(ch);
        }
        node.setEnd();
    }

    /**
     * True when every prefix of word (including word itself) was inserted.
     * The end-flag test sits INSIDE the loop - that is the whole trick.
     */
    public boolean checkIfAllPrefixExists(String word) {
        Node node = root;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (!node.containsKey(ch)) {
                return false; // that prefix was never even spelled out
            }
            node = node.get(ch);
            if (!node.isEnd()) {
                return false; // prefix path exists, but it is not a word on its own
            }
        }
        return true;
    }
}

class LongestWordWithAllPrefixesORCompleteStringFinderTrie {

    /**
     * n is the list size - kept because the judge's signature passes it; a.size()
     * is used instead so the method stays correct if the two ever disagree.
     */
    public static String completeString(int n, List<String> a) {
        Trie trie = new Trie();
        for (String word : a) {
            trie.insert(word);
        }

        String longest = "";
        for (String word : a) {
            if (!trie.checkIfAllPrefixExists(word)) {
                continue;
            }
            if (word.length() > longest.length()) {
                longest = word;
            } else if (word.length() == longest.length() && word.compareTo(longest) < 0) {
                longest = word; // same length, keep the lexicographically smaller
            }
        }
        return longest.isEmpty() ? "None" : longest;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    private static void run(String label, List<String> words, String expected) {
        print(label + " " + words, completeString(words.size(), words), expected);
    }

    public static void main(String[] args) {
        // typical: a clean chain of prefixes, "nz" is a decoy of length 2
        run("case 1", List.of("n", "ni", "nin", "ninj", "ninja", "nz"), "ninja");

        // tricky: "bmnn" is the longest word but dies at prefix "bm"
        run("case 2", List.of("ab", "bp", "bmnn", "bpo", "bpom", "b"), "bpom");

        // tricky: two valid words of length 2 - lexicographic tie-break picks "ab"
        run("case 3", List.of("a", "ac", "ab"), "ab");

        // edge: no single letter is present, so nothing qualifies
        run("case 4", List.of("ab", "bc"), "None");

        // edge: one word, which is its own only prefix
        run("case 5", List.of("a"), "a");
    }
}
