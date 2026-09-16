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
}