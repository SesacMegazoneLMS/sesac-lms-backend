package com.sesac.backend.users.repository;

import com.sesac.backend.users.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(UUID uuid);
    Boolean existsByUserId(UUID uuid);
}
