package com.marcinsz.backend.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GuaranteeHistoryMongoRepository extends MongoRepository<GuaranteeHistoryDocument,String> {
    List<GuaranteeHistoryDocument> findAllByGuaranteeOwnerEmail(String email);
}
