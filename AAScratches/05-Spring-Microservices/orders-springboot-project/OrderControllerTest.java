/**
 * Tests the OrderController REST endpoints for creating and retrieving orders.
 *
 * The test verifies that a POST to /v1/api/orders correctly persists an OrderDTO
 * and returns the same data, while a GET to /v1/api/orders/{id} with a non‑existent ID
 * results in a 4xx client error response.
 *
 * Approach: Use Spring's MockMvc to simulate HTTP requests, serialize/deserialize JSON
 * via Jackson ObjectMapper, and assert returned values or status codes.
 *
 * Time Complexity: O(1) per test case (fixed number of operations).
 * Space Complexity: O(1) additional space beyond the request/response payloads.
 */
package com.target.orders.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.target.orders.dto.OrderDTO;
import com.target.orders.exception.exceptions.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void saveOrder() throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setId(1);
        dto.setAmount(250.5);
        dto.setCustomerName("Ravi");
        dto.setDiscount(0.1);

        String content = mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/v1/api/orders")
                                .content(mapper.writeValueAsString(dto))
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        OrderDTO orderDTO = mapper.readValue(content, OrderDTO.class);

        assertEquals(1, orderDTO.getId());
        assertEquals(250.5, orderDTO.getAmount());
        assertEquals(0.1, orderDTO.getDiscount());
        assertEquals("Ravi", orderDTO.getCustomerName());
    }

    @Test

    void getOrder() throws Exception {

        String contentAsString = mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/v1/api/orders/2")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();

    }

    public static void main(String[] args) throws Exception {
        // Build a small concrete example input
        com.target.orders.dto.OrderDTO dto = new com.target.orders.dto.OrderDTO();
        dto.setId(1);
        dto.setAmount(250.5);
        dto.setCustomerName("Ravi");
        dto.setDiscount(0.1);

        // Convert to JSON string using ObjectMapper
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        String jsonInput = mapper.writeValueAsString(dto);

        // Print the input with a label
        System.out.println("Input OrderDTO as JSON:");
        System.out.println(jsonInput);
        System.out.println();

        // Simulate the controller's saveOrder behavior by converting back to DTO
        com.target.orders.dto.OrderDTO outputDto = mapper.readValue(jsonInput, com.target.orders.dto.OrderDTO.class);

        // Print the output with a label
        System.out.println("Output OrderDTO after round-trip:");
        System.out.println("ID: " + outputDto.getId());
        System.out.println("Amount: " + outputDto.getAmount());
        System.out.println("Customer Name: " + outputDto.getCustomerName());
        System.out.println("Discount: " + outputDto.getDiscount());
    }
}