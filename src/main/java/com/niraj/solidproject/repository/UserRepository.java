package com.niraj.solidproject.repository;

import com.niraj.solidproject.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA automagically implements this
    Optional<User> findByUsername(String username);
}