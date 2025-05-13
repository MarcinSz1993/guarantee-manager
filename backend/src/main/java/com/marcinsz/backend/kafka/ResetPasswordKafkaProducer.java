package com.marcinsz.backend.kafka;

import com.marcinsz.backend.mongodb.ResetPasswordDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResetPasswordKafkaProducer implements KafkaEventProducer<ResetPasswordDocument> {
    private final KafkaTemplate<String, ResetPasswordDocument> kafkaTemplate;
    @Value("${spring.kafka.topic.reset-password}")
    private String resetPasswordTopic;

    @Override
    public void sendMessage(ResetPasswordDocument message) {
        kafkaTemplate.send(resetPasswordTopic, message);
    }
}
