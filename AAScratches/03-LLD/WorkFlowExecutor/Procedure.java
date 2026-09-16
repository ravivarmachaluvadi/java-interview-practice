package com.tgt.gom.federator.grouped_processor;

@FunctionalInterface
public interface Procedure {
    void invoke(String payload);
}
