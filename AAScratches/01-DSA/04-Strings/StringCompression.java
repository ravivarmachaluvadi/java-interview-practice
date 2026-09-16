/**
 * Problem: In-place compression of a character array by replacing consecutive duplicate characters with the character followed by its count.
 *
 * Approach: Two pointers iterate over the array; the read pointer scans runs of identical characters, while the write pointer records the compressed form directly into the same array. Counts are converted to strings and written digit by digit.
 *
 * Time Complexity: O(n), where n is the length of the input array (each element is processed once).
 * Space Complexity: O(1) auxiliary space (in-place modification; only a few integer variables are used).
 */
class StringCompression {
    public static int compress(char[] chars) {
        int left = 0; // Write index
        int right = 0; // Read index

        while (right < chars.length) {
            char currentChar = chars[right];
            int count = 0;

            // before run starts current compared
            // to itself and incremented to 1
            while (right < chars.length && chars[right] == currentChar) {
                right++;
                count++;
            }
            // Write the character
            chars[left++] = currentChar;
            if (count > 1) {
                for (char c : String.valueOf(count).toCharArray()) {
                    chars[left++] = c;
                }
            }
        }
        return left;
    }

    public static void main(String[] args) {

        char[] chars1 = {'a', 'a', 'b', 'b', 'c', 'c', 'c'};
        char[] chars2 = {'a'};
        char[] chars3 = {'a', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b'};

        System.out.println(compress(chars1)); // Output: 6
        System.out.println(String.valueOf(chars1, 0, 6)); // "a2b2c3"

        System.out.println(compress(chars2)); // Output: 1
        System.out.println(String.valueOf(chars2, 0, 1)); // "a"

        System.out.println(compress(chars3)); // Output: 4
        System.out.println(String.valueOf(chars3, 0, 4)); // "ab12"
    }
}
