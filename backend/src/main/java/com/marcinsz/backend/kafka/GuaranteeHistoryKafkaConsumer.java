package com.marcinsz.backend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinsz.backend.mongodb.GuaranteeHistoryDocument;
import com.marcinsz.backend.mongodb.GuaranteeHistoryMongoRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class GuaranteeHistoryKafkaConsumer extends AbstractKafkaConsumer<GuaranteeHistoryDocument>{
    private final GuaranteeHistoryMongoRepository guaranteeHistoryMongoRepository;

    public GuaranteeHistoryKafkaConsumer(ObjectMapper objectMapper, GuaranteeHistoryMongoRepository guaranteeHistoryMongoRepository) {
        super(objectMapper, GuaranteeHistoryDocument.class);
        this.guaranteeHistoryMongoRepository = guaranteeHistoryMongoRepository;

    }

    @KafkaListener(topics = "guarantee-history",groupId = "guarantee-message")
    public void consumeGuaranteeHistoryMessage(String message) throws JsonProcessingException {
        GuaranteeHistoryDocument consumedAndProcessedMessage = consume(message);
        saveRecordToMongoDB(consumedAndProcessedMessage);
    }

    @Override
    void saveRecordToMongoDB(GuaranteeHistoryDocument message) {
        guaranteeHistoryMongoRepository.save(message);
    }
}
