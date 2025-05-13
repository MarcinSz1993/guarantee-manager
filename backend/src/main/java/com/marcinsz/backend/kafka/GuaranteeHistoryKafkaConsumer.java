package com.marcinsz.backend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinsz.backend.mongodb.GuaranteeHistoryDocument;
import com.marcinsz.backend.mongodb.GuaranteeHistoryMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GuaranteeHistoryKafkaConsumer extends AbstractKafkaConsumer<GuaranteeHistoryDocument>{
    private final GuaranteeHistoryMongoRepository guaranteeHistoryMongoRepository;

    public GuaranteeHistoryKafkaConsumer(ObjectMapper objectMapper, GuaranteeHistoryMongoRepository guaranteeHistoryMongoRepository) {
        super(objectMapper, GuaranteeHistoryDocument.class);
        this.guaranteeHistoryMongoRepository = guaranteeHistoryMongoRepository;

    }

    @KafkaListener(topics = "guarantee-history",groupId = "guarantee-message")
    public void consumeGuaranteeHistoryMessage(String message) throws JsonProcessingException {
        GuaranteeHistoryDocument guaranteeHistoryDocument = consume(message);
        saveRecordToMongoDB(guaranteeHistoryDocument);
        log.info("Przetworzona wiadomość: {}", guaranteeHistoryDocument);
    }

    @Override
    void saveRecordToMongoDB(GuaranteeHistoryDocument message) {
        guaranteeHistoryMongoRepository.insert(message);
    }
}
