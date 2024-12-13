package com.sesac.backend.lectures.dto.response;

import com.sesac.backend.lectures.domain.Lecture;
import lombok.*;

// LectureStudentResponse.java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureStudentResponse {
    private Long id;
    private String title;
    private Integer orderIndex;
    private String duration;
    private String videoUrl;  // cloudFrontUrl + videoKey

    public static LectureStudentResponse from(Lecture lecture) {
        return LectureStudentResponse.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .orderIndex(lecture.getOrderIndex())
                .duration(lecture.getDuration())
                .videoUrl(lecture.getCloudFrontUrl() + "/" + lecture.getVideoKey())
                .build();
    }
}