package com.sesac.backend.quizProblems.domain;

import com.sesac.backend.quizProblems.enums.Answer;
import com.sesac.backend.quizProblems.enums.Correctness;
import com.sesac.backend.quizzes.domain.Quiz;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@ToString(exclude = {"quiz"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"quiz_id", "problem_Number"})
})
@Entity
public class QuizProblem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false)
    private Integer problemNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Answer correctAnswer;

    @Enumerated(EnumType.STRING)
    private Answer selectedAnswer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Correctness correctness = Correctness.WRONG;

    @Column(nullable = false)
    private String question;

    @ElementCollection
    private List<String> choices = new ArrayList<>();
}
