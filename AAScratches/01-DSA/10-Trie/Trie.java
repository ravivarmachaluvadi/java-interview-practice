/**
 * Implements a Trie (prefix tree) for storing lowercase English words.
 *
 * Problem solved:
 *   - Insert words into the trie.
 *   - Search for exact word existence.
 *   - Check if any stored word starts with a given prefix.
 *
 * Approach:
 *   Each node holds up to 26 child references (one per letter). Insertion
 *   walks/creates nodes along the word, marking the final node as an end.
 *   Search and prefix checks traverse nodes; search additionally verifies
 *   the end flag. All operations run in linear time relative to the word length.
 *
 * Time Complexity:
 *   - insert(word): O(L) where L is word length.
 *   - search(word): O(L).
 *   - startsWith(prefix): O(P) where P is prefix length.
 *
 * Space Complexity:
 *   O(N * L) in worst case, where N is number of inserted words and
 *   each character may create a new node. Each node uses constant space for
 *   its 26-element array and flag.
 */
class TrieNode {
    TrieNode[] links = new TrieNode[26];
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

    private TrieNode root = new TrieNode();

    public Trie() {
    }

    public void insert(String word) {
        TrieNode trieNode = root;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (!trieNode.containsChar(ch)) {
                trieNode.put(ch);
            }
            trieNode = trieNode.getNodeAtChar(ch);
        }
        trieNode.setEnd();
    }

    public boolean search(String word) {
        TrieNode trieNode = root;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (!trieNode.containsChar(ch)) {
                return false;
            }
            trieNode = trieNode.getNodeAtChar(ch);
        }
        return trieNode.isEnd();
    }

    public boolean startsWith(String prefix) {
        TrieNode trieNode = root;
        for (int i = 0; i < prefix.length(); i++) {
            char ch = prefix.charAt(i);
            if (!trieNode.containsChar(ch)) {
                return false;
            }
            trieNode = trieNode.getNodeAtChar(ch);
        }
        return true;
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        System.out.println("Inserting words: Striver, Striving, String, Strike");
        trie.insert("striver");
        trie.insert("striving");
        trie.insert("string");
        trie.insert("strike");

        System.out.println("Search if Strawberry exists in trie: " +
                (trie.search("strawberry") ? "True" : "False"));

        System.out.println("Search if Strike exists in trie: " +
                (trie.search("strike") ? "True" : "False"));

        System.out.println("If words in Trie start with Stri: " +
                (trie.startsWith("stri") ? "True" : "False"));
    }
}
