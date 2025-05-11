package com.marcinsz.backend.kafka;

import com.marcinsz.backend.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResetPasswordKafkaProducer implements KafkaEventProducer<ApiResponse> {
    private final KafkaTemplate<String, ApiResponse> kafkaTemplate;
    @Value("${spring.kafka.topic.reset-password}")
    private String resetPasswordTopic;

    @Override
    public void sendMessage(ApiResponse message) {
        kafkaTemplate.send(resetPasswordTopic, message);
    }
}
