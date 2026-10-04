/*
 * =====================================================================
 *  P064 Trie + DFS, and the Bit Trie   Canonical LC 212 | Hard
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 212, Word Search II)
 *   Return every word from `words` that can be traced in the letter grid by moving up,
 *   down, left or right, without reusing a cell within one word.
 *
 * EXAMPLE
 *   board [[o,a,a,n],[e,t,a,e],[i,h,k,r],[i,f,l,v]], words [oath,pea,eat,rain]
 *       ->  [eat, oath]
 *   board [[a,b],[c,d]], words [abcb]  ->  []
 *
 * RECOGNIZE WHEN
 *   - Search for MANY words at once in a grid, a stream or a text: build one trie and walk
 *     it together with the search, pruning the moment the path is not a prefix.
 *   - "maximum XOR of two numbers": a trie over BITS, greedily taking the opposite bit.
 *   Not this if: one word in a grid -> plain backtracking (P080_GridPathBacktracking); one
 *   pattern in a text -> P106_KmpPrefixFunction.
 *
 * TEMPLATE
 *   build a trie of all words (store the word at its end node)
 *   for each cell: dfs(cell, root)
 *   dfs(r, c, node):
 *       next = node.child[board[r][c]]; if null: return          // prune: not a prefix
 *       if next.word: record it; next.word = null                 // report once
 *       mark (r, c) used; dfs over 4 neighbours with next; unmark
 *   bit trie: insert each number from bit 31 down; query takes the opposite bit if present
 *
 * APPROACH
 *   1. Insert all words into a trie.
 *   2. DFS from every cell, moving in the trie in step with the grid.
 *   3. When the trie has no child for the next letter, stop that branch at once.
 *
 * KEY INSIGHT
 *   Running one search per word repeats the same prefixes over and over. Walking the trie
 *   alongside the grid explores each shared prefix once and stops as soon as no word can
 *   continue. The bit trie applies the same idea to binary strings: at each bit, greedily
 *   prefer the branch that sets the XOR bit.
 *
 * COMPLEXITY
 *   LC 212: O(cells * 4 * 3^(L-1)) worst case, far less with pruning. LC 421: O(32 n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 421  Max XOR of Two Numbers   bit trie of all numbers; for each, walk the
 *                                            opposite bits greedily
 *           LC 1707 Max XOR With an Element  sort queries by limit, insert numbers <= limit
 *           LC 745  Prefix and Suffix Search insert "suffix#word" for every suffix
 *           LC 1032 Stream of Characters     trie of reversed words, walked backwards
 *           LC 336  Palindrome Pairs         trie of reversed words + palindrome checks
 *
 * PITFALLS
 *   - Report each word once: clear the word at its node after finding it.
 *   - Restore the grid cell on the way back (backtracking), or later searches break.
 *   - Prune leaves of the trie as words are found to keep later DFS calls short.
 *
 * DEEP DIVE
 *   C05_WordSearch (14-Backtracking-Recursion), A01_Trie (10-Trie)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TrieDfsBitTrie {

    static class TrieNode {
        TrieNode[] child = new TrieNode[26];
        String word;                               // set at the node where a word ends
    }

    // Canonical LC 212.
    static List<String> findWords(char[][] board, String[] words) {
        TrieNode root = new TrieNode();
        for (String w : words) {
            TrieNode cur = root;
            for (char c : w.toCharArray()) {
                if (cur.child[c - 'a'] == null) {
                    cur.child[c - 'a'] = new TrieNode();
                }
                cur = cur.child[c - 'a'];
            }
            cur.word = w;
        }
        List<String> found = new ArrayList<>();
        for (int r = 0; r < board.length; r++) {
            for (int c = 0; c < board[0].length; c++) {
                dfs(board, r, c, root, found);
            }
        }
        Collections.sort(found);                   // any order is accepted; sorted to print
        return found;
    }

    private static void dfs(char[][] board, int r, int c, TrieNode node, List<String> found) {
        if (r < 0 || c < 0 || r >= board.length || c >= board[0].length || board[r][c] == '#') {
            return;
        }
        char ch = board[r][c];
        TrieNode next = node.child[ch - 'a'];
        if (next == null) {
            return;                                // no word continues with this letter
        }
        if (next.word != null) {
            found.add(next.word);
            next.word = null;                      // report once
        }
        board[r][c] = '#';                         // mark as used on this path
        dfs(board, r + 1, c, next, found);
        dfs(board, r - 1, c, next, found);
        dfs(board, r, c + 1, next, found);
        dfs(board, r, c - 1, next, found);
        board[r][c] = ch;                          // restore
    }

    static class BitNode {
        BitNode[] child = new BitNode[2];
    }

    // LC 421.
    static int findMaximumXOR(int[] nums) {
        BitNode root = new BitNode();
        for (int x : nums) {
            BitNode cur = root;
            for (int b = 30; b >= 0; b--) {
                int bit = (x >> b) & 1;
                if (cur.child[bit] == null) {
                    cur.child[bit] = new BitNode();
                }
                cur = cur.child[bit];
            }
        }
        int best = 0;
        for (int x : nums) {
            BitNode cur = root;
            int xor = 0;
            for (int b = 30; b >= 0; b--) {
                int want = ((x >> b) & 1) ^ 1;     // the opposite bit makes this XOR bit 1
                if (cur.child[want] != null) {
                    xor |= 1 << b;
                    cur = cur.child[want];
                } else {
                    cur = cur.child[want ^ 1];
                }
            }
            best = Math.max(best, xor);
        }
        return best;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        char[][] board = {
                {'o', 'a', 'a', 'n'}, {'e', 't', 'a', 'e'},
                {'i', 'h', 'k', 'r'}, {'i', 'f', 'l', 'v'}};
        check("LC 212 four words",
                findWords(board, new String[]{"oath", "pea", "eat", "rain"}), "[eat, oath]");
        check("LC 212 cell reuse not allowed",
                findWords(new char[][]{{'a', 'b'}, {'c', 'd'}}, new String[]{"abcb"}), "[]");
        check("LC 212 duplicates reported once",
                findWords(new char[][]{{'a', 'a'}}, new String[]{"a", "aa"}), "[a, aa]");

        check("LC 421 [3,10,5,25,2,8]", findMaximumXOR(new int[]{3, 10, 5, 25, 2, 8}), 28);
        check("LC 421 twelve numbers",
                findMaximumXOR(new int[]{14, 70, 53, 83, 49, 91, 36, 80, 92, 51, 66, 70}), 127);
        check("LC 421 single number", findMaximumXOR(new int[]{7}), 0);
    }
}
