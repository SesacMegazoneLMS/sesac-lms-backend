package com.sesac.backend.users.service;

import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.dto.request.UpdateProfileImg;
import com.sesac.backend.users.dto.request.UpdateProfileInfo;
import com.sesac.backend.users.dto.response.GetUserProfileResponse;
import com.sesac.backend.users.dto.response.PutProfileResponse;
import com.sesac.backend.users.dto.response.UserProfile;
import com.sesac.backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repo;


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
        if(!repo.existsByUserId(uuid)){
            throw new IllegalArgumentException();
        }
    }

    public GetUserProfileResponse getUserProfile(UUID uuid) {
        User user = repo.findByUserId(uuid).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserProfile userProfile = new UserProfile(user);
        userProfile.setTechStack(deserializeTechStack(user.getTechStack()));
        return new GetUserProfileResponse("OK",null,null,userProfile);
    }


    public PutProfileResponse updateUserProfile(UUID uuid, UpdateProfileImg img) {
        User user = repo.findByUserId(uuid).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setProfileImgUrl(img.getProfileImgUrl());
        repo.save(user);
        return new PutProfileResponse("OK",null,"프로필 이미지 변경 성공");
    }
    public PutProfileResponse updateUserProfile(UUID uuid, UpdateProfileInfo info) {
        User user = repo.findByUserId(uuid).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setNickname(info.getNickname());
        user.setIntroduction(info.getIntroduction());
        user.setTechStack(serializeTechStack(info.getTechStack()));
        user.setWebsiteUrl(info.getWebsiteUrl());
        user.setLinkedinUrl(info.getLinkedinUrl());
        user.setGithubUrl(info.getGithubUrl());
        repo.save(user);
        return new PutProfileResponse("OK",null,"프로필 정보 변경 성공");
    }
}