package com.marcinsz.backend.kafka;

import com.marcinsz.backend.mongodb.GuaranteeHistoryDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuaranteeHistoryKafkaProducer implements KafkaEventProducer<GuaranteeHistoryDocument> {
    private final KafkaTemplate<String, GuaranteeHistoryDocument> kafkaTemplate;
    @Value("${spring.kafka.topic.guarantee-history}")
    private  String guaranteeHistoryTopic;

    @Override
    public void sendMessage(GuaranteeHistoryDocument message) {
        kafkaTemplate.send(guaranteeHistoryTopic,message);
    }
}
