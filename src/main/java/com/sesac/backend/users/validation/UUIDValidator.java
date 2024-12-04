package com.sesac.backend.users.validation;

import com.sesac.backend.users.anotation.ValidUUID;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;

public class UUIDValidator implements ConstraintValidator<ValidUUID, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false; // null 또는 빈 문자열은 유효하지 않음
        }
        try {
            UUID.fromString(value); // UUID 형식 검증
            return true;
        } catch (IllegalArgumentException e) {
            return false; // 변환 실패 시 유효하지 않은 형식
        }
    }
}