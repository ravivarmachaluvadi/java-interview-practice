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
}
