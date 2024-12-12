package com.sesac.backend.lectures.dto.response;

import com.sesac.backend.lectures.domain.Lecture;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureDetailResponse {
    private Long id;
    private Long courseId;
    private String title;
    private String duration;
    private Integer orderIndex;
    private String videoKey;
    private String cloudFrontUrl;
    private String status;

    public static LectureDetailResponse from(Lecture lecture) {
        return LectureDetailResponse.builder()
                .id(lecture.getId())
                .courseId(lecture.getCourse().getId())
                .title(lecture.getTitle())
                .duration(lecture.getDuration())
                .orderIndex(lecture.getOrderIndex())
                .videoKey(lecture.getVideoKey())
                .cloudFrontUrl(lecture.getCloudFrontUrl())
                .status(lecture.getStatus())
                .build();
    }
}