package com.sesac.backend.lectures.service;

import com.sesac.backend.lectures.domain.Lecture;
import com.sesac.backend.lectures.domain.LectureProgress;
import com.sesac.backend.lectures.repository.LectureProgressRepository;
import com.sesac.backend.lectures.repository.LectureRepository;
import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LectureProgressService {
    private final LectureProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;

    public void saveProgress(Long lectureId, String userUuid, Double progressRate, Integer watchedSeconds) {
        User user = userRepository.findByUuid(UUID.fromString(userUuid))
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        Lecture lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> new IllegalArgumentException("강의를 찾을 수 없습니다."));

        LectureProgress progress = progressRepository
            .findByUserUuidAndLectureId(user.getUuid(), lectureId)
            .orElse(new LectureProgress());

        progress.setUser(user);
        progress.setLecture(lecture);
        progress.setProgressRate(progressRate);
        progress.setWatchedSeconds(watchedSeconds);
        progress.setLastWatchedAt(LocalDateTime.now());
        progress.setIsCompleted(progressRate >= 90);  // 90% 이상 시청 시 완료로 처리

        progressRepository.save(progress);
    }

    // 학생의 총 학습 시간 조회
    public int getTotalWatchedSeconds(UUID userUuid) {
        return progressRepository.findByUserUuid(userUuid).stream()
            .mapToInt(LectureProgress::getWatchedSeconds)
            .sum();
    }

    // 학생의 완료한 강의 수 조회
    public long getCompletedLecturesCount(UUID userUuid) {
        return progressRepository.findByUserUuid(userUuid).stream()
            .filter(LectureProgress::getIsCompleted)
            .count();
    }
}
