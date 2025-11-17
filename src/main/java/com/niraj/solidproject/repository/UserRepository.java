package com.niraj.solidproject.repository;

import com.niraj.solidproject.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Spring Data JPA automagically implements this
    Optional<User> findByUsername(String username);
}