/*
 * =====================================================================
 *  Caesar Cipher - encrypt and decrypt                    Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a text and an integer shift key, replace every letter by the letter
 *   'shift' positions later in the alphabet, wrapping Z back to A. Case is
 *   preserved and non-letters (spaces, punctuation, digits) pass through
 *   untouched. Decryption is the same walk with the shift reversed.
 *
 * EXAMPLE
 *   encrypt("Hello, World!", 3)  ->  "Khoor, Zruog!"   H+3=K, comma untouched
 *   encrypt("xyz XYZ", 3)        ->  "abc ABC"         wraps past z and Z
 *   decrypt("Khoor, Zruog!", 3)  ->  "Hello, World!"   shift back by 3
 *   encrypt("Hello", 29)         ->  "Khoor"           29 mod 26 == 3
 *
 * APPROACH  (modular arithmetic on a 0..25 alphabet index)
 *   1. Normalise the key once: ((shift % 26) + 26) % 26, so any int - negative,
 *      zero, or huge - becomes a forward shift in 0..25.
 *   2. Walk the string one char at a time.
 *   3. For a letter, pick its base ('A' or 'a'), convert to an index with
 *      ch - base, add the shift, take % 26, then add the base back.
 *   4. Append anything that is not a letter unchanged.
 *   5. decrypt(text, shift) is just encrypt(text, -shift) - step 1 makes the
 *      negative key safe.
 *
 * KEY INSIGHT
 *   Mapping a letter to 0..25 turns the alphabet into a ring, and a shift
 *   cipher is addition on that ring. Because addition on a ring is invertible,
 *   encryption and decryption are the same code with opposite signs - that is
 *   the whole idea of a symmetric key. Recognise the pattern any time a problem
 *   says "wrap around": subtract the base, do the maths mod n, add the base.
 *   Fixed: the original used (ch - base + shift) % 26 directly, which returns a
 *   negative index for a negative shift and produced garbage control characters;
 *   normalising the key first removes that whole class of bug.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass, constant work per character
 *   Space O(n)  the StringBuilder holding the result; O(1) besides the output
 *
 * INTERVIEW FOLLOW-UPS
 *   - Break it without the key: 26 brute-force shifts, or letter-frequency analysis.
 *   - Generalise to a Vigenere cipher, where the shift cycles through a keyword.
 *   - Why is this not encryption in any real sense? (tiny key space, no diffusion)
 *   - Shift over the full byte range instead of A-Z: what breaks for Unicode text?
 *
 * RUN
 *   main() runs 7 cases (typical, round trip, wrap-around, empty, zero key,
 *   key > 26, negative key) and prints actual vs expected.
 */

class CaesarCipher {

    private static final int ALPHABET_SIZE = 26;

    /** Shifts every letter forward by 'shift'; leaves everything else alone. */
    public static String encrypt(String plaintext, int shift) {
        // Fold any int into 0..25 so a negative or oversized key still works.
        int key = ((shift % ALPHABET_SIZE) + ALPHABET_SIZE) % ALPHABET_SIZE;

        StringBuilder out = new StringBuilder(plaintext.length());
        for (char ch : plaintext.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                out.append(shiftLetter(ch, 'A', key));
            } else if (Character.isLowerCase(ch)) {
                out.append(shiftLetter(ch, 'a', key));
            } else {
                out.append(ch); // digits, spaces, punctuation ride through
            }
        }
        return out.toString();
    }

    /** Decryption is encryption with the opposite shift - the symmetric part. */
    public static String decrypt(String cipherText, int shift) {
        return encrypt(cipherText, -shift);
    }

    /** ch -> index 0..25 -> add key -> wrap -> back to a char of the same case. */
    private static char shiftLetter(char ch, char base, int key) {
        return (char) (((ch - base + key) % ALPHABET_SIZE) + base);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // case 1: typical - letters shift, punctuation does not
        print("case 1 encrypt", encrypt("Hello, World!", 3), "Khoor, Zruog!");

        // case 2: the same text decrypts back - symmetry check
        print("case 2 decrypt", decrypt("Khoor, Zruog!", 3), "Hello, World!");

        // case 3: wrap-around at both ends of the alphabet
        print("case 3 wrap   ", encrypt("xyz XYZ", 3), "abc ABC");

        // case 4: edge - empty text and a zero key are both identity
        print("case 4 empty  ", "[" + encrypt("", 5) + "]", "[]");
        print("case 5 key 0  ", encrypt("Hello", 0), "Hello");

        // case 6: tricky - keys outside 0..25 must behave like their mod-26 value
        print("case 6 key 29 ", encrypt("Hello", 29), "Khoor");
        print("case 7 key -3 ", encrypt("Khoor", -3), "Hello");
    }
}
