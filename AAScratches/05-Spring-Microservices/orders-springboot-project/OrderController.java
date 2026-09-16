/**
 * Handles CRUD operations for orders via a REST API.
 *
 * <p>Problem: Persist and retrieve {@link com.target.orders.dto.OrderDTO} objects by ID,
 * returning appropriate HTTP responses and throwing an exception when an order is not found.</p>
 *
 * <p>Approach: Uses an in-memory {@code Map<Integer, OrderDTO>} to store orders.
 * POST requests add or overwrite entries; GET requests fetch by key and wrap the result
 * in a {@link org.springframework.http.ResponseEntity}. If the ID does not exist,
 * an {@link com.target.orders.exception.exceptions.OrderNotFoundException} is thrown.</p>
 *
 * <p>Time Complexity: O(1) for both saveOrder and getOrder due to hash map operations.</p>
 *
 * <p>Space Complexity: O(n), where n is the number of stored orders, as each entry
 * occupies a constant amount of space in the map.</p>
 */
package com.target.orders.controller;

import com.target.orders.dto.OrderDTO;
import com.target.orders.exception.exceptions.OrderNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@Validated
@RequestMapping("/v1/api/orders")
public class OrderController {

    private static final Map<Integer, OrderDTO> map = new HashMap<>();

    @PostMapping
    public ResponseEntity<?> saveOrder(@Valid @RequestBody OrderDTO order) {
        map.put(order.getId(), order);
        return ResponseEntity.ok(order);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Integer id,
                                      @RequestParam(required = false) String val) {
        OrderDTO orderDTO = map.get(id);

        OrderDTO dto = Optional.ofNullable(orderDTO).orElseThrow(() ->
                new OrderNotFoundException("order not found with id " + id));

        return ResponseEntity.ok(dto);
    }

    public static void main(String[] args) {
        // Create controller instance
        OrderController controller = new OrderController();

        // Build a simple OrderDTO (assuming it has a no‑arg constructor and setId)
        com.target.orders.dto.OrderDTO order = new com.target.orders.dto.OrderDTO();
        order.setId(1);
        order.setDescription("Sample order");

        // Call the primary method to save the order
        ResponseEntity<?> saveResponse = controller.saveOrder(order);

        // Retrieve the same order by id
        ResponseEntity<?> getResponse = controller.getOrder(1, null);

        // Print input and outputs
        System.out.println("Input OrderDTO: " + order);
        System.out.println("Save response body: " + ((ResponseEntity<?>) saveResponse).getBody());
        System.out.println("Get response body: " + ((ResponseEntity<?>) getResponse).getBody());
    }
}
