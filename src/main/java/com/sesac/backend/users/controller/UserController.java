package com.sesac.backend.users.controller;

import java.util.UUID;

import com.sesac.backend.users.dto.PutUserProfileDto;
import com.sesac.backend.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<PutUserProfileDto> getUserProfile() {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        try {
            PutUserProfileDto user = userService.getUser(userId);
            return ResponseEntity.ok(user);
        }catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @PutMapping("/profile")
    public ResponseEntity<PutUserProfileDto> updateUserProfile(PutUserProfileDto user) {
        try {
            UUID userId = UUID.fromString(
                    SecurityContextHolder.getContext().getAuthentication().getName());
            PutUserProfileDto userprofile = userService.getUser(userId);
            return ResponseEntity.ok(userprofile);
        }catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}