package com.marcinsz.backend.kafka;

import com.marcinsz.backend.history.GuaranteeHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuaranteeHistoryKafkaProducer implements KafkaEventProducer<GuaranteeHistory> {
    private final KafkaTemplate<String, GuaranteeHistory> kafkaTemplate;
    @Value("${spring.kafka.topic.guarantee-history}")
    private  String guaranteeHistoryTopic;

    @Override
    public void sendMessage(GuaranteeHistory message) {
        kafkaTemplate.send(guaranteeHistoryTopic,message);
    }
}
