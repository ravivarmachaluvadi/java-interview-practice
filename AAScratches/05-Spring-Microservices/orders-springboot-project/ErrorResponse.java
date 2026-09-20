/*
 * =====================================================================
 *  ErrorResponse -- the error payload DTO          Spring Boot: orders service
 * =====================================================================
 *
 * ROLE IN THE PROJECT
 *   Every failure this service returns is serialised from this one class, so a client
 *   parses the same four fields whether the cause was a validation failure, a missing
 *   order, or an unexpected crash. It is the "error contract" of the API.
 *
 *   Who builds it: GlobalExceptionHandler, and only GlobalExceptionHandler. Controllers
 *   never construct it; they throw, and the advice turns the throw into this object.
 *
 *   The four fields and why each earns its place:
 *     status   - the numeric HTTP status, duplicated in the body so clients that only
 *                log the body still know what happened.
 *     message  - one human sentence ("Validation failed", "order not found with id 2").
 *     errors   - the per-field detail list; empty for failures with no field breakdown.
 *     path     - the request URI, so a log line identifies the call without correlation.
 *
 * WHAT TO NOTICE
 *   - Lombok @Getter/@Setter generate the accessors. Jackson needs the getters to
 *     serialise; the setters exist only so tests can deserialise the JSON back.
 *   - There is no no-arg constructor. Jackson can still write this object out, but it
 *     cannot read one back in without @JsonCreator or a default constructor. A test that
 *     does mapper.readValue(body, ErrorResponse.class) will fail for exactly that reason.
 *   - The class advertises itself as immutable in the original comment yet ships setters
 *     and stores the caller's List by reference. Real immutability would mean final
 *     fields, no setters, and List.copyOf(errors) in the constructor.
 *   - The imported java.time.LocalDateTime is unused. A production error contract almost
 *     always carries a timestamp; this one declares the import and then forgets the field.
 *   - Interview angle: this is a hand-rolled version of RFC 7807 / Spring's ProblemDetail.
 *     Knowing that Spring 6 ships ProblemDetail out of the box is the expected follow-up.
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
                new com.target.orders.exception.exceptions.ErrorResponse(
                        status, message, errors, path);

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
