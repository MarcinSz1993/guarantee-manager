package com.marcinsz.backend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public abstract class AbstractKafkaConsumer<T> {
    private final ObjectMapper objectMapper;
    private final Class<T> typeParameterClass;

    public void consume(String message) throws JsonProcessingException {
        System.out.println("Nieprzetworzona wiadomość: " + message);

        T processedMessage = objectMapper.readValue(message, typeParameterClass);
        System.out.println("Przetworzona wiadomość " + processedMessage);
    }
}
