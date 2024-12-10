package com.sesac.backend.users.repository;

import com.sesac.backend.users.domain.InstructorDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstructorDetailRepository extends JpaRepository<InstructorDetail, Long> {
    Optional<InstructorDetail> findByUserUuid(UUID uuid);
    Boolean existsByUserUuid(UUID uuid);
}
