package com.sesac.backend.users.dto.response;

import com.sesac.backend.users.enums.Expertise;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private UUID userId;
    private String email;
    private String address;
    private String name;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userType;
    private String bio;
    private List<Expertise> expertises;
    private String websiteUrl;
    private String linkedinUrl;
    private String githubUrl;
}
