/*
 * =====================================================================
 *  P063 Trie (Prefix Tree)   Canonical LC 208 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 208, Implement Trie (Prefix Tree))
 *   Implement insert(word), search(word) (exact word present?) and startsWith(prefix)
 *   (any word with this prefix?) over lowercase words.
 *
 * EXAMPLE
 *   insert("apple"); search("apple") -> true; search("app") -> false;
 *   startsWith("app") -> true; insert("app"); search("app") -> true
 *
 * RECOGNIZE WHEN
 *   - Many words, many PREFIX questions: autocomplete, "starts with", replace a word by its
 *     shortest root, longest word built letter by letter.
 *   - Wildcard search over a dictionary ('.' matches any letter).
 *   - Searching a grid or stream for MANY words at once (-> P064_TrieDfsBitTrie).
 *   Not this if: only whole-word lookups -> a HashSet is simpler; one pattern in one text
 *   -> P106_KmpPrefixFunction.
 *
 * TEMPLATE
 *   node = {children[26], isWord}
 *   insert(w): cur = root; for c in w: cur = cur.child[c] (create if missing); cur.isWord = true
 *   walk(s):   cur = root; for c in s: cur = cur.child[c]; if null: return null; return cur
 *   search(w) = walk(w) != null and walk(w).isWord;  startsWith(p) = walk(p) != null
 *   wildcard:  DFS; on '.', try every non-null child
 *
 * APPROACH
 *   1. Each node is one prefix; its 26 children extend it by one letter.
 *   2. insert creates the missing nodes and marks the last one as a word end.
 *   3. search / startsWith walk the same path; only search needs the end mark.
 *
 * KEY INSIGHT
 *   Words that share a prefix share the path for it, so a prefix question costs O(length of
 *   the prefix), no matter how many words are stored. The "isWord" flag separates "a word
 *   ends here" from "some word passes through here".
 *
 * COMPLEXITY
 *   insert / search / startsWith O(L) time; space O(total characters * alphabet).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 211  Add and Search Words     DFS: a '.' branches into all 26 children
 *   [coded] LC 1268 Search Suggestions       insert sorted products; each node keeps up to
 *                                            3 suggestions passing through it
 *   [coded] LC 720  Longest Word in Dict     a word counts only if every prefix is a word:
 *                                            DFS only through isWord nodes
 *           LC 648  Replace Words            walk each word; stop at the first isWord node
 *           LC 677  Map Sum Pairs            each node stores the sum of values below it
 *           LC 14   Longest Common Prefix    walk while exactly one child and not a word end
 *           LC 1032 Stream of Characters     trie of REVERSED words; check suffixes
 *
 * PITFALLS
 *   - Forgetting isWord: "app" would be "found" just because "apple" exists.
 *   - children as a HashMap when the alphabet is large; array[26] for lowercase.
 *   - LC 1268: insert products in sorted order so the first 3 seen are the answer.
 *
 * DEEP DIVE
 *   A01_Trie, C02_DesignAddAndSearchWordsDataStructure, C03_SearchSuggestionsSystem,
 *   C01_LongestWordWithAllPrefixesORCompleteStringFinderTrie (10-Trie)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class TrieBasics {

    static class TrieNode {
        TrieNode[] child = new TrieNode[26];
        boolean isWord;
        List<String> suggestions = new ArrayList<>();   // used by LC 1268 only
    }

    // Canonical LC 208.
    static class Trie {
        private final TrieNode root = new TrieNode();

        void insert(String word) {
            TrieNode cur = root;
            for (char c : word.toCharArray()) {
                if (cur.child[c - 'a'] == null) {
                    cur.child[c - 'a'] = new TrieNode();
                }
                cur = cur.child[c - 'a'];
            }
            cur.isWord = true;
        }

        boolean search(String word) {
            TrieNode n = walk(word);
            return n != null && n.isWord;
        }

        boolean startsWith(String prefix) {
            return walk(prefix) != null;
        }

        private TrieNode walk(String s) {
            TrieNode cur = root;
            for (char c : s.toCharArray()) {
                cur = cur.child[c - 'a'];
                if (cur == null) {
                    return null;
                }
            }
            return cur;
        }
    }

    // LC 211: '.' matches any letter.
    static class WordDictionary {
        private final TrieNode root = new TrieNode();

        void addWord(String word) {
            TrieNode cur = root;
            for (char c : word.toCharArray()) {
                if (cur.child[c - 'a'] == null) {
                    cur.child[c - 'a'] = new TrieNode();
                }
                cur = cur.child[c - 'a'];
            }
            cur.isWord = true;
        }

        boolean search(String word) {
            return match(word, 0, root);
        }

        private boolean match(String w, int i, TrieNode node) {
            if (i == w.length()) {
                return node.isWord;
            }
            char c = w.charAt(i);
            if (c != '.') {
                TrieNode next = node.child[c - 'a'];
                return next != null && match(w, i + 1, next);
            }
            for (TrieNode next : node.child) {
                if (next != null && match(w, i + 1, next)) {
                    return true;
                }
            }
            return false;
        }
    }

    // LC 1268: after each typed character, up to 3 lexicographically smallest matches.
    static List<List<String>> suggestedProducts(String[] products, String searchWord) {
        String[] sorted = products.clone();
        Arrays.sort(sorted);
        TrieNode root = new TrieNode();
        for (String p : sorted) {
            TrieNode cur = root;
            for (char c : p.toCharArray()) {
                if (cur.child[c - 'a'] == null) {
                    cur.child[c - 'a'] = new TrieNode();
                }
                cur = cur.child[c - 'a'];
                if (cur.suggestions.size() < 3) {
                    cur.suggestions.add(p);        // sorted insertion: first 3 are smallest
                }
            }
        }
        List<List<String>> out = new ArrayList<>();
        TrieNode cur = root;
        for (char c : searchWord.toCharArray()) {
            cur = cur == null ? null : cur.child[c - 'a'];
            out.add(cur == null ? new ArrayList<>() : cur.suggestions);
        }
        return out;
    }

    // LC 720: longest word whose every prefix is also a word (ties: smallest).
    static String longestWord(String[] words) {
        TrieNode root = new TrieNode();
        for (String w : words) {
            TrieNode cur = root;
            for (char c : w.toCharArray()) {
                if (cur.child[c - 'a'] == null) {
                    cur.child[c - 'a'] = new TrieNode();
                }
                cur = cur.child[c - 'a'];
            }
            cur.isWord = true;
        }
        return deepest(root, new StringBuilder(), "");
    }

    // DFS through word-end nodes only; children in a..z order keep the smallest on ties.
    private static String deepest(TrieNode node, StringBuilder path, String best) {
        if (path.length() > best.length()) {
            best = path.toString();
        }
        for (int i = 0; i < 26; i++) {
            TrieNode next = node.child[i];
            if (next != null && next.isWord) {
                path.append((char) ('a' + i));
                best = deepest(next, path, best);
                path.deleteCharAt(path.length() - 1);
            }
        }
        return best;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("apple");
        check("LC 208 search apple", trie.search("apple"), true);
        check("LC 208 search app (prefix only)", trie.search("app"), false);
        check("LC 208 startsWith app", trie.startsWith("app"), true);
        trie.insert("app");
        check("LC 208 search app after insert", trie.search("app"), true);
        check("LC 208 startsWith b", trie.startsWith("b"), false);

        WordDictionary dict = new WordDictionary();
        dict.addWord("bad");
        dict.addWord("dad");
        dict.addWord("mad");
        check("LC 211 pad", dict.search("pad"), false);
        check("LC 211 bad", dict.search("bad"), true);
        check("LC 211 .ad", dict.search(".ad"), true);
        check("LC 211 b..", dict.search("b.."), true);
        check("LC 211 .. too short", dict.search(".."), false);

        String[] products = {"mobile", "mouse", "moneypot", "monitor", "mousepad"};
        check("LC 1268 mouse", suggestedProducts(products, "mouse"),
                "[[mobile, moneypot, monitor], [mobile, moneypot, monitor], [mouse, mousepad], "
                        + "[mouse, mousepad], [mouse, mousepad]]");
        check("LC 1268 no match after t", suggestedProducts(new String[]{"havana"}, "tatiana"),
                "[[], [], [], [], [], [], []]");

        check("LC 720 world",
                longestWord(new String[]{"w", "wo", "wor", "worl", "world"}), "world");
        check("LC 720 apple beats apply",
                longestWord(new String[]{"a", "banana", "app", "appl", "ap", "apply", "apple"}),
                "apple");
    }
}
