package com.sesac.backend.users.service;

import com.sesac.backend.users.domain.InstructorDetail;
import com.sesac.backend.users.dto.request.UpdateProfileImg;
import com.sesac.backend.users.dto.request.UpdateProfileInfo;
import com.sesac.backend.users.dto.response.GetUserProfileResponse;
import com.sesac.backend.users.dto.response.PutProfileResponse;
import com.sesac.backend.users.dto.response.InstructorProfile;
import com.sesac.backend.users.repository.InstructorDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final InstructorDetailRepository repo;


    // List<String> -> String (직렬화)
    private String serializeTechStack(List<String> techStack) {
        return String.join(",", techStack);
    }

    // String -> List<String> (역직렬화)
    private List<String> deserializeTechStack(String techStack) {
        if (techStack == null || techStack.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(techStack.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public void checkUserValidate(UUID uuid) {
        if(!repo.existsByUserUuid(uuid)){
            throw new IllegalArgumentException();
        }
    }

    public GetUserProfileResponse getUserProfile(UUID uuid) {
        InstructorDetail instructorDetail = repo.findByUserUuid(uuid).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        InstructorProfile instructorProfile = new InstructorProfile(instructorDetail);
        instructorProfile.setTechStack(deserializeTechStack(instructorDetail.getTechStack()));
        return new GetUserProfileResponse("OK",null,null, instructorProfile);
    }


    public PutProfileResponse updateUserProfile(UUID uuid, UpdateProfileImg img) {
        InstructorDetail instructorDetail = repo.findByUserUuid(uuid).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        instructorDetail.setProfileImgUrl(img.getProfileImgUrl());
        repo.save(instructorDetail);
        return new PutProfileResponse("OK",null,"프로필 이미지 변경 성공");
    }
    public PutProfileResponse updateUserProfile(UUID uuid, UpdateProfileInfo info) {
        InstructorDetail instructorDetail = repo.findByUserUuid(uuid).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        instructorDetail.getUser().setNickname(info.getNickname());
        instructorDetail.setIntroduction(info.getIntroduction());
        instructorDetail.setTechStack(serializeTechStack(info.getTechStack()));
        instructorDetail.setWebsiteUrl(info.getWebsiteUrl());
        instructorDetail.setLinkedinUrl(info.getLinkedinUrl());
        instructorDetail.setGithubUrl(info.getGithubUrl());
        repo.save(instructorDetail);
        return new PutProfileResponse("OK",null,"프로필 정보 변경 성공");
    }
}