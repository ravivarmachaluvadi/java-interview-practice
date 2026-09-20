/*
 * =====================================================================
 *  Huffman Coding                              Classic greedy (GfG) | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a set of characters and their frequencies, build a prefix-free binary code
 *   (no code is a prefix of another) that minimises the total encoded length
 *   sum(freq[c] * len(code[c])). Then encode a string with it and decode it back.
 *
 * EXAMPLE
 *   chars = a b c d e f, freq = 5 9 12 13 16 45
 *     codes: f=0 c=100 d=101 e=111 a=1100 b=1101, total = 224 bits
 *     (fixed 3-bit codes would cost 100 * 3 = 300 bits)
 *   chars = a, freq = 3                    edge: one symbol, code "1", "aaa" -> "111"
 *   chars = a b c d, freq = 1 1 1 1        tricky: all tied, every code is 2 bits, total 8
 *
 * APPROACH  (merge-two-smallest greedy tree build)
 *   1. Put every character in a min-heap of Nodes ordered by frequency.
 *   2. While more than one node remains: poll the two smallest, create a parent whose
 *      frequency is their sum (left = smaller, right = larger), push the parent back.
 *   3. The last node is the root. Walk the tree: going left appends '0', right appends '1';
 *      the path to each leaf is that character's code. A lone root leaf gets code "1".
 *   4. Encode: concatenate each character's code. Decode: walk from the root one bit at a
 *      time, emit the character at each leaf and restart at the root.
 *
 *   Fixed: decoding crashed (NullPointerException) when the alphabet has a single symbol,
 *   because the root itself is the leaf and has no children to walk into. Also removed a
 *   duplicate copy of the tree-building loop; encode and decode now share buildTree().
 *
 * KEY INSIGHT
 *   The two rarest symbols should be the deepest leaves, and merging them makes a pseudo-
 *   symbol that is treated exactly like any other -- so the greedy is just "poll two,
 *   push one" on a heap, the Last Stone Weight loop with tree nodes instead of ints.
 *   The same shape solves Minimum Cost to Connect Ropes / Merge Files (LC 1167).
 *
 * COMPLEXITY
 *   Time  O(m log m)   m symbols, m - 1 merges, each two polls and one offer
 *   Space O(m)         the heap and the tree (2m - 1 nodes); codes are O(m * depth)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is it optimal? Exchange argument: swapping a rare symbol deeper never hurts.
 *   - Prefix-free property: every symbol is a leaf, so no code is a prefix of another.
 *   - Ties give different trees but the same total cost; decoder must use the SAME tree.
 *   - Connect Ropes / Merge Stones with a heap: identical loop, just sum the costs.
 *
 * RUN
 *   main() runs 3 cases (classic six symbols, single symbol, all-tied frequencies)
 *   and prints codes, total bits and the encode/decode round trip vs expected.
 */
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

// Node of the Huffman tree: a leaf holds a character; an internal node holds the merged frequency.
class Node {
    char ch;
    int freq;
    Node left, right;

    Node(char ch, int freq) {
        this(ch, freq, null, null);
    }

    Node(char ch, int freq, Node left, Node right) {
        this.ch = ch;
        this.freq = freq;
        this.left = left;
        this.right = right;
    }

    boolean isLeaf() {
        return left == null && right == null;
    }
}

class HuffmanCoding {

    // Greedy build: repeatedly merge the two least frequent nodes until one root remains.
    public static Node buildTree(char[] characters, int[] frequencies) {
        if (characters.length == 0) return null;
        PriorityQueue<Node> minHeap =
                new PriorityQueue<>((l, r) -> Integer.compare(l.freq, r.freq));
        for (int i = 0; i < characters.length; i++) {
            minHeap.add(new Node(characters[i], frequencies[i]));
        }
        while (minHeap.size() > 1) {
            Node left = minHeap.poll();          // smallest
            Node right = minHeap.poll();         // second smallest
            minHeap.add(new Node('\0', left.freq + right.freq, left, right));  // pseudo-symbol
        }
        return minHeap.peek();
    }

    // Root-to-leaf paths become the codes: '0' for left, '1' for right.
    private static void collectCodes(Node node, String path, Map<Character, String> codeOf) {
        if (node == null) return;
        if (node.isLeaf()) {
            codeOf.put(node.ch, path.isEmpty() ? "1" : path);   // lone root leaf still needs 1 bit
            return;
        }
        collectCodes(node.left, path + '0', codeOf);
        collectCodes(node.right, path + '1', codeOf);
    }

    public static Map<Character, String> getEncodedHuffmanMap(Node root) {
        Map<Character, String> codeOf = new HashMap<>();
        collectCodes(root, "", codeOf);
        return codeOf;
    }

    public static String encode(String text, Map<Character, String> codeOf) {
        StringBuilder bits = new StringBuilder();
        for (char ch : text.toCharArray()) {
            bits.append(codeOf.get(ch));
        }
        return bits.toString();
    }

    // Walk the tree bit by bit; every time a leaf is reached emit its character and restart.
    public static String decode(Node root, String bits) {
        StringBuilder text = new StringBuilder();
        if (root == null) return "";
        if (root.isLeaf()) {                      // single-symbol alphabet: each bit is one symbol
            for (int i = 0; i < bits.length(); i++) text.append(root.ch);
            return text.toString();
        }
        Node current = root;
        for (char bit : bits.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;
            if (current.isLeaf()) {
                text.append(current.ch);
                current = root;
            }
        }
        return text.toString();
    }

    // sum(freq * codeLength): the quantity Huffman minimises.
    private static int totalBits(char[] characters, int[] frequencies,
                                 Map<Character, String> codeOf) {
        int total = 0;
        for (int i = 0; i < characters.length; i++) {
            total += frequencies[i] * codeOf.get(characters[i]).length();
        }
        return total;
    }

    private static void runCase(String label, char[] chars, int[] freqs, String text,
                                int expectedTotalBits, String expectedCodes) {
        Node root = buildTree(chars, freqs);
        Map<Character, String> codeOf = getEncodedHuffmanMap(root);
        String encoded = encode(text, codeOf);
        String decoded = decode(root, encoded);

        System.out.println(label);
        if (expectedCodes != null) {
            System.out.println("  codes      : " + new TreeMap<>(codeOf)
                    + "   expected " + expectedCodes);
        }
        System.out.println("  total bits : " + totalBits(chars, freqs, codeOf)
                + "   expected " + expectedTotalBits);
        System.out.println("  encoded    : " + encoded + " (" + encoded.length() + " bits)");
        System.out.println("  round trip : " + decoded + "   expected " + text);
    }

    public static void main(String[] args) {
        // typical: the textbook example, no ties so the codes are fully determined
        runCase("case 1 (a..f, freq 5 9 12 13 16 45)",
                new char[]{'a', 'b', 'c', 'd', 'e', 'f'}, new int[]{5, 9, 12, 13, 16, 45},
                "abcdef", 224, "{a=1100, b=1101, c=100, d=101, e=111, f=0}");

        // edge: single symbol -> root is a leaf; used to NPE in decode
        runCase("case 2 (single symbol a)",
                new char[]{'a'}, new int[]{3},
                "aaa", 3, "{a=1}");

        // tricky: all frequencies tied -> tree shape depends on heap tie-breaks, but every
        // code has length 2 and the total is fixed, so only the totals are asserted
        runCase("case 3 (a b c d all freq 1)",
                new char[]{'a', 'b', 'c', 'd'}, new int[]{1, 1, 1, 1},
                "abcdabcd", 8, null);
    }
}
