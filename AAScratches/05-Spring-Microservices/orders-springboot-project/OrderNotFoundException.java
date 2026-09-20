/*
 * =====================================================================
 *  OrderNotFoundException                     Spring project file (orders-springboot-project)
 * =====================================================================
 *
 * ROLE IN THE PROJECT
 *   The domain exception for "you asked for an order that does not exist".
 *   OrderController (or the service behind it) throws this when a lookup by id
 *   returns nothing. GlobalExceptionHandler catches this exact type and turns it
 *   into a 404 ErrorResponse, so the controller never has to build an HTTP
 *   status itself - it just throws a business-meaningful exception.
 *
 *   Package: com.target.orders.exception.exceptions
 *   Thrown by:  order lookup paths in OrderController
 *   Handled by: GlobalExceptionHandler @ExceptionHandler(OrderNotFoundException.class)
 *
 * WHAT TO NOTICE
 *   - It extends RuntimeException, not Exception. That is deliberate: an
 *     unchecked exception does not force every caller into try/catch, which is
 *     what lets the controller method stay clean and the handler do the work.
 *   - It carries only a message and forwards it with super(message). The message
 *     is what ends up in the ErrorResponse body, so it should name the id that
 *     was missing, not just say "not found".
 *   - There is no @ResponseStatus annotation here. The mapping to 404 lives in
 *     GlobalExceptionHandler instead. Both styles work; centralising it in the
 *     handler keeps the exception free of web concerns and makes the status
 *     visible in one place.
 *   - A custom exception type (rather than reusing RuntimeException) is the
 *     whole point: the handler can dispatch on the type. That is the pattern
 *     worth carrying into an interview answer about Spring error handling.
 *   - The main() at the bottom is a leftover from the old scratch format. It is
 *     not part of the Spring app and nothing in the project calls it.
 *
 * INTERVIEW FOLLOW-UPS
 *   - @ResponseStatus on the exception vs @ExceptionHandler in a @ControllerAdvice:
 *     when would you pick each? (Annotation for simple fixed mappings; advice
 *     when you need to shape the body, log, or map several exceptions.)
 *   - Checked vs unchecked for domain errors, and why Spring's own DataAccess
 *     exceptions are all unchecked.
 *   - How would you add an error code or the missing id as a typed field rather
 *     than embedding it in the message string?
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
