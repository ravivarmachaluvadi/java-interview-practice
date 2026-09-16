/**
 * Represents a data transfer object for an order.
 *
 * Problem: Validates and encapsulates the core fields of an order (id, customer name,
 * amount, and optional discount) to be used in service layers or API endpoints.
 *
 * Approach: Uses Lombok's @Data to generate boilerplate code and Jakarta Bean Validation
 * annotations (@NotBlank, @NotNull, @Positive) to enforce constraints on incoming data.
 *
 * Time Complexity: O(1) – validation occurs once per field during object creation.
 * Space Complexity: O(1) – only a fixed number of fields are stored in the DTO instance.
 */
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


    public static void main(String[] args) {
        // Create a concrete example of OrderDTO
        OrderDTO order = new OrderDTO();
        order.setId(101);
        order.setCustomerName("Alice Smith");
        order.setAmount(250.0);
        order.setDiscount(15.0);

        // Print the input object
        System.out.println("Input OrderDTO:");
        System.out.println(order);

        // For demonstration, let's say we want to calculate final amount after discount
        double finalAmount = order.getAmount() - (order.getDiscount() != null ? order.getDiscount() : 0);
    
        // Print the output result
        System.out.println("\nOutput: Final amount after discount:");
        System.out.printf("%.2f%n", finalAmount);
    }
}
