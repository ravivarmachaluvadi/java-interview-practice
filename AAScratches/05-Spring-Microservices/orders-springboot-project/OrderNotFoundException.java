/**
 * Problem: Provides a specific unchecked exception to signal that an
 * Order entity could not be located in the system.
 *
 * Approach: Extends RuntimeException and forwards a descriptive message
 * to its superclass, allowing callers to throw or catch this type
 * without mandatory try/catch blocks.
 *
 * Time Complexity: O(1) – construction is constant time.
 * Space Complexity: O(1) – only stores the exception object reference.
 */
package com.target.orders.exception.exceptions;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }

    public static void main(String[] args) {
        String inputMessage = "Order with ID 12345 not found.";
        System.out.println("Input: " + inputMessage);
        OrderNotFoundException ex = new OrderNotFoundException(inputMessage);
        System.out.println("Output: Exception message -> " + ex.getMessage());
    }
}
