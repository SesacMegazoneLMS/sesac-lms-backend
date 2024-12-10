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
    private Long courseId;
    private String title;
    private String videoKey;
    private Integer orderIndex;
    private String duration;
    private String hlsUrl;
    private String status;

    public static LectureRequest from(Lecture lecture) {
        return LectureRequest.builder()
                .courseId(lecture.getId())
                .title(lecture.getTitle())
                .duration(lecture.getDuration())
                .orderIndex(lecture.getOrderIndex())
                .videoKey(lecture.getVideoKey())
                .hlsUrl(lecture.getHlsUrl())
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
                .hlsUrl(this.hlsUrl)
                .status(this.status)
                .build();
    }
    
}
