package com.sesac.backend.users.service;

import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.dto.UserProfile;
import com.sesac.backend.users.dto.request.UserProfileUpdateRequest;
import com.sesac.backend.users.dto.response.InstructorProfileGetResponse;
import com.sesac.backend.users.dto.response.UserProfileGetResponse;
import com.sesac.backend.users.dto.response.UserProfilePutResponse;
import com.sesac.backend.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepo;

    private boolean checkUserValidate(UUID uuid) {
        return userRepo.existsByUserId(uuid);
    }

    public UserProfileGetResponse getUserProfile(UUID uuid) {
        if(!checkUserValidate(uuid)) {
            throw new IllegalArgumentException();
        }
        User user = userRepo.findByUserId(uuid).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        UserProfile profile = new UserProfile(user);
        return new UserProfileGetResponse("OK", null, "프로필 정보 조회 성공",profile);
    }

    public UserProfilePutResponse updateUserProfile(UUID uuid, UserProfileUpdateRequest profile) {
        if(!checkUserValidate(uuid)) {
            throw new IllegalArgumentException();
        }
        User user = userRepo.findByUserId(uuid).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        if (profile.getNickname() != null) {
            user.setNickname(profile.getNickname());
        }
        if (profile.getPhoneNumber() != null) {
            user.setPhoneNumber(profile.getPhoneNumber());
        }
        if (profile.getAddress() != null) {
            user.setAddress(profile.getAddress());
        }
        return new UserProfilePutResponse("OK", null, "프로필 정보 변경 성공");
}
}
