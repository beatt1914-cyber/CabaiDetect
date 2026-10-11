package com.example.cabaidetect.repository;

import com.example.cabaidetect.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    // backward compat (old field name used in some controllers)
    default Optional<User> findByEmailOrUsername(String val) { return findByEmail(val); }
    default boolean existsByEmailOrUsername(String val) { return existsByEmail(val); }
}
