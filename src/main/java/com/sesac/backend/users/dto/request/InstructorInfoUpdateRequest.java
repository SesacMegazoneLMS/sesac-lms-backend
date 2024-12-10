package com.sesac.backend.users.dto.request;

import java.util.List;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class InstructorInfoUpdateRequest {

    private String introduction;
    private List<String> techStack;
    private String websiteUrl;
    private String linkedinUrl;
    private String githubUrl;
}
