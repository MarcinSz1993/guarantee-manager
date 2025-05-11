package com.marcinsz.backend.kafka;

import org.springframework.stereotype.Component;

@Component
@FunctionalInterface
public interface KafkaEventConsumer {
    void consumeMessage();
}
