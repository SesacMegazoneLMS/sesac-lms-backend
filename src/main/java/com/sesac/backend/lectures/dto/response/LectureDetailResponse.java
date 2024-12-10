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
    private Boolean isFree;
    private Integer orderIndex;
    private String videoKey;
    private String hlsUrl;
    private String status;

    public static LectureDetailResponse from(Lecture lecture) {
        return LectureDetailResponse.builder()
                .id(lecture.getId())
                .courseId(lecture.getCourse().getId())
                .title(lecture.getTitle())
                .duration(lecture.getDuration())
                .isFree(lecture.getIsFree())
                .orderIndex(lecture.getOrderIndex())
                .videoKey(lecture.getVideoKey())
                .hlsUrl(lecture.getHlsUrl())
                .status(lecture.getStatus())
                .build();
    }
}