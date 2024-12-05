package com.sesac.backend.audit;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@Slf4j
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class) &&
            parameter.getParameterType().equals(UUID.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 디버깅을 위한 로그 추가
        log.debug("Authentication: {}", authentication);
        if (authentication != null) {
            log.debug("Authentication name: {}", authentication.getName());
            log.debug("Authentication principal: {}", authentication.getPrincipal());
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("사용자 인증 정보가 없습니다.");
        }

        try {
            return UUID.fromString(authentication.getName());
        } catch (IllegalArgumentException e) {
            log.error("UUID 변환 실패: {}", authentication.getName());
            throw new IllegalStateException("잘못된 사용자 ID 형식입니다.");
        }
    }
}