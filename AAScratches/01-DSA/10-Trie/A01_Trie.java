/*
 * =====================================================================
 *  Implement Trie (Prefix Tree)              LeetCode 208 | Medium     MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Build a data structure that stores lowercase words and answers three questions
 *   fast: insert(word), search(word) -> is this exact word stored, and
 *   startsWith(prefix) -> is any stored word starting with this prefix.
 *   Alphabet is the 26 lowercase English letters only.
 *
 * EXAMPLE
 *   insert("strike"); search("strike")     ->  true
 *   insert("strike"); search("strawberry") ->  false  (letters 'a' branch missing)
 *   insert("strike"); startsWith("stri")   ->  true   (prefix path exists)
 *   insert("strike"); search("stri")       ->  false  (path exists, end flag not set)
 *
 * APPROACH  (26-way trie: one node per prefix, one child slot per letter)
 *   1. A TrieNode holds links[26] (child per letter) and a boolean end flag.
 *      The node itself stores no character; the EDGE you walked names the letter.
 *   2. insert: walk from root, creating a child whenever the slot is null,
 *      then set the end flag on the node you land on after the last letter.
 *   3. search: walk the same way, but return false the moment a slot is null;
 *      on arrival return the node's end flag.
 *   4. startsWith: identical walk, but arriving at all is the answer - no flag check.
 *
 * KEY INSIGHT
 *   search and startsWith are the SAME traversal; the only difference is the last
 *   line (isEnd() vs true). That end flag is what separates "this prefix exists"
 *   from "this whole word was inserted" - without it a trie cannot tell "app" that
 *   was inserted apart from "app" that is merely a prefix of "apple".
 *   Recognise a trie whenever a problem repeats prefix questions over a word set:
 *   a HashSet answers exact lookups but cannot answer prefix questions at all.
 *
 * COMPLEXITY
 *   Time  O(L) per operation  one step per character, no branching or rescanning
 *   Space O(N * L * 26) worst case  every character of every word can add a node,
 *         and each node carries a fixed 26-slot array (swap for a HashMap to shrink)
 *
 * INTERVIEW FOLLOW-UPS
 *   - How do you delete a word? Unset the flag, then prune nodes with no children.
 *   - Unicode or mixed case input: replace links[26] with a Map<Character, TrieNode>.
 *   - Count how many stored words share a prefix: keep a passCount per node.
 *   - Why not a HashSet of all prefixes? Same asymptotics, far more memory, and no
 *     way to enumerate completions in sorted order.
 *
 * RUN
 *   main() inserts four words sharing the "stri" prefix and runs 6 cases
 *   (hit, miss, prefix-only word, empty string, prefix of nothing) actual vs expected.
 */
class TrieNode {

    // links[c - 'a'] is the child reached by walking the letter c from this node.
    TrieNode[] links = new TrieNode[26];

    // true only if some inserted word ENDS exactly here.
    boolean flag = false;

    void put(char ch) {
        links[ch - 'a'] = new TrieNode();
    }

    boolean containsChar(char ch) {
        return links[ch - 'a'] != null;
    }

    TrieNode getNodeAtChar(char ch) {
        return links[ch - 'a'];
    }

    void setEnd() {
        flag = true;
    }

    boolean isEnd() {
        return flag;
    }
}

class Trie {

    private final TrieNode root = new TrieNode();

    public Trie() {
    }

    public void insert(String word) {
        TrieNode trieNode = root;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (!trieNode.containsChar(ch)) {
                trieNode.put(ch); // first word to use this letter here
            }
            trieNode = trieNode.getNodeAtChar(ch);
        }
        trieNode.setEnd(); // mark the whole word, not just the path
    }

    public boolean search(String word) {
        TrieNode end = walk(word);
        return end != null && end.isEnd();
    }

    public boolean startsWith(String prefix) {
        // reaching the node at all proves at least one word goes through it
        return walk(prefix) != null;
    }

    /**
     * Follows s one character at a time from the root.
     * Returns the node reached, or null if the path breaks anywhere.
     */
    private TrieNode walk(String s) {
        TrieNode trieNode = root;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (!trieNode.containsChar(ch)) {
                return null;
            }
            trieNode = trieNode.getNodeAtChar(ch);
        }
        return trieNode;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        System.out.println("inserting: striver, striving, string, strike");
        trie.insert("striver");
        trie.insert("striving");
        trie.insert("string");
        trie.insert("strike");

        // typical: one hit, one miss
        print("case 1 search(strike)", trie.search("strike"), true);
        print("case 2 search(strawberry)", trie.search("strawberry"), false);

        // tricky: "stri" is a live prefix but was never inserted as a word
        print("case 3 startsWith(stri)", trie.startsWith("stri"), true);
        print("case 4 search(stri)", trie.search("stri"), false);

        // edge: empty string is a prefix of everything, but is not a stored word
        print("case 5 startsWith(\"\")", trie.startsWith(""), true);
        print("case 6 search(\"\")", trie.search(""), false);
    }
}
