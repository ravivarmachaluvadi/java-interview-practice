import java.util.ArrayList;
import java.util.List;

class RabinKarpAlgorithm {
    // Function to find the starting index of all occurrences of pattern in text
    public List<Integer> search(String pat, String txt) {
        int n = pat.length();
        int m = txt.length();
        // Primes for Rabin-Karp algorithm
        int p = 7, mod = 101;

        // To store the hash values of pattern and substring of text
        int hashPat = 0, hashText = 0;

        int pRight = 1, pLeft = 1;

        // Computing the initial hash values
        for (int i = 0; i < n; i++) {
            hashPat = (hashPat + ((pat.charAt(i) - 'a' + 1) * pRight) % mod) % mod;
            hashText = (hashText + ((txt.charAt(i) - 'a' + 1) * pRight) % mod) % mod;
            pRight = (pRight * p) % mod;
        }

        // List to store the result
        List<Integer> ans = new ArrayList<>();

        // Traverse the text string
        for (int i = 0; i <= m - n; i++) {

            // If the hash value matches
            if (hashPat == hashText) {
                // Add the index of the result if the substring matches
                if (txt.substring(i, i + n).equals(pat)) ans.add(i);
            }

            // Updating the hash values
            if (i < m - n) {
                hashText = (hashText - ((txt.charAt(i) - 'a' + 1) * pLeft) % mod + mod) % mod;
                hashText = (hashText + ((txt.charAt(i + n) - 'a' + 1) * pRight) % mod) % mod;
                hashPat = (hashPat * p) % mod;

                // Updating the prime multiples
                pLeft = (pLeft * p) % mod;
                pRight = (pRight * p) % mod;
            }
        }

        return ans; // Return the stored result
    }

    public static void main(String[] args) {
        RabinKarpAlgorithm rk = new RabinKarpAlgorithm();
        String txt = "ababcababcabc";
        String pat = "abc";
        List<Integer> result = rk.search(pat, txt);
        System.out.println("Pattern found at indices: " + result);
    }
}