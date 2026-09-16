/**
 * Parses JSON strings representing different response types (Customer, Address, Order) and prints a specific field from each.
 *
 * The program defines generic Gson TypeTokens for each concrete Response<T> type, then inspects the input JSON to determine which
 * type it contains. It deserializes using the appropriate TypeToken and outputs either the customer's name, address street,
 * or order amount.
 *
 * Time Complexity: O(n) per JSON string, where n is the length of the string (Gson parsing).
 * Space Complexity: O(m) for the parsed object graph, with m proportional to the size of the JSON payload.
 */
package casting;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

class GenericTypeDynamically {
    private static final Gson gson = new Gson();

    private static final Type CUSTOMER_RESPONSE_TYPE = new TypeToken<Response<Customer>>() {
    }.getType();
    private static final Type ADDRESS_RESPONSE_TYPE = new TypeToken<Response<Address>>() {
    }.getType();
    private static final Type ORDER_RESPONSE_TYPE = new TypeToken<Response<Order>>() {
    }.getType();

    public static void main(String[] args) {
        String customerJson = """
                {
                  "status": "status_9b474e88f86a",
                  "message": "message_cad611b3fc84",
                  "payloadType": "customer",
                  "payload": {
                               "name": "John Doe",
                               "age": 30
                             }
                }""";

        String addressJson = """
                {
                  "status": "status_9b474e88f86a",
                  "message": "message_cad611b3fc84",
                  "payloadType": "address",
                  "payload": {
                               "street": "123 Main St",
                               "city": "Anytown"
                             }
                }""";

        String orderJson = """
                {
                  "status": "status_9b474e88f86a",
                  "message": "message_cad611b3fc84",
                  "payloadType": "order",
                  "payload": {
                               "orderId": "orderId_80197cedda25",
                               "amount": 0.00
                             }
                }""";

        processJson(customerJson);
        processJson(addressJson);
        processJson(orderJson);
    }

    public static void processJson(String json) {
        try {
            if (json.contains("customer")) {
                Response<Customer> response = gson.fromJson(json, CUSTOMER_RESPONSE_TYPE);
                System.out.println(response.payload.name);
            } else if (json.contains("address")) {
                Response<Address> response = gson.fromJson(json, ADDRESS_RESPONSE_TYPE);
                System.out.println(response.payload.street);
            } else if (json.contains("order")) {
                Response<Order> response = gson.fromJson(json, ORDER_RESPONSE_TYPE);
                System.out.println(response.payload.amount);
            }
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Failed to parse JSON", e);
        }
    }
}

class Address {
    public String street;
    public String city;
}

class Order {
    public String orderId;
    public double amount;
}

class Customer {
    public String name;
    public int age;
}

class Response<T> {
    public String status;
    public String message;
    public String payloadType;
    public T payload;

}
