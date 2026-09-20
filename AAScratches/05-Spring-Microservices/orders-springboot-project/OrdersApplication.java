/*
 * =====================================================================
 *  OrdersApplication                          Spring project file (orders-springboot-project)
 * =====================================================================
 *
 * ROLE IN THE PROJECT
 *   The entry point of the Orders microservice. Running its main() starts the
 *   embedded web server and builds the Spring application context: the
 *   controller (OrderController), the @RestControllerAdvice
 *   (GlobalExceptionHandler) and everything else in or below the package
 *   com.target.orders is discovered from here.
 *
 *   This is the only class in the project with a real main(). Every other file
 *   in this folder is a bean, a DTO, an exception or a test that this context
 *   wires together.
 *
 * WHAT TO NOTICE
 *   - @SpringBootApplication is three annotations in one: @Configuration
 *     (this class can declare beans), @EnableAutoConfiguration (Spring Boot
 *     configures the web server, Jackson, validation and so on from what is on
 *     the classpath) and @ComponentScan (scan this package and everything under
 *     it). That is the single most asked Spring Boot question.
 *   - The scan root is the package of this class, com.target.orders. Any
 *     @Component/@RestController placed outside that subtree is invisible - the
 *     classic "my controller returns 404" bug. Note the exception classes live
 *     in com.target.orders.exception.exceptions, which is inside the root, so
 *     they are picked up.
 *   - SpringApplication.run(...) returns the ConfigurableApplicationContext.
 *     Capturing it is useful in tests or when you want to inspect beans; here
 *     the return value is deliberately ignored.
 *   - @Slf4j (Lombok) generates a private static final Logger named "log". No
 *     logging is done in this class today, so the annotation is decorative.
 *   - The imports of HttpHeaders, HttpStatus, ResponseEntity,
 *     MethodArgumentNotValidException and WebRequest are unused leftovers from
 *     when exception handling lived here before it moved to
 *     GlobalExceptionHandler. They are kept so the file matches the original
 *     project, but in review they would be deleted.
 *
 * INTERVIEW FOLLOW-UPS
 *   - What exactly does auto-configuration do, and how do you switch one piece
 *     of it off? (@SpringBootApplication(exclude = ...), or a matching bean of
 *     your own beating the @ConditionalOnMissingBean.)
 *   - How do you run this as a non-web application, or pick the port and
 *     profile at startup?
 *   - Why does the main class normally sit in the top-level package, and what
 *     breaks when it does not?
 *   - CommandLineRunner vs ApplicationRunner: where would startup work go?
 */
package com.target.orders;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

@SpringBootApplication
@Slf4j
public class OrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersApplication.class, args);
	}

}
