import java.util.Arrays;

// https://leetcode.com/problems/maximum-population-year/description/
// 1854. Maximum Population Year
class MaximumPopulationYear {

    // it seems like variation of minimum platforms required
    // it also has different solution in below
    public static int maximumPopulationV2(int[][] logs) {
        // Extract birth and death years into separate arrays
        int[] births = new int[logs.length];
        int[] deaths = new int[logs.length];

        for (int i = 0; i < logs.length; i++) {
            births[i] = logs[i][0];
            deaths[i] = logs[i][1];
        }

        // Sort both arrays
        Arrays.sort(births);
        Arrays.sort(deaths);

        // Process intervals
        int maxPopulation = 0;
        int currentPopulation = 0;
        int maxYear = births[0];
        // birth , death pointers
        int i = 0, j = 0;

        while (i < logs.length) {
            // If a birth year comes before or is the
            // same as a death year, increase population
            if (births[i] < deaths[j]) {
                currentPopulation++;
                if (currentPopulation > maxPopulation) {
                    maxPopulation = currentPopulation;
                    maxYear = births[i];
                }
                i++;
            } else {
                // Otherwise, decrease population (a person has died)
                currentPopulation--;
                j++;
            }
        }

        return maxYear;
    }


    /**
     * Constraints:
     * <p>
     * 1 <= logs.length <= 100
     * 1950 <= birthi < deathi <= 2050
     */
    // create a time-line increment births and decrement deaths
    // sum population find a year with max population
    // 1950-1950 = 0 , can be used as pivot
    // also another variation of max prefixSum point in array with -ve values possible
    public static int maximumPopulation(int[][] logs) {

        // consider as timeline
        // population == timeLine
        int[] population = new int[101];

        for (int[] log : logs) {
            int birth = log[0] - 1950;
            int death = log[1] - 1950;
            // updaing log of births and deaths with increments and decrements
            population[birth]++;
            population[death]--;
        }
        int maxPopulation = 0;
        int year = 0;
        int currentPopulation = 0;
        for (int i = 0; i < population.length; i++) {
            currentPopulation += population[i];
            if (currentPopulation > maxPopulation) {
                maxPopulation = currentPopulation;
                year = i;
            }
        }
        return year + 1950;
    }


    public static void main(String[] args) {

        int[][] logs1 = {
                {1993, 1999},
                {2000, 2010}
        };
        System.out.println(maximumPopulation(logs1));

        int[][] logs2 = {
                {1950, 1961},
                {1960, 1971},
                {1970, 1981}
        };
        System.out.println(maximumPopulation(logs2));

        int[][] logs3 = {
                {2005, 2020},
                {2000, 2015},
                {2010, 2015}
        };
        System.out.println(maximumPopulation(logs3));
    }
}
