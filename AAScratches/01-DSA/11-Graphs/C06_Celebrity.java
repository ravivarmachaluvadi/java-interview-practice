// // LeetCode Link: https://leetcode.com/problems/find-the-celebrity/
class Celebrity {

    // This method simulates the knows function from the problem
    public boolean knows(int a, int b) {
        // Implement the actual knows method based on the problem constraints.
        // For this example, it's assumed you have a matrix that tells who knows whom.
        return false; // Placeholder implementation
    }

    public int findCelebrity(int n) {
        int candidate = 0;

        // Step 1: Find a potential celebrity using two-pointer technique
        for (int i = 1; i < n; i++) {
            if (knows(candidate, i)) {
                candidate = i;  // candidate cannot be a celebrity if they know someone
            }
        }

        // Step 2: Verify if the candidate is indeed a celebrity
        for (int i = 0; i < n; i++) {
            // A celebrity is known by everyone and knows no one
            if (i != candidate && (knows(candidate, i) || !knows(i, candidate))) {
                return -1;  // No celebrity
            }
        }
        return candidate;  // If the candidate passed all checks
    }

    public static void main(String[] args) {
        Celebrity solution = new Celebrity();
        int n = 4; // Example with 4 people
        int celebrity = solution.findCelebrity(n);
        if (celebrity == -1) {
            System.out.println("No celebrity found.");
        } else {
            System.out.println("The celebrity is person " + celebrity);
        }
    }
}
