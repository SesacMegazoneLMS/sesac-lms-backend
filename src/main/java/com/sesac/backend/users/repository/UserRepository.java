package com.sesac.backend.users.repository;

import com.sesac.backend.users.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUuid(UUID userId);
    boolean existsByUuid(UUID userId);

}
