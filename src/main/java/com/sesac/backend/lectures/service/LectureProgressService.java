package com.sesac.backend.lectures.service;

import com.sesac.backend.lectures.domain.Lecture;
import com.sesac.backend.lectures.domain.LectureProgress;
import com.sesac.backend.lectures.repository.LectureProgressRepository;
import com.sesac.backend.lectures.repository.LectureRepository;
import com.sesac.backend.users.domain.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LectureProgressService {
    private final LectureProgressRepository progressRepository;
    private final LectureRepository lectureRepository;

    @Transactional
    public void saveProgress(User student, Long lectureId, Double progressRate, Integer watchedSeconds) {
        Lecture lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> new EntityNotFoundException("강의를 찾을 수 없습니다."));

        LectureProgress progress = progressRepository
            .findByStudentAndLecture(student, lecture)
            .orElse(new LectureProgress());

        progress.setStudent(student);
        progress.setLecture(lecture);
        progress.setProgressRate(progressRate);
        progress.setWatchedSeconds(watchedSeconds);
        progress.setLastWatchedAt(LocalDateTime.now());
        progress.setIsCompleted(progressRate >= 90);

        progressRepository.save(progress);
        log.info("Progress saved for student: {}, lecture: {}, rate: {}", student.getUuid(), lectureId, progressRate);
    }

    // 학생의 총 학습 시간 조회
    public int getTotalWatchedSeconds(UUID userUuid) {
        return progressRepository.findByStudentUuid(userUuid).stream()
            .mapToInt(LectureProgress::getWatchedSeconds)
            .sum();
    }

    // 학생의 완료한 강의 수 조회
    public long getCompletedLecturesCount(UUID userUuid) {
        return progressRepository.findByStudentUuid(userUuid).stream()
            .filter(LectureProgress::getIsCompleted)
            .count();
    }
}
