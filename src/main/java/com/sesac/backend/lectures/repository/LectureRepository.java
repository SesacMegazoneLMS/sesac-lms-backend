package com.sesac.backend.lectures.repository;

import com.sesac.backend.lectures.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Optional<Lecture> findByVideoKey(String videoKey);
}
