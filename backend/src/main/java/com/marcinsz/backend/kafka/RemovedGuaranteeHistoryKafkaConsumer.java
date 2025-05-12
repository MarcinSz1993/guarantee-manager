package com.marcinsz.backend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinsz.backend.mongodb.RemovedGuaranteeHistoryDocument;
import com.marcinsz.backend.mongodb.RemovedGuaranteeHistoryMongoRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RemovedGuaranteeHistoryKafkaConsumer extends AbstractKafkaConsumer<RemovedGuaranteeHistoryDocument>{
    private final RemovedGuaranteeHistoryMongoRepository removedGuaranteeHistoryMongoRepository;

    public RemovedGuaranteeHistoryKafkaConsumer(ObjectMapper objectMapper, RemovedGuaranteeHistoryMongoRepository removedGuaranteeHistoryMongoRepository) {
        super(objectMapper, RemovedGuaranteeHistoryDocument.class);
        this.removedGuaranteeHistoryMongoRepository = removedGuaranteeHistoryMongoRepository;
    }

    @KafkaListener(topics = "removed-guarantee-history",groupId = "guarantee-message")
    public void consumeRemovedGuaranteeHistoryMessage(String message) throws JsonProcessingException {
        RemovedGuaranteeHistoryDocument removedGuaranteeHistoryDocument = consume(message);
        removedGuaranteeHistoryMongoRepository.insert(removedGuaranteeHistoryDocument);
    }

    @Override
    void saveRecordToMongoDB(RemovedGuaranteeHistoryDocument message) {

    }
}
