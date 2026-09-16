/**
 * Problem: Given a list of strings, find the longest string such that every prefix
 * of it is also present in the list. If multiple candidates have the same length,
 * return the lexicographically smallest one; if none exist, return "None".
 *
 * Approach: Build a Trie from all words. For each word, traverse the Trie while
 * verifying that each visited node marks the end of a word (i.e., every prefix
 * exists). Keep track of the best candidate by length and lexicographic order.
 *
 * Time Complexity: O(total characters in all words) for building the Trie,
 * plus another O(total characters) for validation, overall O(N·L).
 *
 * Space Complexity: O(total characters) to store the Trie nodes. 
 */
import java.util.List;

class Node {
    Node[] links = new Node[26];
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
    private Node root;

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

    public boolean checkIfAllPrefixExists(String word) {
        Node node = root;
        boolean flag = true;
        for (int i = 0; i < word.length() && flag; i++) {
            char ch = word.charAt(i);
            if (node.containsKey(ch)) {
                node = node.get(ch);
//                for every prefix it's a word by it's own
                flag = flag && node.isEnd();
            } else {
                return false;
            }
        }
        return flag;
    }
}

class LongestWordWithAllPrefixesORCompleteStringFinderTrie {
    public static String completeString(int n, List<String> a) {
        Trie obj = new Trie();
        for (String word : a) {
            obj.insert(word);
        }
        String longest = "";
        for (String word : a) {
            if (obj.checkIfAllPrefixExists(word)) {
                if (word.length() > longest.length()) {
                    longest = word;
                } else if (word.length() == longest.length() && word.compareTo(longest) < 0) {
                    longest = word;
                }
            }
        }
        return longest.isEmpty() ? "None" : longest;
    }

    public static void main(String[] args) {
        List<String> words = List.of("a", "ap", "app", "appl", "apple");
        int n = words.size();
        System.out.println(completeString(n, words));
    }
}

