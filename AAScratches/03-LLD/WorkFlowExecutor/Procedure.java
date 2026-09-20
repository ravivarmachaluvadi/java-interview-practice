/*
 * =====================================================================
 *  Procedure - unit of work contract           Packaged helper | header-only
 * =====================================================================
 *
 * ROLE IN THE PROJECT
 *   The unit of work that WorkFlowExecutor runs. One method: take the raw payload
 *   String, do something with it, return nothing. Callers pass a lambda:
 *
 *       workFlowExecutor.submitTask(payload -> handler.process(payload),
 *                                   rawMessage, orderNumber);
 *
 *   This is the seam that decouples WHAT gets executed from WHERE and WHEN. The
 *   executor only knows how to pick a thread and preserve per-key order; it never
 *   knows what the payload means.
 *
 * WHAT TO NOTICE
 *   - It is structurally identical to java.util.function.Consumer<String>. A named
 *     interface is used anyway so the API reads as domain language and cannot be
 *     handed an arbitrary consumer by accident. That is a style call, not a
 *     capability difference - a Consumer lambda would compile here unchanged.
 *
 *   - The return type is void, so the executor can never hand a result back. This
 *     is fire-and-forget by design; anything the task produces must be written to a
 *     store, a queue or a future the task closes over itself.
 *
 *   - The signature declares no checked exception, so implementations must catch or
 *     wrap. WorkFlowExecutor wraps the invoke() call in try/finally precisely so
 *     that an unchecked exception still unlinks the key rather than leaking it.
 *
 *   - @FunctionalInterface is a compile-time assertion, not behaviour: it makes
 *     adding a second abstract method a compile error. A single-method interface is
 *     lambda-compatible with or without it.
 *
 *   - The payload is a String, which means serialisation format is the caller's
 *     problem. A typed Procedure<T> would push that decision into the type system
 *     at the cost of a generic parameter everywhere.
 */
package com.tgt.gom.federator.grouped_processor;

@FunctionalInterface
public interface Procedure {
    void invoke(String payload);
}
