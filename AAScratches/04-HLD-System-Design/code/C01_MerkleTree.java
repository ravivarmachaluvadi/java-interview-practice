/*
 * =====================================================================
 *  Merkle Tree - anti-entropy between replicas          HLD building block
 * =====================================================================
 *
 * PROBLEM
 *   Two replicas each hold the same N blocks of data. Some unknown subset has drifted
 *   apart (a dropped write, a hinted handoff that never landed). Find WHICH blocks differ
 *   without shipping all N blocks across the network. Cassandra, DynamoDB, Riak and
 *   git all answer this the same way: compare hash trees, not data.
 *
 * SHAPE
 *   leaves: h(block0) h(block1) h(block2) h(block3)
 *   level1:   h(h0+h1)           h(h2+h3)
 *   root:            h(l0 + l1) Equal roots -> the two replicas are identical, proven in ONE hash
 *   comparison.
 *   Different roots -> descend only into children that disagree; each step halves the
 *   search space, so one bad block costs ~log2(N) comparisons instead of N.
 *
 * DESIGN (classes and why)
 *   sha256(String)              - the hash primitive; any collision-resistant hash works.
 *   buildLevels(List<String>)   - returns every level bottom-up, leaves at index 0 and
 *                                 the root as the single entry of the last level. Keeping
 *                                 the levels (not just the root) is what makes diffing
 *                                 possible; a root alone only answers "same or not".
 *   getMerkleRoot(List<String>) - convenience wrapper over buildLevels.
 *   findDifferingLeaves(a, b)   - walks both trees top-down, following only mismatched
 *                                 nodes, and reports the leaf indexes that actually
 *                                 differ. This is the anti-entropy repair step.
 *
 * KEY DECISIONS
 *   Odd level -> the last hash is paired with itself (Bitcoin's rule). Simple, but it
 *     makes [a,b,c] and [a,b,c,c] hash to the same root (CVE-2012-2459). Case 4 shows it.
 *   Hashes are compared, never the data, so the transfer is O(log N) small strings even
 *     when the blocks are megabytes.
 *   Real systems hash RANGES of the key space at the leaves, not single rows, and stop
 *     the tree at a fixed depth (Cassandra: 2^15 leaves) to bound memory.
 *
 * COMPLEXITY
 *   Time  O(N) to build (each level halves: N + N/2 + N/4 ... = 2N hashes).
 *   Time  O(N) per findDifferingLeaves() call here - it rebuilds both trees first.
 *         The descent itself is only O(d * log N) for d differing leaves, which is what
 *         a real system pays because it keeps the trees built and warm.
 *   Space O(N) for the stored levels.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Merkle tree vs a single checksum over everything: both detect drift, only the tree
 *     localises it, and a checksum cannot be updated incrementally.
 *   - What changes when a leaf is updated? Only the path to the root: O(log N) rehashes.
 *   - Merkle proof: prove a block is in the set with log N sibling hashes (SPV clients).
 *   - When is this the wrong tool? High churn - the tree is rebuilt faster than it is
 *     used; vector clocks or read-repair handle the hot path instead.
 *
 * RUN
 *   main() runs 5 cases (identical replicas, one drifted block, empty input, the
 *   duplicate-leaf collision, determinism) and prints actual vs expected.
 */

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MerkleTreeExample {

    /** SHA-256 of a string, rendered as lowercase hex. */
    public static String sha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                // 0xff & b undoes Java's signed byte; then pad single digits to two chars.
                String digits = Integer.toHexString(0xff & b);
                if (digits.length() == 1) hex.append('0');
                hex.append(digits);
            }
            return hex.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Builds the whole tree bottom-up and keeps every level.
     * levels.get(0) = leaf hashes, levels.get(last) = a single-element list holding the root.
     */
    public static List<List<String>> buildLevels(List<String> dataBlocks) {
        List<List<String>> levels = new ArrayList<>();
        if (dataBlocks.isEmpty()) return levels;

        List<String> current = new ArrayList<>();
        for (String data : dataBlocks) current.add(sha256(data));
        levels.add(current);

        while (current.size() > 1) {
            List<String> parents = new ArrayList<>();
            for (int i = 0; i < current.size(); i += 2) {
                String left = current.get(i);
                // Odd count: the last node is paired with itself (Bitcoin's rule).
                String right = (i + 1 < current.size()) ? current.get(i + 1) : left;
                parents.add(sha256(left + right));
            }
            levels.add(parents);
            current = parents;
        }
        return levels;
    }

    /** The root hash: a single fingerprint of the entire data set. "" for no data. */
    public static String getMerkleRoot(List<String> dataBlocks) {
        List<List<String>> levels = buildLevels(dataBlocks);
        if (levels.isEmpty()) return "";
        return levels.get(levels.size() - 1).get(0);
    }

    /**
     * Anti-entropy: which leaf indexes differ between two replicas of the same size.
     * Descends from the root and follows only the children whose hashes disagree, so an
     * identical subtree is dismissed with one comparison however large it is.
     */
    public static List<Integer> findDifferingLeaves(List<String> replicaA, List<String> replicaB) {
        if (replicaA.size() != replicaB.size()) {
            throw new IllegalArgumentException("Replicas must hold the same number of blocks");
        }
        List<Integer> differing = new ArrayList<>();
        List<List<String>> a = buildLevels(replicaA);
        List<List<String>> b = buildLevels(replicaB);
        if (a.isEmpty()) return differing;

        int top = a.size() - 1;
        // Node indexes still under suspicion at the current level, starting with the root.
        List<Integer> suspects = new ArrayList<>(List.of(0));
        if (a.get(top).get(0).equals(b.get(top).get(0))) return differing;  // roots match

        for (int level = top; level > 0; level--) {
            List<Integer> nextSuspects = new ArrayList<>();
            for (int node : suspects) {
                // A parent at index n covers children 2n and 2n+1 on the level below.
                for (int child = 2 * node; child <= 2 * node + 1; child++) {
                    if (child >= a.get(level - 1).size()) continue;  // odd level, no sibling
                    if (!a.get(level - 1).get(child).equals(b.get(level - 1).get(child))) {
                        nextSuspects.add(child);
                    }
                }
            }
            suspects = nextSuspects;
        }
        differing.addAll(suspects);
        return differing;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    private static String shortHash(String hash) {
        return hash.isEmpty() ? "(empty)" : hash.substring(0, 12) + "...";
    }

    public static void main(String[] args) {
        List<String> replicaA = Arrays.asList(
                "Alice pays Bob 10 BTC",
                "Bob pays Charlie 5 BTC",
                "Charlie pays Dave 2 BTC",
                "Dave pays Eve 1 BTC");

        System.out.println("replica A root : " + shortHash(getMerkleRoot(replicaA)));

        // Case 1 (typical): a replica that received every write agrees at the root, so
        // anti-entropy stops after one comparison.
        List<String> healthy = new ArrayList<>(replicaA);
        print("case 1 roots equal", getMerkleRoot(healthy).equals(getMerkleRoot(replicaA)), true);
        print("case 1 differing leaves", findDifferingLeaves(replicaA, healthy), "[]");

        // Case 2 (the point): one block drifted. The root differs, and the descent names
        // exactly which leaf to repair - no other block is transferred.
        List<String> drifted = new ArrayList<>(replicaA);
        drifted.set(2, "Charlie pays Dave 20 BTC");   // a dropped write, replayed wrong
        print("case 2 roots equal", getMerkleRoot(drifted).equals(getMerkleRoot(replicaA)), false);
        print("case 2 differing leaves", findDifferingLeaves(replicaA, drifted), "[2]");

        // Case 3 (edge): empty input, and a single block (the tree is just its own leaf).
        print("case 3 empty root", shortHash(getMerkleRoot(new ArrayList<>())), "(empty)");
        List<String> one = List.of("only block");
        print("case 3 single-block root equals leaf hash",
                getMerkleRoot(one).equals(sha256("only block")), true);

        // Case 4 (tricky): duplicating the odd last leaf makes [a,b,c] and [a,b,c,c]
        // collide - the Bitcoin CVE-2012-2459 weakness this simple pairing rule carries.
        List<String> three = List.of("a", "b", "c");
        List<String> threePlusDup = List.of("a", "b", "c", "c");
        print("case 4 [a,b,c] root == [a,b,c,c] root",
                getMerkleRoot(three).equals(getMerkleRoot(threePlusDup)), true);

        // Case 5 (edge): two differing leaves in different subtrees are both found, and
        // the root is deterministic across runs (same input -> same fingerprint).
        List<String> twoOff = new ArrayList<>(replicaA);
        twoOff.set(0, "Alice pays Bob 11 BTC");
        twoOff.set(3, "Dave pays Eve 7 BTC");
        print("case 5 differing leaves", findDifferingLeaves(replicaA, twoOff), "[0, 3]");
        print("case 5 root is deterministic",
                getMerkleRoot(replicaA).equals(getMerkleRoot(new ArrayList<>(replicaA))), true);
    }
}
