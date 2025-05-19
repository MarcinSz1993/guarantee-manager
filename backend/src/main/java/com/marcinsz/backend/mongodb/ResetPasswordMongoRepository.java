package com.marcinsz.backend.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResetPasswordMongoRepository extends MongoRepository<ResetPasswordDocument,String> {
    List<ResetPasswordDocument> findAllByEmail(String email);
}
