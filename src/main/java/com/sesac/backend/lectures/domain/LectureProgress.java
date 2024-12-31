package com.sesac.backend.lectures.domain;


import com.sesac.backend.users.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@Entity
public class LectureProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;  // UUID 대신 User 엔티티 참조

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    private Double progressRate;    // 진도율 (0-100%)
    private Integer watchedSeconds; // 시청 시간(초)
    private LocalDateTime lastWatchedAt; // 마지막 시청 시간
    private Boolean isCompleted = false;    // 수강 완료 여부
}
