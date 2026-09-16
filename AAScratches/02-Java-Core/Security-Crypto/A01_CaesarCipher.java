class CaesarCipher {

    // Method to encrypt the plaintext using Caesar cipher
    public static String encrypt(String plaintext, int shift) {
        StringBuilder encryptedText = new StringBuilder();
        // Iterate over each character in the plaintext
        for (char ch : plaintext.toCharArray()) {
            // Encrypt uppercase letters
            if (Character.isUpperCase(ch)) {
                char encryptedChar = (char) (((ch - 'A' + shift) % 26) + 'A');
                encryptedText.append(encryptedChar);
            }
            // Encrypt lowercase letters
            else if (Character.isLowerCase(ch)) {
                char encryptedChar = (char) (((ch - 'a' + shift) % 26) + 'a');
                encryptedText.append(encryptedChar);
            }
            // Non-alphabetical characters remain unchanged
            else {
                encryptedText.append(ch);
            }
        }

        return encryptedText.toString();
    }

    public static void main(String[] args) {
        String plaintext = "Hello, World!";
        int shift = 3; // Shift value for the cipher

        String encryptedText = encrypt(plaintext, shift);
        System.out.println("Original Text: " + plaintext); // Hello, World!
        System.out.println("Encrypted Text: " + encryptedText); // Khoor, Zruog!
    }
}
