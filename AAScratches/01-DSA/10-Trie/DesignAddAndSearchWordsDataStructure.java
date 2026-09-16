import java.util.*;

// 211. Design Add and Search Words Data Structure
// https://leetcode.com/problems/design-add-and-search-words-data-structure/description/
class DesignAddAndSearchWordsDataStructure {
    private static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEnd = false;
    }

    private final TrieNode root;

    /**
     * Initialize your data structure here.
     */
    public DesignAddAndSearchWordsDataStructure() {
        root = new TrieNode();
    }

    /**
     * Adds a word into the data structure.
     */
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

    /**
     * Returns if the word is in the data structure.
     * A word could contain the dot character '.'
     * to represent any one letter.
     */
    public boolean search(String word) {
        return searchInNode(word, 0, root);
    }

    private boolean searchInNode(String word, int pos, TrieNode node) {
        if (node == null) return false;
        if (pos == word.length()) {
            return node.isEnd;
        }
        char ch = word.charAt(pos);
        if (ch == '.') {
            // wildcard: try all possible children
            for (TrieNode child : node.children) {
                if (child != null && searchInNode(word, pos + 1, child)) {
                    return true;
                }
            }
            return false;
        } else {
            int idx = ch - 'a';
            return searchInNode(word, pos + 1, node.children[idx]);
        }
    }

    // main method for demonstration
    public static void main(String[] args) {
        DesignAddAndSearchWordsDataStructure wordDictionary = new DesignAddAndSearchWordsDataStructure();
        wordDictionary.addWord("bad");
        wordDictionary.addWord("dad");
        wordDictionary.addWord("mad");

        System.out.println(wordDictionary.search("pad")); // false
        System.out.println(wordDictionary.search("bad")); // true
        System.out.println(wordDictionary.search(".ad")); // true
        System.out.println(wordDictionary.search("b..")); // true
    }
}
