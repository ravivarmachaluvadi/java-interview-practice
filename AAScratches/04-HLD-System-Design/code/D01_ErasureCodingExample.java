/*
 * =====================================================================
 *  Erasure Coding - XOR parity instead of 3x replication   HLD building block
 * =====================================================================
 *
 * PROBLEM
 *   Survive node loss without paying 3x storage. Split an object into k data blocks,
 *   compute m parity blocks, spread all k+m across different failure domains. Any k of
 *   the k+m blocks are enough to rebuild the object, so up to m losses are survivable at
 *   (k+m)/k storage overhead instead of 3.0. This file implements the m = 1 case, where
 *   the parity is a plain XOR - the same algebra as RAID-5.
 *
 * WORKED EXAMPLE
 *   D1 = "HELLO123", D2 = "WORLD456", P = D1 XOR D2
 *   D2 is lost  ->  D1 XOR P = D1 XOR (D1 XOR D2) = D2       (recovered exactly)
 *   k=2, m=1 -> 1.5x storage, survives 1 loss. 3x replication -> 3.0x, survives 2.
 *
 * DESIGN (classes and why)
 *   xor(a, b)           - byte-wise XOR; the one primitive everything else is built on.
 *   split(data, k)      - cuts a payload into k equal-length blocks, zero-padding the
 *                         tail. Equal length is a hard requirement of the algebra, which
 *                         is why splitting and padding belong together in one method.
 *   parity(blocks)      - XOR-folds all k blocks into the single m=1 parity block.
 *   recoverMissing(...) - XORs the parity with every surviving data block; whatever is
 *                         left is the lost one. Self-inverse: XOR cancels itself out.
 *   join(blocks, len)   - reassembles and trims the padding back off.
 *
 * KEY DECISIONS
 *   Fixed: xor() read b[i] for i up to a.length, so unequal blocks threw
 *     ArrayIndexOutOfBoundsException (or silently truncated). It now rejects a length
 *     mismatch, and split() guarantees equal blocks by padding.
 *   XOR gives exactly m = 1. Two simultaneous losses leave two unknowns in one equation
 *     and are unrecoverable - recoverMissing() refuses rather than returning garbage.
 *     Real m >= 2 needs Reed-Solomon over GF(2^8): same idea, a solvable linear system.
 *   Padding is tracked by keeping the original length; the trailing zero bytes are
 *     indistinguishable from real data otherwise.
 *
 * COMPLEXITY
 *   Time  O(n) to encode, O(n) to rebuild one block (n = object size).
 *   Space (k + m) / k times the object - 1.5x here, versus 3.0x for triple replication.
 *   Cost of the saving: a rebuild reads k blocks over the network; a replica read is
 *   one local copy. EC is for cold and warm data, replication for the hot path.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why does HDFS keep 3x replicas for hot data and EC (RS 6,3) for cold archives?
 *   - Reed-Solomon vs XOR: what does GF(2^8) buy that XOR cannot give you?
 *   - Degraded read: serving a read while a block is missing costs k fetches plus the
 *     rebuild. How do you keep tail latency sane - hedged requests, or a hot replica?
 *   - Placement: all k+m blocks must sit in distinct racks/AZs or m is a fiction.
 *
 * RUN
 *   main() runs 4 cases (the classic k=2 recovery, k=5 with padding, losing the parity
 *   block, and a fatal double loss) and prints actual vs expected.
 */

import java.util.Arrays;

class ErasureCodingExample {

    /** Byte-wise XOR. Both blocks must be the same length - see split(). */
    public static byte[] xor(byte[] a, byte[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException(
                    "Blocks must be equal length: " + a.length + " vs " + b.length);
        }
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            // ^ promotes to int, so cast the result back down to a byte.
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }

    /** Cuts data into k equal blocks, zero-padding the last one. */
    public static byte[][] split(byte[] data, int k) {
        int blockSize = (data.length + k - 1) / k;   // ceiling division
        byte[][] blocks = new byte[k][blockSize];
        for (int i = 0; i < k; i++) {
            int from = Math.min(i * blockSize, data.length);
            int to = Math.min(from + blockSize, data.length);
            System.arraycopy(data, from, blocks[i], 0, to - from);  // rest stays zero
        }
        return blocks;
    }

    /** The single parity block for m = 1: every data block XOR-folded together. */
    public static byte[] parity(byte[][] blocks) {
        byte[] p = blocks[0].clone();
        for (int i = 1; i < blocks.length; i++) {
            p = xor(p, blocks[i]);
        }
        return p;
    }

    /**
     * Rebuilds the one lost data block. blocks[missingIndex] is ignored (it may be null);
     * every other entry must be present. XOR is self-inverse, so folding the parity with
     * all survivors cancels them out and leaves exactly the missing block.
     */
    public static byte[] recoverMissing(byte[][] blocks, byte[] parityBlock, int missingIndex) {
        byte[] rebuilt = parityBlock.clone();
        for (int i = 0; i < blocks.length; i++) {
            if (i == missingIndex) continue;
            if (blocks[i] == null) {
                throw new IllegalStateException(
                        "Two blocks lost with m=1 parity: unrecoverable");
            }
            rebuilt = xor(rebuilt, blocks[i]);
        }
        return rebuilt;
    }

    /** Reassembles the blocks and trims the padding added by split(). */
    public static byte[] join(byte[][] blocks, int originalLength) {
        byte[] all = new byte[blocks.length * blocks[0].length];
        for (int i = 0; i < blocks.length; i++) {
            System.arraycopy(blocks[i], 0, all, i * blocks[i].length, blocks[i].length);
        }
        return Arrays.copyOf(all, originalLength);
    }

    /** Storage cost of (k data + m parity) relative to one raw copy. */
    public static double storageOverhead(int k, int m) {
        return (double) (k + m) / k;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1 (typical): the classic k=2, m=1 stripe. Lose D2, rebuild it from D1 and P.
        byte[] d1 = "HELLO123".getBytes();
        byte[] d2 = "WORLD456".getBytes();
        byte[] p = xor(d1, d2);
        System.out.println("D1 = " + new String(d1) + ", D2 = " + new String(d2));
        System.out.println("P  = " + Arrays.toString(p));
        byte[] recovered = xor(d1, p);                       // D1 XOR (D1 XOR D2) = D2
        print("case 1 recovered D2", new String(recovered), "WORLD456");
        print("case 1 overhead k=2,m=1 (3x replication would be 3.0x)",
                storageOverhead(2, 1) + "x", "1.5x");

        // Case 2 (edge - padding): 36 bytes over k=5 does not divide evenly, so the last
        // block is zero-padded. Lose block 2 and rebuild the whole payload byte for byte.
        String payload = "erasure coding beats 3x replication!";
        byte[] data = payload.getBytes();
        int k = 5;
        byte[][] blocks = split(data, k);
        byte[] parityBlock = parity(blocks);
        print("case 2 block size, ceil(36/5)", blocks[0].length, 8);

        byte[] lostBlock = blocks[2];
        blocks[2] = null;                                     // node 2 is gone
        byte[] rebuilt = recoverMissing(blocks, parityBlock, 2);
        print("case 2 rebuilt block matches", Arrays.equals(rebuilt, lostBlock), true);
        blocks[2] = rebuilt;
        print("case 2 payload after rebuild", new String(join(blocks, data.length)), payload);

        // Case 3 (edge): the PARITY block is the one lost. Data is complete, so there is
        // nothing to reconstruct - just recompute the parity from the surviving blocks.
        byte[] recomputed = parity(blocks);
        print("case 3 parity recomputed", Arrays.equals(recomputed, parityBlock), true);

        // Case 4 (tricky): two losses with m=1 leave two unknowns in one equation.
        // The system must say so rather than hand back plausible garbage.
        blocks[0] = null;
        blocks[3] = null;
        String outcome;
        try {
            recoverMissing(blocks, parityBlock, 0);
            outcome = "returned data";
        } catch (IllegalStateException e) {
            outcome = "unrecoverable";
        }
        print("case 4 two blocks lost, m=1", outcome, "unrecoverable");
        print("case 4 RS(4,2) overhead, same 1.5x but survives 2 losses",
                storageOverhead(4, 2) + "x", "1.5x");
    }
}
