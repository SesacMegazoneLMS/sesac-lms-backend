package com.sesac.backend.auths.controller;

import com.sesac.backend.audit.CurrentUser;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/user-info")
@RestController
public class AuthsController {

    @GetMapping("/my-page")
    public ResponseEntity<Map<String, String>> getMyPage(Authentication auth, @CurrentUser UUID uuid) {
        try {
            log.error(uuid.toString() + "*********************************");
            log.error(SecurityContextHolder.getContext().getAuthentication().getName() + "=========================");
            return ResponseEntity.ok(Map.of(
                "name", auth.getName(),
                "custom:userType", String.valueOf(auth.getAuthorities()),
                "full-auth", String.valueOf(auth)
            ));
        } catch (RuntimeException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
