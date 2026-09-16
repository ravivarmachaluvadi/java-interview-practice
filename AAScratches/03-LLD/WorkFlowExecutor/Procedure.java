/**
 * Defines a contract for processing a single payload within a grouped
 * federated stream. Implementations receive the raw payload as a {@code String}
 * and perform any side‑effecting operation (e.g., logging, transformation,
 * persistence). The interface is marked {@link FunctionalInterface} so it can be
 * used with lambda expressions or method references.
 *
 * <p>Usage pattern:
 * <pre>
 * Procedure proc = payload -> System.out.println(payload);
 * proc.invoke("sample data");
 * </pre>
 *
 * <p>This abstraction decouples the processing logic from the grouping and
 * dispatching mechanism, enabling flexible composition of processors.
 *
 * @param payload the raw string to be processed
 *
 * Time Complexity: O(1) per invocation (the method body determines actual cost).
 * Space Complexity: O(1) – no additional storage beyond local variables.
 */
package com.tgt.gom.federator.grouped_processor;

@FunctionalInterface
public interface Procedure {
    void invoke(String payload);
}
