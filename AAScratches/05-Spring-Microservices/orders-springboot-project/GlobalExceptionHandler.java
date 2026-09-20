/*
 * =====================================================================
 *  GlobalExceptionHandler -- one place for every failure   Spring Boot: orders service
 * =====================================================================
 *
 * ROLE IN THE PROJECT
 *   This is the single translation layer between "something went wrong somewhere in the
 *   app" and "an HTTP response the client can read". Controllers stay free of try/catch:
 *   they throw, and @ControllerAdvice catches the throw anywhere in the dispatch chain
 *   and converts it into an ErrorResponse body plus the right status code.
 *
 *   Three failures are mapped, from most specific to least:
 *     MethodArgumentNotValidException -> 400, with one "field : message" line per
 *        violation. Raised by Spring when @Valid rejects an incoming @RequestBody.
 *     OrderNotFoundException          -> 404, thrown by OrderController.getOrder.
 *     Exception                       -> 500, the catch-all so nothing escapes as a
 *        raw Spring whitelabel page.
 *
 *   Extending ResponseEntityExceptionHandler is what gives the class the ready-made
 *   hooks for Spring MVC's own exceptions; the class only overrides the one it cares
 *   about and inherits sensible defaults for the rest.
 *
 * WHAT TO NOTICE
 *   - handleMethodArgumentNotValid has no @Override annotation. It does still override
 *     the parent method, but only because the four-parameter signature happens to match
 *     Spring 6's exactly (note HttpStatusCode, not HttpStatus -- that type changed in
 *     Spring 6 and is a classic upgrade break). Drop the annotation and a one-character
 *     signature drift silently turns an override into a dead private-looking method.
 *   - Two ways of getting the path appear in the same class: WebRequest.getDescription
 *     (false) with "uri=" stripped off by hand for the inherited hook, and
 *     HttpServletRequest.getRequestURI() for the @ExceptionHandler methods. The string
 *     surgery is the ugly one, and it exists only because the parent hook hands you a
 *     WebRequest rather than the servlet request.
 *   - The 500 branch puts exception.getMessage() straight into the response body and,
 *     unlike the other two, logs nothing. That is backwards on both counts: internal
 *     messages leak to the caller and the operator gets no trace.
 *   - Specificity wins, not declaration order. @ExceptionHandler(Exception.class) does
 *     not swallow OrderNotFoundException; Spring picks the closest supertype match.
 *   - Return types are ResponseEntity<?>, which tells a reader nothing. ResponseEntity
 *     <ErrorResponse> documents the contract and is what a reviewer will ask for.
 *   - @Slf4j is Lombok generating the private static final Logger field named "log".
 */
package com.target.orders.exception.handler;

import com.target.orders.exception.exceptions.ErrorResponse;
import com.target.orders.exception.exceptions.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {


    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {

        List<String> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
                .toList();
        String path = request.getDescription(false).replace("uri=", "");

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                validationErrors,
                path);
        log.error("Validation failed at {} -> {}", path, validationErrors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<?> handleOrderNotFoundException(OrderNotFoundException ex, HttpServletRequest request) {
        String path = request.getRequestURI();

        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                List.of(),
                path);

        log.error("Resource not found at {} -> {}", path, ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleServerError(Exception exception, HttpServletRequest request) {
        String path = request.getRequestURI();
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                exception.getMessage(), List.of(), path);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
