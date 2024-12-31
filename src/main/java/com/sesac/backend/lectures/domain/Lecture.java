package com.sesac.backend.lectures.domain;

import com.sesac.backend.courses.domain.Course;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString(exclude = {"course"})
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 강의 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course; // 강의가 속한 코스

    // @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "lecture_progress_id")
    // private LectureProgress lectureProgress;



    private String title; // 강의 제목
    private Integer orderIndex;  // 강의 순서

    //영상정보
    private String videoKey;      // S3 객체 키 //
    private String duration; // 강의 시간 //
    private String cloudFrontUrl = "https://cdn.sesac-univ.click";
    private String status;        // PROCESSING, COMPLETED, FAILED
}
