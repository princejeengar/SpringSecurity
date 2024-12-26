package com.registration.repository;

import com.registration.entity.JwtToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JwtTokenRepository extends MongoRepository<JwtToken, String> {
    boolean existsByToken(String token);
    void deleteByToken(String token);
}