package com.sesac.backend.users;

import java.util.List;
import java.util.UUID;

import com.sesac.backend.auths.constants.SecurityConstants;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
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
    public ResponseEntity<UserDto> getUserProfile() {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
        try {
            UserDto user = userService.getUser(userId);
            return ResponseEntity.ok(user);
        }catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    @PutMapping("/profile")
    public ResponseEntity<UserDto> updateUserProfile(UserDto user) {
        try {
            UUID userId = UUID.fromString(
                    SecurityContextHolder.getContext().getAuthentication().getName());
            UserDto userprofile = userService.getUser(userId);
            return ResponseEntity.ok(userprofile);
        }catch(IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}