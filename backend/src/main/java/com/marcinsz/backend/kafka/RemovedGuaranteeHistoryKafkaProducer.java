package com.marcinsz.backend.kafka;

import com.marcinsz.backend.history.GuaranteeHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RemovedGuaranteeHistoryKafkaProducer implements KafkaEventProducer<GuaranteeHistory> {

    private final KafkaTemplate<String, GuaranteeHistory> kafkaTemplate;

    @Value("${spring.kafka.topic.removed-guarantee-history}")
    private  String removedGuaranteeHistoryTopic;

    @Override
    public void sendMessage(GuaranteeHistory message) {
        kafkaTemplate.send(removedGuaranteeHistoryTopic, message);
    }
}
