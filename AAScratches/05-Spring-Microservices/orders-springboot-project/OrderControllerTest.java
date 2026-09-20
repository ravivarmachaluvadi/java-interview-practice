/*
 * =====================================================================
 *  OrderControllerTest -- MockMvc slice of the API   Spring Boot: orders service
 * =====================================================================
 *
 * ROLE IN THE PROJECT
 *   The only test in the project. It proves the two endpoints behave as advertised
 *   without ever opening a real socket: MockMvc drives Spring's DispatcherServlet
 *   directly, so routing, JSON binding, validation and @ControllerAdvice all run,
 *   but no Tomcat connector is started and no port is bound.
 *
 *   Two cases:
 *     saveOrder() -- POST a full OrderDTO, read the response body back into an
 *        OrderDTO, and assert all four fields survived the round trip. This is really
 *        a serialisation test: it catches a renamed JSON property or a missing getter.
 *     getOrder()  -- GET an id that was never stored and assert a 4xx. That is the
 *        OrderNotFoundException -> GlobalExceptionHandler -> 404 path, end to end.
 *
 *   @SpringBootTest boots the whole application context; @AutoConfigureMockMvc adds the
 *   configured MockMvc bean to it. Together they give a full-context test with a fake
 *   transport, which is the middle ground between a plain unit test and a live server.
 *
 * WHAT TO NOTICE
 *   - The two tests share OrderController's static map, so they are coupled through
 *     global state. getOrder() passes only because nothing ever stores id 2. Change
 *     saveOrder() to use id 2 and the other test fails for reasons unrelated to itself.
 *     JUnit 5 gives no order guarantee, so this is a latent flake, not a safe shortcut.
 *   - getOrder() captures contentAsString and never asserts on it. Either assert the
 *     ErrorResponse body (status, message, path) or drop the variable -- an unused
 *     capture reads like a forgotten assertion.
 *   - is4xxClientError() is weaker than the code deserves. The handler promises exactly
 *     404; isNotFound() would catch a regression that turned it into a 400.
 *   - ObjectMapper is built by hand instead of being @Autowired. The injected one is the
 *     Boot-configured instance (modules, naming strategy, date format), so a hand-rolled
 *     mapper can serialise differently from the server it is testing.
 *   - There is no test for the 400 validation path, which is the most interesting branch
 *     in the whole project: POST a body with a blank customerName or a negative amount
 *     and assert the per-field "customerName : ..." line comes back.
 *   - @SpringBootTest is heavy. @WebMvcTest(OrderController.class) loads only the web
 *     layer and is the right slice when there is nothing below the controller to wire.
 *   - The ErrorResponse import is unused; nothing here deserialises the error body.
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
