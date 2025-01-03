package com.sesac.backend.courses.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InstructorInfoDTO {
    private String nickname;
    private String introduction;
    private String techStack;
    private String websiteUrl;
    private String linkedinUrl;
    private String githubUrl;

    public InstructorInfoDTO(String nickname, String introduction, String techStack, String websiteUrl, String linkedinUrl, String githubUrl){
        this.nickname = nickname;
        this.introduction = introduction;
        this.techStack = techStack;
        this.websiteUrl = websiteUrl;
        this.linkedinUrl = linkedinUrl;
        this.githubUrl = githubUrl;
    }
}
