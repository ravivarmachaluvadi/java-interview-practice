/**
 * Represents a standardized error response for REST API endpoints.
 *
 * <p>Problem: When an exception occurs in the application, we need to return a consistent
 * JSON payload containing HTTP status, user‑friendly message, detailed errors and request path.
 *
 * <p>Approach: This immutable data transfer object (DTO) holds all relevant error information
 * and is constructed once per exception. It can be serialized by Spring MVC into the response body.
 *
 * <p>Time Complexity: O(1) – construction simply assigns fields.
 * <p>Space Complexity: O(n) – proportional to the number of error strings stored in {@code errors}.
 */
package com.target.orders.exception.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String message;
    private List<String> errors;
    private String path;

    public ErrorResponse(int status, String message, List<String> errors, String path) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.path = path;
    }

    public static void main(String[] args) {
        // Build a concrete example input
        int status = 404;
        String message = "Resource not found";
        java.util.List<String> errors = java.util.List.of("Missing ID", "Invalid format");
        String path = "/api/orders/123";

        // Create the ErrorResponse instance using the exposed constructor
        com.target.orders.exception.exceptions.ErrorResponse errorResponse =
                new com.target.orders.exception.exceptions.ErrorResponse(status, message, errors, path);

        // Print both input and output with labels
        System.out.println("Input:");
        System.out.println("  status: " + status);
        System.out.println("  message: " + message);
        System.out.println("  errors: " + errors);
        System.out.println("  path: " + path);

        System.out.println("\nOutput (ErrorResponse):");
        System.out.println("  status: " + errorResponse.getStatus());
        System.out.println("  message: " + errorResponse.getMessage());
        System.out.println("  errors: " + errorResponse.getErrors());
        System.out.println("  path: " + errorResponse.getPath());
    }
}
