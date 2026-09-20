/*
 * =====================================================================
 *  Design Add and Search Words Data Structure   LeetCode 211 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Design a dictionary with two operations: addWord(word) stores a lowercase word,
 *   and search(pattern) reports whether any stored word matches the pattern.
 *   The pattern may contain '.', which matches exactly one letter (never zero, never
 *   many), so the pattern always has the same length as the word it matches.
 *
 * EXAMPLE
 *   add "bad", "dad", "mad" search("pad")   ->  false  no word starts with p
 *   search("bad")   ->  true   exact hit
 *   search(".ad")   ->  true   '.' matches b, d or m
 *   search("b..")   ->  true   b then any two letters
 *   search("....")  ->  false  length 4, every stored word has length 3
 *
 * APPROACH  (26-way trie + DFS with backtracking on the wildcard)
 *   1. addWord is the plain trie insert: walk the letters, create missing children,
 *      set isEnd on the final node.
 *   2. search recurses with (pattern, position, currentNode).
 *   3. Base cases: a null node is a dead branch -> false; reaching the end of the
 *      pattern -> return node.isEnd, because the path must spell a whole word.
 *   4. A normal letter has one option: descend into that single child.
 *   5. A '.' fans out: try every non-null child in turn and return true on the first
 *      success. Returning false simply unwinds the recursion - that is the backtrack.
 *
 * KEY INSIGHT
 *   Exact trie search is a loop because each character leaves you exactly one choice.
 *   The moment a character can mean 26 things, the walk becomes a tree of choices, so
 *   the loop becomes recursion (a DFS) with the position in the pattern as the depth.
 *   Pattern to recognise: wildcard / pattern-matching over a word set = trie + DFS,
 *   the same skeleton that solves Word Search II and regex-style dictionary matching.
 *
 * COMPLEXITY
 *   Time  addWord O(L). search O(L) when the pattern has no dots; worst case
 *         O(26^d * L) where d is the number of dots, since each dot branches 26 ways
 *         (in practice only existing children are visited, so it is far smaller).
 *   Space O(N * L * 26) for the trie, plus O(L) recursion depth - one frame per
 *         pattern character.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Support '*' (zero or more characters): the recursion must also try staying on
 *     the same node, which breaks the pattern-length == word-length shortcut.
 *   - Bucket words by length first so "...." fails instantly instead of descending.
 *   - Leading dots are the expensive case - how would you index suffixes to help?
 *   - How does this compare to running a regex over a list of words? (O(N*L) per
 *     query, no shared prefixes.)
 *
 * RUN
 *   main() runs 9 cases: exact hits and misses, dots in first / middle / last
 *   position, an all-dots pattern, a wrong-length pattern, and a single-letter edge.
 */
class DesignAddAndSearchWordsDataStructure {

    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd = false;
    }

    private final TrieNode root;

    public DesignAddAndSearchWordsDataStructure() {
        root = new TrieNode();
    }

    /** Standard trie insert: one node per character, end flag on the last node. */
    public void addWord(String word) {
        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            int idx = ch - 'a';
            if (node.children[idx] == null) {
                node.children[idx] = new TrieNode();
            }
            node = node.children[idx];
        }
        node.isEnd = true;
    }

    /** True if any stored word matches the pattern, where '.' matches one letter. */
    public boolean search(String pattern) {
        return searchInNode(pattern, 0, root);
    }

    private boolean searchInNode(String pattern, int pos, TrieNode node) {
        if (node == null) {
            return false; // the previous letter had no such child
        }
        if (pos == pattern.length()) {
            // path consumed: it only counts if a whole word ends right here
            return node.isEnd;
        }

        char ch = pattern.charAt(pos);
        if (ch == '.') {
            // wildcard: branch into every live child, unwind (backtrack) on failure
            for (TrieNode child : node.children) {
                if (child != null && searchInNode(pattern, pos + 1, child)) {
                    return true;
                }
            }
            return false;
        }
        // ordinary letter: exactly one way forward
        return searchInNode(pattern, pos + 1, node.children[ch - 'a']);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        DesignAddAndSearchWordsDataStructure dict = new DesignAddAndSearchWordsDataStructure();
        System.out.println("adding: bad, dad, mad");
        dict.addWord("bad");
        dict.addWord("dad");
        dict.addWord("mad");

        // typical: exact match, hit and miss
        print("case 1 search(bad)", dict.search("bad"), true);
        print("case 2 search(pad)", dict.search("pad"), false);

        // wildcard in each position
        print("case 3 search(.ad)", dict.search(".ad"), true);
        print("case 4 search(b..)", dict.search("b.."), true);
        print("case 5 search(ba.)", dict.search("ba."), true);

        // tricky: all dots matches any stored word of that exact length
        print("case 6 search(...)", dict.search("..."), true);
        print("case 7 search(....)", dict.search("...."), false);

        // tricky: 'p' has no child at the root, so the dots never get a chance
        print("case 8 search(p..)", dict.search("p.."), false);

        // edge: single-letter word matched by a bare dot
        DesignAddAndSearchWordsDataStructure tiny = new DesignAddAndSearchWordsDataStructure();
        tiny.addWord("a");
        print("case 9 add(a) then search(.)", tiny.search("."), true);
    }
}
