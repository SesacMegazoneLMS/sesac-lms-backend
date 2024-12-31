package com.sesac.backend.statistics.repository;

import com.sesac.backend.statistics.domain.InstructorStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstructorStatsRepository extends JpaRepository<InstructorStats, Long> {

    Optional<InstructorStats> findByUserId(Long userId);
}
