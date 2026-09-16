class Node {
    // Initialize the links array with size 26
    Node[] links = new Node[26];
    boolean flag;

    public boolean containsKey(char ch) {
        return links[ch - 'a'] != null;
    }

    public Node get(char ch) {
        return links[ch - 'a'];
    }

    public void put(char ch) {
        links[ch - 'a'] = new Node();
    }

    public void setEnd() {
        flag = true;
    }

    public boolean isEnd() {
        return flag;
    }
}

class CountDistinctSubstringsUsingTrie {
    public static int countDistinctSubstrings(String s) {
        Node root = new Node();
        int cnt = 0; // Count of distinct substrings
        int n = s.length();

        for (int i = 0; i < n; i++) {
            Node node = root;
            for (int j = i; j < n; j++) {
                char currentChar = s.charAt(j);
                if (!node.containsKey(currentChar)) {
                    node.put(currentChar);
                    cnt++; // Increment count when a new character is added
                }
                node = node.get(currentChar);
            }
        }
        //28
        return cnt + 1; // Include the empty substring
    }

    public static void main(String[] args) {
        String s = "striver";
        System.out.println("Current String: " + s);
        System.out.println("Number of distinct substrings: " + countDistinctSubstrings(s));
    }
}
