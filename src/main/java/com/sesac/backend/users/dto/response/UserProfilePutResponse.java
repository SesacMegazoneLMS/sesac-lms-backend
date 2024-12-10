package com.sesac.backend.users.dto.response;

import com.sesac.backend.users.common.CommonResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class UserProfilePutResponse extends CommonResponse {
    public UserProfilePutResponse(String statusCode, String errorCode, String message) {
        super(statusCode, errorCode, message); // 상위 클래스 생성자 호출
    }
}
