package com.sesac.backend.lectures.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class VideoCompleteRequest {
    private Long lectureId;
    private String videoKey;
    private String status;
    private String duration;
}