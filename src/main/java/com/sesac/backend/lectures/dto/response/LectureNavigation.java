package com.sesac.backend.lectures.dto.response;

import com.sesac.backend.lectures.domain.Lecture;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureNavigation {
    private Long id;
    private String title;
    private Integer orderIndex;
    private String duration;
    private String status;

    public static LectureNavigation from(Lecture lecture) {
        return LectureNavigation.builder()
                .id(lecture.getId())
                .title(lecture.getTitle())
                .orderIndex(lecture.getOrderIndex())
                .duration(lecture.getDuration())
                .status(lecture.getStatus())
                .build();
    }
}
