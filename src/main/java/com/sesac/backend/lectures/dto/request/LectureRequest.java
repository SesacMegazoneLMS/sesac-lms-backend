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
    private String videoKey;
    private Integer orderIndex;
    private String duration;
    private String hlsUrl;
    private String status;
    
}
