package com.sesac.backend.statistics.repository;

import com.sesac.backend.statistics.domain.InstructorStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorStatsRepository extends JpaRepository<InstructorStats, Long> {

    InstructorStats findByUserId(Long userId);
}
