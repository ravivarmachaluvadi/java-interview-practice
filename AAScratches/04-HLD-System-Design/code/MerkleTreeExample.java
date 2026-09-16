import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

class MerkleTreeExample {
    // Utility: Generate SHA-256 hash
    public static String sha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    // Build Merkle Tree and return Merkle Root
    public static String getMerkleRoot(List<String> dataBlocks) {
        if (dataBlocks.isEmpty()) {
            return "";
        }

        List<String> tempHashes = new ArrayList<>();
        // derive hash for every string or data block
        for (String data : dataBlocks)
            tempHashes.add(sha256(data));

        // calculate for every adjacent pair and increment by 2
        while (tempHashes.size() > 1) {
            List<String> newLevel = new ArrayList<>();

            for (int i = 0; i < tempHashes.size(); i += 2) {
                // if last element is even
                if (i + 1 < tempHashes.size())
                    newLevel.add(sha256(tempHashes.get(i) + tempHashes.get(i + 1)));
                else
                    newLevel.add(sha256(tempHashes.get(i) + tempHashes.get(i)));
            }
            // replace hashLevel of hlaved one
            tempHashes = newLevel;
        }
        // thus boiled down to one single hash
        // return it as root level hash
        return tempHashes.get(0);
    }

    // Main Example
    public static void main(String[] args) {
        List<String> transactions = new ArrayList<>();
        transactions.add("Alice pays Bob 10 BTC");
        transactions.add("Bob pays Charlie 5 BTC");
        transactions.add("Charlie pays Dave 2 BTC");
        transactions.add("Dave pays Eve 1 BTC");

        System.out.println("Transactions:");
        for (String tx : transactions) {
            System.out.println(" - " + tx);
        }

        String merkleRoot = getMerkleRoot(transactions);
        System.out.println("\nMerkle Root: " + merkleRoot);
    }
}
