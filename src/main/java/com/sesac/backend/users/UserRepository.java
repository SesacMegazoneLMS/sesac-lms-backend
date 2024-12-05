package com.sesac.backend.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // user_Id에서 userId로 변경 -> JAVA에서 일반적으로 Camel 표기를 사용하므로 JPA가
    // snake 표기는 인식하지 못할 수 있음
    Optional<User> findByUserId(UUID userId);
}
