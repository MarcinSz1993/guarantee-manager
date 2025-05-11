package com.marcinsz.backend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinsz.backend.response.ApiResponse;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordKafkaConsumer extends AbstractKafkaConsumer<ApiResponse>{
    public ResetPasswordKafkaConsumer(ObjectMapper objectMapper) {
        super(objectMapper, ApiResponse.class);
    }

    @KafkaListener(topics = "reset-password",groupId = "guarantee-settings")
    public void consumeResetPasswordMessage(String message) throws JsonProcessingException {
        consume(message);
    }
}
