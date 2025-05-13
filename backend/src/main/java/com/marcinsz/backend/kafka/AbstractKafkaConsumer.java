package com.marcinsz.backend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractKafkaConsumer<T> {
    private final ObjectMapper objectMapper;
    private final Class<T> classType;

    public T consume(String message) throws JsonProcessingException {
        log.info("Nieprzetworzona wiadomość: {}", message);
        return objectMapper.readValue(message, classType);
    }

    abstract void saveRecordToMongoDB(T message);
}
