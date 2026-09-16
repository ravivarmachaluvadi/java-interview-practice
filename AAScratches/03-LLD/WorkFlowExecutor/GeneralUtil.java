package com.tgt.gom.util;

public class GeneralUtil {

    /**
     * Generate a random number based on upper & lower bounds
     *
     * @param upperBound max value for the random number
     * @param lowerBound min value for the random number
     * @return random number
     */
    public static String generateRandomNumber(int upperBound, int lowerBound) {
        return String.valueOf((int) (Math.random() * ((upperBound - lowerBound) + 1)) + lowerBound);
    }
}
