package com.marcinsz.backend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinsz.backend.history.GuaranteeHistory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RemovedGuaranteeHistoryKafkaConsumer extends AbstractKafkaConsumer<GuaranteeHistory>{
    public RemovedGuaranteeHistoryKafkaConsumer(ObjectMapper objectMapper) {
        super(objectMapper, GuaranteeHistory.class);
    }

    @KafkaListener(topics = "removed-guarantee-history",groupId = "guarantee-message")
    public void consumeRemovedGuaranteeHistoryMessage(String message) throws JsonProcessingException {
        consume(message);
    }

    @Override
    void saveRecordToMongoDB(GuaranteeHistory message) {

    }
}
