package com.sesac.backend.enrollments.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EnrolledLectureDto {

    private Long id;
    private String title;
    private String duration;
    private Boolean isFree;
    private Integer orderIndex;
    private String videoKey;
    private String cloudFrontUrl;
    private String status;
}
