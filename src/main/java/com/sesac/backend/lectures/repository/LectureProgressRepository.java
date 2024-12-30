package com.sesac.backend.lectures.repository;

import com.sesac.backend.lectures.domain.LectureProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LectureProgressRepository extends JpaRepository<LectureProgress, Long> {
    Optional<LectureProgress> findByUserUuidAndLectureId(UUID userUuid, Long lectureId);
    List<LectureProgress> findByUserUuid(UUID userUuid);
} 