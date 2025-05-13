package com.marcinsz.backend.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ResetPasswordMongoRepository extends MongoRepository<ResetPasswordDocument,String> {
}
