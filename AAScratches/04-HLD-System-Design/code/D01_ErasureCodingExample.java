import java.util.Arrays;

/**
 * Instead of simply replicating data (e.g., keeping 3 full copies), erasure encoding splits
 * data into smaller fragments and adds redundant parity fragments. Using these fragments,
 * the system can reconstruct the original data even if some fragments are lost.
 * <p>
 * 🔑 Key Idea
 * <p>
 * Break data into k data blocks.
 * <p>
 * Compute m parity blocks using mathematical functions (often XOR or Reed-Solomon).
 * <p>
 * Store total of k + m blocks across nodes.
 * <p>
 * If up to m blocks are lost, original data can still be reconstructed.
 * <p>
 * This gives the same fault-tolerance as replication, but with less storage overhead.
 * <p>
 * 📖 Simple Example (XOR-based, not full Reed-Solomon)
 * <p>
 * Suppose we have:
 * <p>
 * k = 2 (2 data blocks)
 * <p>
 * m = 1 (1 parity block)
 *
 */
class ErasureCodingExample {

    // XOR-based parity calculation
    public static byte[] xor(byte[] a, byte[] b) {
        byte[] result = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            // xor of bytes and after xorring cating to byte type
            result[i] = (byte) (a[i] ^ b[i]);
        }
        return result;
    }

    public static void main(String[] args) {
        // Example data blocks
        byte[] D1 = "HELLO123".getBytes();
        byte[] D2 = "WORLD456".getBytes();

        // Generate parity
        byte[] P = xor(D1, D2);

        System.out.println("D1 = " + new String(D1));
        System.out.println("D2 = " + new String(D2));
        System.out.println("P  = " + Arrays.toString(P));

        // Simulate data loss: D2 is lost
        byte[] recoveredD2 = xor(D1, P);
        System.out.println("Recovered D2 = " + new String(recoveredD2));
    }
}
