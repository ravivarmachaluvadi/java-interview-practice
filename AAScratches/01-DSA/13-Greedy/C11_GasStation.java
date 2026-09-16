/**
 * There are n gas stations along a circular route, where the amount of gas at the ith station is gas[i].
 * <p>
 * You have a car with an unlimited gas tank and it costs cost[i] of gas to travel from the ith station
 * <p>
 * to its next (i + 1)th station. You begin the journey with an empty tank at one of the gas stations.
 * <p>
 * Given two integer arrays gas and cost, return the starting gas station's index if you can travel
 * <p>
 * around the circuit once in the clockwise direction, otherwise return -1.
 * <p>
 * If there exists a solution, it is guaranteed to be unique.
 */
//https://leetcode.com/problems/gas-station/description/
class GasStation {
    public static int canCompleteCircuit(int[] gas, int[] cost) {
        int totalGas = 0;
        int totalCost = 0;
        int fuelLeftInTank = 0;
        int startingIndex = 0;

        for (int i = 0; i < gas.length; i++) {
            totalGas += gas[i];
            totalCost += cost[i];
            fuelLeftInTank += gas[i] - cost[i];
            // If tank is negative, reset start and tank
            if (fuelLeftInTank < 0) {
                startingIndex = i + 1;
                fuelLeftInTank = 0;
            }
        }
        return totalGas >= totalCost ? startingIndex : -1;
    }

    public static void main(String[] args) {
        System.out.println(canCompleteCircuit(new int[]{1, 2, 3, 4, 5}
                , new int[]{3, 4, 5, 1, 2})); // 3

        System.out.println(canCompleteCircuit(new int[]{2, 3, 4}, new int[]{3, 4, 3})); // -1
    }
}
