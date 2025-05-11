package com.marcinsz.backend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public abstract class AbstractKafkaConsumer<T> {
    private final ObjectMapper objectMapper;
    private final Class<T> classType;

    public T consume(String message) throws JsonProcessingException {
        System.out.println("Nieprzetworzona wiadomość: " + message);
        return objectMapper.readValue(message, classType);
    }

    abstract void saveRecordToMongoDB(T message);
}
