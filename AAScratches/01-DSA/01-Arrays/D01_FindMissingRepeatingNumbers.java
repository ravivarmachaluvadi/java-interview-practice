/**
 * Property	Statement	Why it matters
 * <p>
 * Self-inverse	a ^ a = 0	The cancellation engine — duplicates vanish
 * <p>
 * Identity	a ^ 0 = a	Safe accumulator seed
 * <p>
 * Commutative	a ^ b = b ^ a	Order of traversal is irrelevant
 * <p>
 * Associative	(a ^ b) ^ c = a ^ (b ^ c)	Fold an array in one pass, no grouping needed
 * <p>
 * Involution	a ^ b ^ b = a	XOR is its own undo — encrypt/decrypt with one op
 * <p>
 * Complement	a ^ ~0 = ~a (i.e. a ^ -1)	Bitwise NOT via XOR
 * <p>
 * Bit semantics	Bit is 1 iff the inputs disagree there	The partitioning insight from your last problem
 *
 */

// int[] a = {3, 1, 2, 5, 4, 6, 7, 5};
public static int[] findMissingRepeatingNumbers(int[] a) {
    int n = a.length;
    int xr = 0;
    for (int i = 0; i < n; i++) {
        xr = xr ^ a[i];
        xr = xr ^ (i + 1);
    }
    //Step 2: Find the differentiating a bit number:
    // it's minus -xr not ~ tilde
    int number = (xr & -xr);
    //Step 3: Group the numbers:
    int zero = 0;
    int one = 0;
    System.out.println("Numbet " + number);
    for (int i = 0; i < n; i++) {
        //part of 1 group:
        if ((a[i] & number) != 0) one = one ^ a[i];
            //part of 0 group:
        else zero = zero ^ a[i];
    }
    for (int i = 1; i <= n; i++) {
        //part of 1 group:
        if ((i & number) != 0) one = one ^ i;
            //part of 0 group:
        else zero = zero ^ i;
    }
    // Last step: Identify the numbers:
    int cnt = 0;
    for (int i = 0; i < n; i++) if (a[i] == zero) cnt++;
    if (cnt == 2) return new int[]{zero, one};
    return new int[]{one, zero};
}

void main() {
    int[] a = {3, 1, 2, 5, 4, 6, 7, 5};
    int[] ans = findMissingRepeatingNumbers(a);
    IO.println("The repeating and missing numbers are: {"
            + ans[0] + ", " + ans[1] + "}");
}

