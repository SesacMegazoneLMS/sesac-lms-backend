package com.sesac.backend.lectures.dto.request;

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
    private Integer orderIndex;
    private Boolean isFree;
    private String duration;

    private String videoKey;
    private String hlsUrl;
    private String status;
    
}
