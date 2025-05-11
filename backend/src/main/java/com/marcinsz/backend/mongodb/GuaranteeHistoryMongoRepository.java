package com.marcinsz.backend.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GuaranteeHistoryMongoRepository extends MongoRepository<GuaranteeHistoryDocument,String> {
}
