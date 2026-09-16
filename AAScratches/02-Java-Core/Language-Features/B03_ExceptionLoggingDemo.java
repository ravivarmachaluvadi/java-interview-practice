/**
 * Demonstrates exception logging using Lombok's @Slf4j.
 *
 * The program intentionally throws a RuntimeException in {@code longName()},
 * catches it, and logs the error twice: once with a simple message
 * concatenation and once using SLF4J's parameterized logging.
 *
 * Approach:
 * 1. Throw an exception to simulate an error scenario.
 * 2. Catch the exception and log its details via Lombok-generated logger.
 * 3. Show both basic string concatenation and parameterized logging styles.
 *
 * Time Complexity: O(1) – a fixed number of operations regardless of input size.
 * Space Complexity: O(1) – only constant auxiliary space is used for local variables.
 */
package com.example.learnlombok.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ExceptionLoggingDemo {
    public static void main(String[] args) {
        m1();
    }

    private static void m1() {
        m2();
    }

    private static void m2() {
        longName();
    }

    private static void longName() {
        try {
            throw new RuntimeException("some exception");

        } catch (Exception e) {
            // 23:20:07.566 [main] ERROR com.example.learnlombok.exception.Scratch -- error : java.lang.RuntimeException: some exception
            log.error("error : " + e);
            System.out.println();
            //23:20:07.568 [main] ERROR com.example.learnlombok.exception.Scratch -- error
            //java.lang.RuntimeException: some exception
            //	at com.example.learnlombok.exception.Scratch.longName(Scratch.java:24)
            //	at com.example.learnlombok.exception.Scratch.m2(Scratch.java:18)
            //	at com.example.learnlombok.exception.Scratch.m1(Scratch.java:14)
            //	at com.example.learnlombok.exception.Scratch.main(Scratch.java:9)
            log.error("error {}", "123", e);
        }
    }
}
