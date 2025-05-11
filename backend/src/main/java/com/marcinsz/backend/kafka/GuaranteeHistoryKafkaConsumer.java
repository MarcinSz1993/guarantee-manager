package com.marcinsz.backend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinsz.backend.history.GuaranteeHistory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class GuaranteeHistoryKafkaConsumer extends AbstractKafkaConsumer<GuaranteeHistory>{

    public GuaranteeHistoryKafkaConsumer(ObjectMapper objectMapper) {
        super(objectMapper, GuaranteeHistory.class);
    }

    @KafkaListener(topics = "guarantee-history",groupId = "guarantee-message")
    public void consumeGuaranteeHistoryMessage(String message) throws JsonProcessingException {
        consume(message);
    }

}
