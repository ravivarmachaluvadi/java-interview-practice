/**
 * Demonstrates AES encryption and decryption using a 128‑bit key.
 *
 * The program encrypts the string "Hello AES!" with AES in ECB mode
 * (default for Cipher.getInstance("AES")) and then decrypts it back,
 * printing both the Base64‑encoded ciphertext and the recovered plaintext.
 *
 * Approach:
 * 1. Create a SecretKeySpec from a 16‑byte key string.
 * 2. Initialize a Cipher for ENCRYPT_MODE, encrypt the plaintext bytes,
 *    and encode the result with Base64.
 * 3. Reinitialize the same Cipher for DECRYPT_MODE, decode the Base64
 *    ciphertext, decrypt it, and convert back to a String.
 *
 * Time Complexity: O(n) where n is the length of the input text (each byte
 *                  processed once by the cipher).
 * Space Complexity: O(n) for storing the encrypted bytes and the Base64
 *                   representation. */
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

class AESExample {
    public static void main(String[] args) throws Exception {
        String plainText = "Hello AES!";
        String key = "1234567890123456"; // 16-byte key for AES-128

        // 1. Create AES key spec
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");

        // 2. Encrypt
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        String encryptedText = Base64.getEncoder().encodeToString(encrypted);
        System.out.println("Encrypted: " + encryptedText);

        // 3. Decrypt
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
        System.out.println("Decrypted: " + new String(decrypted));
    }
}
