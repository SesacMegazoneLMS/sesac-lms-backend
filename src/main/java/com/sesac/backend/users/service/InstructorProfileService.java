package com.sesac.backend.users.service;

import com.sesac.backend.users.domain.InstructorDetail;
import com.sesac.backend.users.dto.request.InstructorImageUpdateRequest;
import com.sesac.backend.users.dto.request.InstructorInfoUpdateRequest;
import com.sesac.backend.users.dto.response.InstructorProfileGetResponse;
import com.sesac.backend.users.dto.response.InstructorProfilePutResponse;
import com.sesac.backend.users.dto.InstructorProfile;
import com.sesac.backend.users.repository.InstructorProfileRepository;
import com.sesac.backend.users.common.SerializationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstructorProfileService {

    private final InstructorProfileRepository ins_repo;

    private boolean checkUserValidate(UUID uuid) {
        return ins_repo.existsByUserUuid(uuid);
    }


    public InstructorProfileGetResponse getInstructorProfile(UUID uuid) {
        if (checkUserValidate(uuid)) {
            InstructorDetail instructorDetail = ins_repo.findByUserUuid(uuid);
            InstructorProfile instructorProfile = new InstructorProfile(instructorDetail);
            instructorProfile.setTechStack(SerializationUtils.deserializeTechStack(instructorDetail.getTechStack()));
            return new InstructorProfileGetResponse("OK",null,null, instructorProfile);
        }
        throw new IllegalArgumentException();
    }

    public InstructorProfilePutResponse updateInstructorProfile(UUID uuid, InstructorImageUpdateRequest img) {
        if (checkUserValidate(uuid)) {
            InstructorDetail instructorDetail = ins_repo.findByUserUuid(uuid);
            instructorDetail.setProfileImgUrl(img.getProfileImgUrl());
            ins_repo.save(instructorDetail);
            return new InstructorProfilePutResponse("OK", null, "프로필 이미지 변경 성공");
        } throw new IllegalArgumentException();
    }

    public InstructorProfilePutResponse updateInstructorProfile(UUID uuid, InstructorInfoUpdateRequest info) {
        if (info == null || uuid == null) {
            throw new IllegalArgumentException("입력 데이터가 유효하지 않습니다.");
        }

        InstructorDetail instructorDetail = ins_repo.findByUserUuid(uuid);
        if (instructorDetail == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
        }

        if (info.getIntroduction() != null) {
            instructorDetail.setIntroduction(info.getIntroduction());
        }
        if (info.getTechStack() != null) {
            instructorDetail.setTechStack(SerializationUtils.serializeTechStack(info.getTechStack()));
        }
        if (info.getWebsiteUrl() != null) {
            instructorDetail.setWebsiteUrl(info.getWebsiteUrl());
        }
        if (info.getLinkedinUrl() != null) {
            instructorDetail.setLinkedinUrl(info.getLinkedinUrl());
        }
        if (info.getGithubUrl() != null) {
            instructorDetail.setGithubUrl(info.getGithubUrl());
        }

        ins_repo.save(instructorDetail);
        return new InstructorProfilePutResponse("OK", null, "프로필 정보 변경 성공");

    }
}