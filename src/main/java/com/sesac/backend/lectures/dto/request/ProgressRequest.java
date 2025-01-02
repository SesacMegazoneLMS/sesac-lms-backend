package com.sesac.backend.lectures.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProgressRequest {
    private Double progressRate;
    private Integer watchedSeconds;
} 