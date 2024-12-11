package com.sesac.backend.users.dto.response;

import com.sesac.backend.users.common.CommonResponse;
import com.sesac.backend.users.dto.InstructorProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class InstructorProfileGetResponse extends CommonResponse {
    private InstructorProfile profile; // 강사의 세부 프로필 객체를 응답에 포함

    public InstructorProfileGetResponse(String statusCode, String errorCode, String message, InstructorProfile profile) {
        super(statusCode, errorCode, message);
        this.profile = profile;
    }
}
