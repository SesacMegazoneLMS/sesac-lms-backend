package com.sesac.backend.learning.repository;

import com.sesac.backend.learning.domain.Learning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningRepository extends JpaRepository<Learning, Long> {

}
