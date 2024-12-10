package com.sesac.backend.users.repository;

import com.sesac.backend.users.domain.InstructorDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InstructorProfileRepository extends JpaRepository<InstructorDetail, Long> {
    InstructorDetail findByUserUserId(UUID uuid);
    boolean existsByUserUserId(UUID uuid);

}
