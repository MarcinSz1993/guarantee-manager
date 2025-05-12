package com.marcinsz.backend.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface RemovedGuaranteeHistoryMongoRepository extends MongoRepository<RemovedGuaranteeHistoryDocument,String> {
}
