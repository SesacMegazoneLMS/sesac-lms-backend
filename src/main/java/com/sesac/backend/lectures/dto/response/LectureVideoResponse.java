package com.sesac.backend.lectures.dto.response;

import com.sesac.backend.lectures.domain.Lecture;
import lombok.*;

import java.util.List;

// LectureVedioResponse.java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureVideoResponse {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String title;
    private Integer orderIndex;
    private String duration;
    private String videoUrl;  // cloudFrontUrl + videoKey
    private String status;
    private List<LectureNavigation> navigation;

    public static LectureVideoResponse from(Lecture lecture, List<LectureNavigation> navigation) {
        return LectureVideoResponse.builder()
                .id(lecture.getId())
                .courseId(lecture.getCourse().getId())
                .courseTitle(lecture.getCourse().getTitle())
                .title(lecture.getTitle())
                .orderIndex(lecture.getOrderIndex())
                .duration(lecture.getDuration())
                .videoUrl(lecture.getCloudFrontUrl() + "/" + lecture.getVideoKey())
                .status(lecture.getStatus())
                .navigation(navigation)
                .build();
    }
}