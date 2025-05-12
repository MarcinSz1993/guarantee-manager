package com.marcinsz.backend.kafka;

import com.marcinsz.backend.mongodb.RemovedGuaranteeHistoryDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemovedGuaranteeHistoryKafkaProducer implements KafkaEventProducer<RemovedGuaranteeHistoryDocument> {

    private final KafkaTemplate<String, RemovedGuaranteeHistoryDocument> kafkaTemplate;

    @Value("${spring.kafka.topic.removed-guarantee-history}")
    private  String removedGuaranteeHistoryTopic;

    @Override
    public void sendMessage(RemovedGuaranteeHistoryDocument message) {
        kafkaTemplate.send(removedGuaranteeHistoryTopic, message);
    }
}
