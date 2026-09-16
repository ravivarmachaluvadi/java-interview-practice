class PairsOfSongsDivisibleBy60 {
    public static int numPairsDivisibleBy60(int[] time) {
        // Array to store frequencies of remainders
        int[] remainders = new int[60];
        int count = 0;
        // Iterate through each song's duration
        for (int t : time) {
            int remainder = t % 60;
            // Complement remainder to make sum divisible by 60
            int complement = (60 - remainder) % 60;

            // If the complement has been seen before, add the number of such pairs
            count += remainders[complement];

            // Increment the frequency of the current remainder
            remainders[remainder]++;
        }

        return count;
    }

    public static void main(String[] args) {
        int[] time = {30, 20, 150, 100, 40}; // Example input
        int result = numPairsDivisibleBy60(time);

        System.out.println("Number of pairs divisible by 60: " + result);
    }
}
