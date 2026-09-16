package com.target.orders.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderDTO {

    Integer id;

    @NotBlank(message = "customer name should be there")
    String customerName;

    @NotNull(message = "Amount can not be null")
    @Positive(message = "amount can't be negative")
    Double amount;

    Double discount;

}
