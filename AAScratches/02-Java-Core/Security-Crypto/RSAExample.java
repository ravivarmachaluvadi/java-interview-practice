/**
 * Demonstrates RSA asymmetric encryption and decryption.
 *
 * The program generates a 2048‑bit RSA key pair, encrypts a short
 * plaintext string with the public key, then decrypts it back to
 * plain text using the private key. It prints the original,
 * encrypted (Base64), and decrypted strings.
 *
 * Approach:
 * 1. Generate RSA KeyPair (public/private).
 * 2. Encrypt plaintext bytes with Cipher.ENCRYPT_MODE using the public key.
 * 3. Encode ciphertext in Base64 for display.
 * 4. Decode Base64, decrypt with Cipher.DECRYPT_MODE using the private key,
 *    and convert back to a string.
 *
 * Time Complexity: O(n) where n is the length of the plaintext (RSA
 * operates on fixed-size blocks; overall cost dominated by key generation).
 * Space Complexity: O(1) additional space beyond input/output buffers.
 */
import java.security.*;
import javax.crypto.Cipher;
import java.util.Base64;

class RSAExample {
    public static void main(String[] args) throws Exception {
        // 1. Generate RSA key pair
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(2048); // key size
        KeyPair pair = keyPairGen.generateKeyPair();
        PublicKey publicKey = pair.getPublic();
        PrivateKey privateKey = pair.getPrivate();

        String plainText = "Hello Asymmetric RSA!";
        System.out.println("Original Text: " + plainText);

        // 2. Encrypt with Public Key
        Cipher encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = encryptCipher.doFinal(plainText.getBytes());
        String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
        System.out.println("Encrypted Text: " + encryptedText);

        // 3. Decrypt with Private Key
        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = decryptCipher.doFinal(Base64.getDecoder().decode(encryptedText));
        String decryptedText = new String(decryptedBytes);
        System.out.println("Decrypted Text: " + decryptedText);
    }
}
