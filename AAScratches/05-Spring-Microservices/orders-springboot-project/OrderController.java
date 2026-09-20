/*
 * =====================================================================
 *  OrderController -- the REST entry point         Spring Boot: orders service
 * =====================================================================
 *
 * ROLE IN THE PROJECT
 *   The only class in this project a client ever talks to. It exposes two endpoints
 *   under /v1/api/orders and owns the (in-memory) store that backs them:
 *
 *     POST /v1/api/orders        body = OrderDTO   -> 200 with the saved order
 *     GET  /v1/api/orders/{id}                     -> 200 with the order, or 404
 *
 *   It is deliberately thin. There is no service layer and no repository: the map is
 *   the database. That keeps the file about the web layer -- routing, binding,
 *   validation, status codes -- which is what the rest of the project demonstrates.
 *
 *   How the three annotations split the work:
 *     @RestController  = @Controller + @ResponseBody, so returns are serialised to JSON.
 *     @RequestMapping  puts the shared /v1/api/orders prefix on every method.
 *     @Valid on the body triggers Bean Validation against OrderDTO's constraints; a
 *        violation becomes MethodArgumentNotValidException, which GlobalExceptionHandler
 *        turns into a 400. The controller never checks a field itself.
 *
 *   The 404 path is the same idea: getOrder throws OrderNotFoundException and lets the
 *   advice decide the status. No error handling lives here.
 *
 * WHAT TO NOTICE
 *   - The store is a static HashMap. Static means it is shared by every request and
 *     survives between tests; HashMap means it is not thread-safe, so two concurrent
 *     POSTs can corrupt it. ConcurrentHashMap (non-static, injected) is the fix, and
 *     "why is that map static" is a near-certain interview question.
 *   - @Validated on the class and @Valid on the parameter are different things. @Valid
 *     (Jakarta) cascades validation into the request body; @Validated (Spring) enables
 *     validation groups and method-level constraints on @PathVariable/@RequestParam.
 *     Here @Validated does nothing useful, because no parameter carries a constraint.
 *   - The optional @RequestParam "val" is declared and never read. Dead surface area on
 *     a public API is worse than dead code -- callers may start sending it.
 *   - Optional.ofNullable(map.get(id)).orElseThrow(...) is the idiomatic shape, but the
 *     intermediate orderDTO variable makes it read twice. One chained call is clearer.
 *   - saveOrder overwrites silently on a duplicate id and returns 200. A create should
 *     return 201 with a Location header, or 409 when the id already exists.
 *   - ResponseEntity<?> hides the payload type; ResponseEntity<OrderDTO> documents it.
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

        // Build a simple OrderDTO (assuming it has a no-arg constructor and setId)
        com.target.orders.dto.OrderDTO order = new com.target.orders.dto.OrderDTO();
        order.setId(1);
        order.setCustomerName("Sample order");

        // Call the primary method to save the order
        ResponseEntity<?> saveResponse = controller.saveOrder(order);

        // Retrieve the same order by id
        ResponseEntity<?> getResponse = controller.getOrder(1, null);

        // Print input and outputs
        System.out.println("Input OrderDTO: " + order);
        System.out.println("Save response body: " + saveResponse.getBody());
        System.out.println("Get response body: " + getResponse.getBody());
    }
}
