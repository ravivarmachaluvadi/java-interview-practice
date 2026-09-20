/*
 * =====================================================================
 *  OrderDTO -- the request/response contract     Spring Boot: orders service
 * =====================================================================
 *
 * ROLE IN THE PROJECT
 *   The shape of an order as it crosses the HTTP boundary, in both directions: Jackson
 *   deserialises the POST body into one of these, and serialises it back out on the
 *   200 response. It is also the map value inside OrderController, which is why this
 *   project has no separate entity class -- the DTO is doing both jobs.
 *
 *   Four fields: id, customerName, amount, discount.
 *
 *   It is also where input validation is declared, not enforced. The annotations are
 *   just metadata until @Valid on the controller parameter tells Spring to run them:
 *     @NotBlank on customerName - rejects null, "" and "   " (whitespace only).
 *     @NotNull + @Positive on amount - rejects a missing amount and any value <= 0.
 *   A violation never reaches the controller body. Spring raises
 *   MethodArgumentNotValidException, GlobalExceptionHandler catches it, and the client
 *   gets a 400 listing "customerName : customer name should be there".
 *
 * WHAT TO NOTICE
 *   - Know which annotation to reach for. @NotNull only rejects null; @NotEmpty rejects
 *     null and length 0; @NotBlank rejects null, empty and all-whitespace, and applies
 *     to CharSequence only. Putting @NotBlank on a Double does not compile.
 *   - id carries no constraint at all, yet OrderController uses it as the map key. A
 *     body with no id maps to key null and is silently stored. @NotNull belongs here.
 *   - discount is likewise unconstrained: negative, larger than amount, or 0.1 meaning
 *     "10%" vs "ten rupees" are all accepted. Unvalidated optional fields are where the
 *     real bugs hide, not the ones the annotations already cover.
 *   - Money as Double is wrong for anything that adds up; BigDecimal is the answer an
 *     interviewer is listening for, and this is the natural place to say so.
 *   - Fields are package-private, not private. Lombok's @Data still generates the
 *     getters, setters, equals, hashCode and toString, so nothing breaks -- but the
 *     encapsulation the getters imply is not actually there.
 *   - @Data on a mutable object generates equals/hashCode over every field. Put one of
 *     these in a HashSet, mutate it, and it is lost. Relevant here: the DTO doubles as
 *     the stored value.
 *   - The jakarta.validation NotEmpty import is unused. Note the jakarta.* package:
 *     Spring Boot 3 moved off javax.*, and stale javax imports are a common upgrade
 *     failure where the annotations compile but are simply never seen by the validator.
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
