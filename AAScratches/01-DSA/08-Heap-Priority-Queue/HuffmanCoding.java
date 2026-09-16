import java.util.*;

// Node class represents each character and its frequency
class Node {
    char ch;
    int freq;
    Node left = null, right = null;

    Node(char ch, int freq) {
        this.ch = ch;
        this.freq = freq;
    }

    Node(char ch, int freq, Node left, Node right) {
        this.ch = ch;
        this.freq = freq;
        this.left = left;
        this.right = right;
    }
}

public class HuffmanCoding {

    // Recursively encode each character using Huffman tree
    private static void getHuffmanCodes(Node root, String pathAsCode, Map<Character, String> huffmanCode) {
        if (root == null) return;

        // Leaf node, assign code to character
        if (root.left == null && root.right == null) {
            huffmanCode.put(root.ch, !pathAsCode.isEmpty() ? pathAsCode : "1");
        }

        getHuffmanCodes(root.left, pathAsCode + '0', huffmanCode);
        getHuffmanCodes(root.right, pathAsCode + '1', huffmanCode);
    }

    // Build the Huffman Tree and encode each character
    public static Map<Character, String> getEncodedHuffmanMap(char[] characters, int[] frequencies) {
        PriorityQueue<Node> minHeap = new PriorityQueue<>((l, r) -> l.freq - r.freq);

        for (int i = 0; i < characters.length; i++) {
            minHeap.add(new Node(characters[i], frequencies[i]));
        }

        while (minHeap.size() != 1) {
            Node left = minHeap.poll();
            Node right = minHeap.poll();
            int sum = left.freq + right.freq;
            minHeap.add(new Node('\0', sum, left, right));
        }

        Node root = minHeap.peek();
        Map<Character, String> huffmanCharToCode = new HashMap<>();
        getHuffmanCodes(root, "", huffmanCharToCode);
        return huffmanCharToCode;
    }

    // Decode a given string using Huffman Tree
    public static String decodeHuffman(Node root, String encodedString) {
        StringBuilder decodedString = new StringBuilder();
        Node current = root;

        for (char bit : encodedString.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;

            if (current.left == null && current.right == null) {
                decodedString.append(current.ch);
                current = root;
            }
        }
        return decodedString.toString();
    }


    // Helper method to construct the Huffman tree
    private static Node buildHuffmanTreeForDecoding(char[] characters, int[] frequencies) {

        PriorityQueue<Node> pq = new PriorityQueue<>((l, r) -> l.freq - r.freq);

        for (int i = 0; i < characters.length; i++) {
            pq.add(new Node(characters[i], frequencies[i]));
        }

        while (pq.size() != 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            int sum = left.freq + right.freq;
            pq.add(new Node('\0', sum, left, right));
        }

        return pq.peek();
    }


    public static void main(String[] args) {
        char[] characters = {'a', 'b', 'c', 'd', 'e', 'f'};
        int[] frequencies = {5, 9, 12, 13, 16, 45};

        Map<Character, String> huffmanCode = getEncodedHuffmanMap(characters, frequencies);

        System.out.println("Huffman Codes:");
        huffmanCode.forEach((ch, code) -> System.out.println(ch + ": " + code));

        // Sample encoding and decoding
        String original = "abcdef";
        StringBuilder encodedString = getEncodedString(original, huffmanCode);
        System.out.println("Encoded string: " + encodedString);

        // Decoding the encoded string
        Node root = buildHuffmanTreeForDecoding(characters, frequencies);
        String decodedString = decodeHuffman(root, encodedString.toString());
        System.out.println("Decoded string: " + decodedString);
    }

    private static StringBuilder getEncodedString(String original, Map<Character, String> huffmanCode) {
        StringBuilder encodedString = new StringBuilder();

        for (char ch : original.toCharArray()) {
            encodedString.append(huffmanCode.get(ch));
        }
        return encodedString;
    }
}
