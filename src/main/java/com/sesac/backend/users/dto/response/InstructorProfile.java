package com.sesac.backend.users.dto.response;

import com.sesac.backend.users.domain.InstructorDetail;
import com.sesac.backend.users.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstructorProfile {
    private String nickname;
    private String introduction;
    private List<String> techStack; // DB에서 스트링 형식의 기술 스택을 List 형태로 변환함
    private String websiteUrl;
    private String linkedinUrl;
    private String githubUrl;
    private String profileImgUrl;

    public InstructorProfile(InstructorDetail detail) {
        this.nickname = detail.getUser().getNickname();
        this.introduction = detail.getIntroduction();
        this.websiteUrl = detail.getWebsiteUrl();
        this.linkedinUrl = detail.getLinkedinUrl();
        this.githubUrl = detail.getGithubUrl();
        this.profileImgUrl = detail.getProfileImgUrl();
    }
}
