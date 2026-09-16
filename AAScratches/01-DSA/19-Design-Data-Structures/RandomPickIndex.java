import java.util.Random;

// LeetCode Link: https://leetcode.com/problems/random-pick-index/

class RandomPickIndex {

    public int pickRandomIndex(int[] arr) {
        Random rand = new Random();
        // Generate a random index between 0 and arr.length - 1
        return rand.nextInt(arr.length); // arr.length is exclusive
    }

    public static void main(String[] args) {
        RandomPickIndex solution = new RandomPickIndex();
        int[] arr = {10, 20, 30, 40, 50, 60};  // Example array

        // Pick a random index and print the value at that index
        int randomIndex = solution.pickRandomIndex(arr);
        System.out.println("Randomly picked index: " + randomIndex);
        System.out.println("Value at picked index: " + arr[randomIndex]);
    }
}
