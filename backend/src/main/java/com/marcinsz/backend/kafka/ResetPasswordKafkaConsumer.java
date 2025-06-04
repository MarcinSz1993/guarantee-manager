package com.marcinsz.backend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcinsz.backend.mongodb.ResetPasswordDocument;
import com.marcinsz.backend.mongodb.ResetPasswordMongoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResetPasswordKafkaConsumer extends AbstractKafkaConsumer<ResetPasswordDocument>{
    private final ResetPasswordMongoRepository resetPasswordMongoRepository;
    public ResetPasswordKafkaConsumer(ObjectMapper objectMapper, ResetPasswordMongoRepository resetPasswordMongoRepository) {
        super(objectMapper, ResetPasswordDocument.class);
        this.resetPasswordMongoRepository = resetPasswordMongoRepository;
    }

    @KafkaListener(topics = "reset-password",groupId = "guarantee-settings")
    public void consumeResetPasswordMessage(String message) throws JsonProcessingException {
        ResetPasswordDocument resetPasswordDocument = consume(message);
        saveRecordToMongoDB(resetPasswordDocument);
        log.info("Przetworzona wiadomość: {}", resetPasswordDocument);
    }

    @Override
    void saveRecordToMongoDB(ResetPasswordDocument message) {
        log.info("Zapisana wiadomość w mongoDB{}", message.toString());
        resetPasswordMongoRepository.save(message);
    }
}