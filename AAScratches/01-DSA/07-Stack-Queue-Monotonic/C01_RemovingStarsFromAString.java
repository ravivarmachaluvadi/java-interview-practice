/*
 * =====================================================================
 *  Removing Stars From a String                    LeetCode 2390 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   s contains lowercase letters and '*'. Every '*' deletes the closest non-star
 *   character to its left and then disappears itself. The input guarantees a letter
 *   is always available to delete. Return the string that remains.
 *
 * EXAMPLE
 *   "leet**cod*e"  ->  "lecoe"   two stars erase "t","e"; the third erases "d"
 *   "erase*****"   ->  ""        every letter is erased
 *   "abc"          ->  "abc"     no stars, unchanged
 *
 * APPROACH  (in-place char array used as a stack)
 *   1. Copy s into a char[]; 'top' is the write pointer = current stack size.
 *   2. Letter: write it at chars[top] and advance top   (push).
 *   3. Star:   step top back by one                     (pop); the erased letter is
 *      simply overwritten by the next push.
 *   4. The answer is chars[0..top).
 *   A second method, removeStarsWithStringBuilder, does the same with an explicit
 *   StringBuilder as the stack (setLength(len - 1) is the pop). main() runs both.
 *
 * KEY INSIGHT
 *   "Delete the nearest thing to the left" is a stack pop. A stack is often just an
 *   index into an array you already own: push = write and advance, pop = step back.
 *   Overwriting in place is safe because the write pointer never overtakes the read
 *   pointer (top <= right at all times).
 *
 * COMPLEXITY
 *   Time  O(n)  one pass; each char is written at most once and erased at most once
 *   Space O(n)  the char[] copy that becomes the output (O(1) beyond the output)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Backspace String Compare (LC 844): same idea with '#' as the star, then compare
 *   - Remove All Adjacent Duplicates (LC 1047): pop when the top equals the new char
 *   - What if a star may have no letter to its left? Guard the pop with top > 0
 *
 * RUN
 *   main() runs 3 cases (typical, all erased, no stars) and prints actual vs expected.
 */
class RemovingStarsFromAString {

    /** Author's approach: the char[] itself is the stack, 'top' is its size. */
    public static String removeStars(String s) {
        char[] chars = s.toCharArray();
        int top = 0; // next free slot == number of surviving letters
        for (int right = 0; right < chars.length; right++) {
            if (chars[right] == '*') {
                top--;                       // pop: forget the last surviving letter
            } else {
                chars[top++] = chars[right]; // push: keep this letter
            }
        }
        return new String(chars, 0, top);
    }

    /** Same algorithm with an explicit StringBuilder as the stack. */
    public static String removeStarsWithStringBuilder(String s) {
        StringBuilder stack = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c == '*') {
                stack.setLength(stack.length() - 1); // pop
            } else {
                stack.append(c);                     // push
            }
        }
        return stack.toString();
    }

    public static void main(String[] args) {
        String[] inputs   = {"leet**cod*e", "erase*****", "abc"};
        String[] expected = {"lecoe",       "",           "abc"};

        for (int i = 0; i < inputs.length; i++) {
            System.out.println("case " + (i + 1) + " in-place:       \"" + removeStars(inputs[i])
                    + "\"   expected \"" + expected[i] + "\"");
            System.out.println("case " + (i + 1) + " StringBuilder:  \""
                    + removeStarsWithStringBuilder(inputs[i])
                    + "\"   expected \"" + expected[i] + "\"");
        }
    }
}
