# Types of Software Testing

1. **Unit Testing** – Tests individual methods/classes in isolation.  
   *Example: JUnit test for `calculatePrice()`.*

2. **Integration Testing** – Tests interaction between multiple components.  
   *Example: Service → Repository → Database.*

3. **Functional Testing** – Verifies that functionality matches business requirements.  
   *Example: Login with valid/invalid credentials.*

4. **API Testing** – Tests REST APIs for request, response, status codes, validation, and errors.

5. **End-to-End (E2E) Testing** – Tests the complete business flow.  
   *Example: Login → Add Cart → Payment → Order.*

6. **Regression Testing** – Ensures new changes haven't broken existing functionality.

7. **Smoke Testing** – Quick check to verify that the build is basically working.

8. **Sanity Testing** – Focused testing after a specific bug fix or small change.

9. **Performance Testing** – Checks system performance under load.
   - **Load** – Expected traffic
   - **Stress** – Beyond expected capacity
   - **Spike** – Sudden traffic increase

10. **Security Testing** – Checks authentication, authorization, vulnerabilities, token security, etc.

# Kafka Partitioning Strategies

1. **Key-based Partitioning** – Same key goes to the same partition.  
   `hash(key) % partitions`  
   **Use:** Maintain ordering for the same customer/order.

2. **Round-Robin Partitioning** – Messages are distributed across partitions sequentially.  
   **Use:** Even distribution when there is no key.

3. **Sticky Partitioning** – Producer sends records to one partition for a batch, then switches.  
   **Use:** Better batching and throughput.

4. **Custom Partitioning** – Implement your own partitioning logic.  
   **Use:** Business-specific routing.

### Interview Tip

- **Key-based** → Ordering
- **Round-robin** → Even distribution
- **Sticky** → Better performance/batching
- **Custom** → Business-specific routing