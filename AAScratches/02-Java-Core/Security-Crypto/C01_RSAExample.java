/*
 * =====================================================================
 *  RSA asymmetric encryption, and why it needs AES            Medium
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   The same Cipher pipeline as A02_AESExample, but with two different keys:
 *   anyone holding the public key can encrypt, only the private key can
 *   decrypt. It then shows the constraint nobody expects - RSA can only
 *   encrypt a few hundred bytes - and the standard way around it: hybrid
 *   encryption, where RSA carries a one-off AES key and AES carries the data.
 *
 * WHAT YOU WILL SEE
 *   round trip   : "Hello Asymmetric RSA!" -> Base64 -> back again
 *   block size   : a 2048-bit key always emits exactly 256 ciphertext bytes
 *   size ceiling : encrypting 300 bytes throws IllegalBlockSizeException
 *   OAEP         : the padding you should actually ask for, round-tripping
 *   hybrid       : a long message encrypted by AES, its key wrapped by RSA
 *
 * HOW IT WORKS
 *   1. KeyPairGenerator.getInstance("RSA").initialize(2048) produces a matched
 *      PublicKey and PrivateKey. This step is the slow one - hundreds of
 *      milliseconds - because it hunts for large primes.
 *   2. Encrypt: Cipher.getInstance("RSA"), init(ENCRYPT_MODE, publicKey),
 *      doFinal(bytes). Base64 turns the raw bytes into something printable.
 *   3. Decrypt: the same transformation, init(DECRYPT_MODE, privateKey).
 *   4. The ceiling: RSA encrypts one number smaller than the modulus, so the
 *      payload limit is keySize/8 minus padding overhead. 2048-bit key with
 *      PKCS#1 v1.5 padding = 256 - 11 = 245 bytes. Over that, doFinal throws.
 *   5. Hybrid: KeyGenerator makes a random AES-256 key, AES/GCM encrypts the
 *      real payload, RSA encrypts just the 32-byte AES key. The receiver
 *      unwraps the key with its private key, then decrypts the payload.
 *
 * KEY INSIGHT
 *   RSA is a key-transport mechanism, not a data-encryption mechanism. It is
 *   roughly a thousand times slower than AES and bounded by the modulus size,
 *   so every real protocol - TLS, PGP, JWE - uses it to move a symmetric key
 *   and lets AES do the bulk work. The other half of the insight is direction:
 *   encrypt with the PUBLIC key so only the private key can read it; sign with
 *   the PRIVATE key so any public key holder can verify it. Swapping those two
 *   is the classic interview trap.
 *
 * GOTCHAS
 *   - Cipher.getInstance("RSA") means RSA/ECB/PKCS1Padding. Prefer
 *     "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"; PKCS#1 v1.5 has known attacks.
 *   - "ECB" in an RSA transformation is a naming artefact - there is one block.
 *   - Never loop RSA over chunks of a large message. Use hybrid encryption.
 *   - Encryption is not signing. Signature uses java.security.Signature, and a
 *     signature proves origin, not secrecy.
 *   - Key generation is expensive: generate once and reuse, never per request.
 *   - Below 2048 bits is considered broken; 3072+ for long-lived data.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Walk through a TLS handshake: where exactly does the asymmetric part stop?
 *   - RSA vs ECDSA/ECDH: why do modern systems prefer elliptic curves?
 *   - How do you distribute and trust a public key? (certificates, CAs, pinning)
 *   - Sign-then-encrypt or encrypt-then-sign, and what goes wrong either way?
 *
 * RUN
 *   main() runs 5 cases (round trip, ciphertext length, oversized payload,
 *   OAEP round trip, hybrid round trip) and prints actual vs expected.
 */

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

class RSAExample {

    private static final String PKCS1 = "RSA";  // == RSA/ECB/PKCS1Padding
    private static final String OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private static final int GCM_IV_BYTES = 12;
    private static final int GCM_TAG_BITS = 128;
    private static final SecureRandom RANDOM = new SecureRandom();

    static KeyPair newKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048); // 256-byte modulus -> 256-byte ciphertext
        return generator.generateKeyPair();
    }

    static byte[] encrypt(String transformation, PublicKey publicKey, byte[] data)
            throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey); // public key locks it
        return cipher.doFinal(data);
    }

    static byte[] decrypt(String transformation, PrivateKey privateKey, byte[] data)
            throws Exception {
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, privateKey); // only the private key opens it
        return cipher.doFinal(data);
    }

    // ------------------------------------------------------------- hybrid ---
    // RSA moves the key, AES moves the data. This is what TLS and JWE do.

    /** Returns [rsa-wrapped AES key | iv | AES-GCM ciphertext], Base64 encoded. */
    static String hybridEncrypt(PublicKey publicKey, String plainText) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey sessionKey = keyGen.generateKey(); // one-off, never stored

        byte[] iv = new byte[GCM_IV_BYTES];
        RANDOM.nextBytes(iv);
        Cipher aes = Cipher.getInstance("AES/GCM/NoPadding");
        aes.init(Cipher.ENCRYPT_MODE, sessionKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
        byte[] payload = aes.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Only the 32-byte AES key goes through RSA - well under the ceiling.
        byte[] wrappedKey = encrypt(OAEP, publicKey, sessionKey.getEncoded());

        byte[] out = new byte[wrappedKey.length + iv.length + payload.length];
        System.arraycopy(wrappedKey, 0, out, 0, wrappedKey.length);
        System.arraycopy(iv, 0, out, wrappedKey.length, iv.length);
        System.arraycopy(payload, 0, out, wrappedKey.length + iv.length, payload.length);
        return Base64.getEncoder().encodeToString(out);
    }

    static String hybridDecrypt(PrivateKey privateKey, String base64) throws Exception {
        byte[] all = Base64.getDecoder().decode(base64);
        int wrappedLen = 256; // fixed by the 2048-bit key size

        byte[] wrappedKey = Arrays.copyOfRange(all, 0, wrappedLen);
        byte[] iv = Arrays.copyOfRange(all, wrappedLen, wrappedLen + GCM_IV_BYTES);
        byte[] payload = Arrays.copyOfRange(all, wrappedLen + GCM_IV_BYTES, all.length);

        byte[] keyBytes = decrypt(OAEP, privateKey, wrappedKey);
        SecretKeySpec sessionKey = new SecretKeySpec(keyBytes, "AES");

        Cipher aes = Cipher.getInstance("AES/GCM/NoPadding");
        aes.init(Cipher.DECRYPT_MODE, sessionKey, new GCMParameterSpec(GCM_TAG_BITS, iv));
        return new String(aes.doFinal(payload), StandardCharsets.UTF_8);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) throws Exception {
        KeyPair pair = newKeyPair(); // generate once, reuse for every case
        PublicKey publicKey = pair.getPublic();
        PrivateKey privateKey = pair.getPrivate();

        String plainText = "Hello Asymmetric RSA!";
        byte[] cipherBytes = encrypt(PKCS1, publicKey, plainText.getBytes(StandardCharsets.UTF_8));
        System.out.println("Ciphertext (Base64): "
                + Base64.getEncoder().encodeToString(cipherBytes));

        // case 1: typical - public key encrypts, private key decrypts
        String recovered =
                new String(decrypt(PKCS1, privateKey, cipherBytes), StandardCharsets.UTF_8);
        print("case 1 round trip     ", recovered, plainText);

        // case 2: output size is the key size, not the message size
        print("case 2 cipher bytes   ", cipherBytes.length, 256);

        // case 3: edge - 300 bytes exceeds the 245-byte PKCS#1 ceiling
        byte[] tooBig = new byte[300];
        Arrays.fill(tooBig, (byte) 'x');
        String verdict;
        try {
            encrypt(PKCS1, publicKey, tooBig);
            verdict = "no exception";
        } catch (javax.crypto.IllegalBlockSizeException e) {
            verdict = "IllegalBlockSizeException";
        }
        print("case 3 300-byte input ", verdict, "IllegalBlockSizeException");

        // case 4: OAEP padding - the transformation to prefer in real code
        byte[] oaep = encrypt(OAEP, publicKey, plainText.getBytes(StandardCharsets.UTF_8));
        print("case 4 OAEP round trip",
                new String(decrypt(OAEP, privateKey, oaep), StandardCharsets.UTF_8), plainText);

        // case 5: tricky - the same 300-byte payload that RSA refused now
        // succeeds, because AES carries it and RSA only carries the AES key
        String longText = "x".repeat(300);
        String hybrid = hybridEncrypt(publicKey, longText);
        print("case 5 hybrid 300 chars", hybridDecrypt(privateKey, hybrid).equals(longText), true);
    }
}
