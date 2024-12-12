package com.sesac.backend.courses.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CourseSearchCriteria {

    private String sort;

    private List<String> categories;

    private String level;

    private String search;
}
