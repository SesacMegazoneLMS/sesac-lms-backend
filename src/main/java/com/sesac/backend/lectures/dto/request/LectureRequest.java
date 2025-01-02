package com.sesac.backend.lectures.dto.request;

import com.sesac.backend.courses.domain.Course;
import com.sesac.backend.lectures.domain.Lecture;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureRequest {
    private Long id;
    private Long courseId;
    private String title;
    private Integer orderIndex;
    private String duration;
    private String videoKey;
    private String status;
    private boolean isCompleted;

    public static LectureRequest from(Lecture lecture) {
        return LectureRequest.builder()
                .id(lecture.getId())
                .courseId(lecture.getCourse().getId())
                .title(lecture.getTitle())
                .duration(lecture.getDuration())
                .orderIndex(lecture.getOrderIndex())
                .videoKey(lecture.getVideoKey())
                .status(lecture.getStatus())
                .build();
    }

    public Lecture toEntity(Course course) {
        return Lecture.builder()
                .course(course)
                .title(this.title)
                .duration(this.duration)
                .orderIndex(this.orderIndex)
                .videoKey(this.videoKey)
                .status(this.status)
                .cloudFrontUrl("https://cdn.sesac-univ.click")
                .build();
    }
    
}
