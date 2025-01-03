package com.sesac.backend.lectures.repository;

import com.sesac.backend.lectures.domain.LectureProgress;
import com.sesac.backend.users.domain.User;
import com.sesac.backend.lectures.domain.Lecture;
import com.sesac.backend.users.domain.StudentDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LectureProgressRepository extends JpaRepository<LectureProgress, Long> {
    Optional<LectureProgress> findByStudentAndLecture(User student, Lecture lecture);
    List<LectureProgress> findByStudentUuid(UUID studentUuid);
    Optional<LectureProgress> findByStudentUuidAndLectureId(UUID studentUuid, Long lectureId);
    Optional<LectureProgress> findByStudentUuidAndLectureIdAndIsCompleted(UUID studentUuid, Long lectureId, Boolean isCompleted);

    Optional<LectureProgress> findByLectureId(Long id);

    Optional<LectureProgress> findByLectureIdAndStudent_Uuid(Long lectureId, UUID studentUuid);
}