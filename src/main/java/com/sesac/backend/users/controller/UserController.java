package com.sesac.backend.users.controller;

import java.util.UUID;

import com.sesac.backend.audit.CurrentUser;
import com.sesac.backend.users.dto.request.InstructorImageUpdateRequest;
import com.sesac.backend.users.dto.request.InstructorInfoUpdateRequest;
import com.sesac.backend.users.dto.request.UserProfileUpdateRequest;
import com.sesac.backend.users.dto.response.InstructorProfileGetResponse;
import com.sesac.backend.users.dto.response.InstructorProfilePutResponse;
import com.sesac.backend.users.dto.response.UserProfileGetResponse;
import com.sesac.backend.users.dto.response.UserProfilePutResponse;
import com.sesac.backend.users.service.InstructorProfileService;

import com.sesac.backend.users.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/profile")
public class UserController {

    private final InstructorProfileService ins_Service;
    private final UserProfileService user_Service;
    @Autowired
    public UserController(InstructorProfileService ins_Service, UserProfileService userProfileService) {
        this.ins_Service = ins_Service;
        this.user_Service = userProfileService;
    }

    @GetMapping("/") // 사용자 기본 프로필 정보 가져오기
    public ResponseEntity<UserProfileGetResponse> getUserProfile(@CurrentUser UUID uuid) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(user_Service.getUserProfile(uuid));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new UserProfileGetResponse("UNAUTHORIZED", "402", "인증되지 않은 사용자입니다.", null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new UserProfileGetResponse("INTERNAL SERVER ERROR", "500", "서버에서 요청을 처리할 수 없습니다.", null));
        }
    }

    @PutMapping("/") // 사용자 기본 프로필 정보 가져오기
    public ResponseEntity<UserProfilePutResponse> updateUserProfile(@CurrentUser UUID uuid, @RequestBody @Valid UserProfileUpdateRequest profile) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(user_Service.updateUserProfile(uuid,profile));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new UserProfilePutResponse("UNAUTHORIZED", "402", "인증되지 않은 사용자입니다."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new UserProfilePutResponse("INTERNAL SERVER ERROR", "500", "서버에서 요청을 처리할 수 없습니다."));
        }
    }

    @GetMapping("/instructor") // 강사의 프로필 정보 가져오기
    public ResponseEntity<InstructorProfileGetResponse> getInsProfile(@CurrentUser UUID uuid) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(ins_Service.getInstructorProfile(uuid));
        }catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new InstructorProfileGetResponse("UNAUTHORIZED","402","인증되지 않은 사용자입니다.",null));
        }catch(Exception e) {
            return ResponseEntity.internalServerError()
                .body(new InstructorProfileGetResponse("INTERNAL SERVER ERROR","500","서버에서 요청을 처리할 수 없습니다.",null));
        }
    }

    @PutMapping("/instructor") // 강사의 프로필 정보 업데이트
    public ResponseEntity<InstructorProfilePutResponse> updateInsProfile(@CurrentUser UUID uuid, @Valid @RequestBody InstructorInfoUpdateRequest info ) {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(ins_Service.updateInstructorProfile(uuid, info));
        }catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new InstructorProfilePutResponse("UNAUTHORIZED","402","인증되지 않은 사용자입니다."));
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new InstructorProfilePutResponse("INTERNAL SERVER ERROR","500","서버에서 요청을 처리할 수 없습니다."));
        }
    }

    @PutMapping("/instructor/img") // 강사의 프로필 이미지 업데이트, AWS Lambda에 의해 S3에 저장되고 이미지 경로가 반환됨
    public ResponseEntity<InstructorProfilePutResponse> putInsProfile(@CurrentUser UUID uuid, @Valid @RequestBody InstructorImageUpdateRequest img){
        try{
            InstructorProfilePutResponse response = ins_Service.updateInstructorProfile(uuid, img);
            return ResponseEntity.status(HttpStatus.OK)
                .body(response);
        }catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new InstructorProfilePutResponse("UNAUTHORIZED","402","인증되지 않은 사용자입니다."));
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new InstructorProfilePutResponse("INTERNAL SERVER ERROR","500","서버에서 요청을 처리할 수 없습니다."));
        }
    }

    
}