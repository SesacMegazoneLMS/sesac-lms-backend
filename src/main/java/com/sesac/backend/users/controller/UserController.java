package com.sesac.backend.users.controller;

import java.util.UUID;

import com.sesac.backend.audit.CurrentUser;
import com.sesac.backend.users.dto.request.UpdateProfileImg;
import com.sesac.backend.users.dto.request.UpdateProfileInfo;
import com.sesac.backend.users.dto.response.GetUserProfileResponse;
import com.sesac.backend.users.dto.response.PutProfileResponse;
import com.sesac.backend.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<GetUserProfileResponse> getUserProfile(@CurrentUser UUID uuid) {
        try {
            userService.checkUserValidate(uuid);
            return ResponseEntity.status(HttpStatus.OK).body(userService.getUserProfile(uuid));
        }catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new GetUserProfileResponse("UNAUTHORIZED","402","인증되지 않은 사용자입니다.",null));
        }catch(Exception e) {
            return ResponseEntity.internalServerError()
                .body(new GetUserProfileResponse("INTERNAL SERVER ERROR","500","서버에서 요청을 처리할 수 없습니다.",null));
        }
    }

    @PutMapping("/profile/img")
    public ResponseEntity<PutProfileResponse> putUserProfile(@CurrentUser UUID uuid, UpdateProfileImg img){
        try{
            userService.checkUserValidate(uuid);
            PutProfileResponse response = userService.updateUserProfile(uuid, img);
            return ResponseEntity.status(HttpStatus.OK)
                .body(response);
        }catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new PutProfileResponse("UNAUTHORIZED","402","인증되지 않은 사용자입니다."));
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new PutProfileResponse("INTERNAL SERVER ERROR","500","서버에서 요청을 처리할 수 없습니다."));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<PutProfileResponse> updateUserProfile(@CurrentUser UUID uuid, UpdateProfileInfo info ) {
        try{
            userService.checkUserValidate(uuid);
            return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserProfile(uuid, info));
        }catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new PutProfileResponse("UNAUTHORIZED","402","인증되지 않은 사용자입니다."));
        }catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new PutProfileResponse("INTERNAL SERVER ERROR","500","서버에서 요청을 처리할 수 없습니다."));
        }
    }
}