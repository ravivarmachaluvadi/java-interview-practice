import java.util.*;

class MinimumMultiplications {

    public int minimumMultiplications(int[] arr,
                                      int start, int end) {
        if (start == end) return 0;

        int n = arr.length;
        int mod = 100_000;

        int[] minSteps = new int[mod];
        Arrays.fill(minSteps, Integer.MAX_VALUE);
        Queue<int[]> q = new LinkedList<>();
        minSteps[start] = 0;
        q.add(new int[]{0, start});

        while (!q.isEmpty()) {
            int[] p = q.poll();
            int steps = p[0];
            int val = p[1];
            for (int i = 0; i < n; i++) {
                // first statement is multiplication
                int num = (val * arr[i]) % mod;
                if (num == end) return steps + 1;
                // left side steps , right side mulValue
                if (steps + 1 < minSteps[num]) {
                    minSteps[num] = steps + 1;
                    q.add(new int[]{steps + 1, num});
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int start = 3, end = 30;
        int[] arr = {2, 5, 7};
        MinimumMultiplications sol = new MinimumMultiplications();

        int ans = sol.minimumMultiplications(arr, start, end);

        System.out.println("The minimum multiplications to reach end is: " + ans);
    }
}
