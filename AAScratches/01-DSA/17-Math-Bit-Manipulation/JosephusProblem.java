/**
 * Problem Statement: There are n people standing in a circle waiting to be executed.
 * The counting out begins at some point in the circle and proceeds around the circle
 * in a fixed direction. In each step, a certain number of people are skipped and the
 * next person is executed. The elimination proceeds around the circle (which is becoming
 * smaller and smaller as the executed people are removed), until only the last person remains,
 * who is given freedom. Given the total number of person n and a number k which indicates
 * that k-1 persons are skipped and the kth person is killed in a circle. The task is to
 * choose the place in the initial circle so that you are the last one remaining and so survive.
 */
// https://takeuforward.org/data-structure/josephus-problem/
class JosephusProblem {

    // Recursive function to solve Josephus problem
    public static int josephus(int n, int k) {
        // Base case: if there is only one person, they survive
        if (n == 1) {
            return 0;  // Return 0-based index for 1 person
        } else {
            // Recursive step: Reduce the problem size by 1, adjust position by k
            return (josephus(n - 1, k) + k) % n;
        }
    }
    public static void main(String[] args) {
        int n = 7; // Number of people
        int k = 3; // Every k-th person is eliminated
        // Josephus function returns the index of the survivor (0-based index)
        int survivor = josephus(n, k);
        // If you want the result in 1-based index, add 1
        System.out.println("The survivor is at position: " + (survivor + 1));
    }
}
