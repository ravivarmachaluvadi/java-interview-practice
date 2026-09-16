/**
 * Starts the Orders microservice.
 *
 * This class bootstraps a Spring Boot application that exposes order-related
 * REST endpoints (defined elsewhere in the project). It configures logging via
 * Lombok's @Slf4j and launches the embedded servlet container when executed.
 *
 * Approach:
 * 1. Annotate with @SpringBootApplication to enable component scanning,
 *    auto-configuration, and property support.
 * 2. Use SpringApplication.run in main() to launch the application context.
 *
 * Time Complexity: O(1) – launching is a constant‑time operation relative
 * to code execution (actual startup time depends on bean initialization).
 * Space Complexity: O(1) – no additional data structures are created beyond
 * the framework’s internal containers.
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
