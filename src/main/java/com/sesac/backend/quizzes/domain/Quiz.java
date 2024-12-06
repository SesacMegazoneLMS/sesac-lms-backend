package com.sesac.backend.quizzes.domain;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.quizProblems.domain.QuizProblem;
import com.sesac.backend.users.domain.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
    @UniqueConstraint(
        name = "uk_quiz_course_number",  // 제약조건 이름
        columnNames = {"course_id", "quiz_number"}  // 복합 유니크 키로 설정할 컬럼들
    )
})
@Entity
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quizNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuizProblem> quizProblems = new ArrayList<>();

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
