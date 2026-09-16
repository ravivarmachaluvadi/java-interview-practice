// input -> [2, 1, 5, 4, 3, 0, 0]
// ans -> [2, 3, 0, 0, 1, 4, 5]
public static List<Integer> nextGreaterPermutation(List<Integer> A) {
    int n = A.size(); // size of the array.

    // identify adjacent pair such that arr[ind]<arr[ind+1]
    int ind = -1;
    for (int i = n - 2; i >= 0; i--) {
        if (A.get(i) < A.get(i + 1)) {
            ind = i;
            break;
        }
    }

    // if no such pair found then reverse whole array
    if (ind == -1) {
        // reverse the whole array:
        Collections.reverse(A);
        return A;
    }

    // now swap arr[ind] with an element while first larger value
    // than arr[ind] from right to the element it self
    for (int i = n - 1; i > ind; i--) {
        if (A.get(i) > A.get(ind)) {
            int tmp = A.get(i);
            A.set(i, A.get(ind));
            A.set(ind, tmp);
            break;
        }
    }

    // Step 3: reverse the right half from ind+1 to end
    List<Integer> sublist = A.subList(ind + 1, n);
    Collections.reverse(sublist);
    return A;
}

void main() {
    List<Integer> A = Arrays.asList(2, 1, 5, 4, 3, 0, 0);
    List<Integer> ans = nextGreaterPermutation(A);
    IO.println(ans); // [2, 3, 0, 0, 1, 4, 5]
}
