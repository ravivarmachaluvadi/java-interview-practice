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
