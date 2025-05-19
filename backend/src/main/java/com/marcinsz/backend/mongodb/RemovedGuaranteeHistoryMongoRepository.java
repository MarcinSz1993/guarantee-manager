package com.marcinsz.backend.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RemovedGuaranteeHistoryMongoRepository extends MongoRepository<RemovedGuaranteeHistoryDocument,String> {
    List<RemovedGuaranteeHistoryDocument> findAllByGuaranteeOwnerEmail(String email);
}
