package com.sesac.backend.users.dto.response;

import com.sesac.backend.users.common.CommonResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class InstructorProfilePutResponse extends CommonResponse {
// 업데이트된 정보를 담을 수 있는 필드 추가

    public InstructorProfilePutResponse(String statusCode, String errorCode, String message) {
        super(statusCode, errorCode, message); // 상위 클래스 생성자 호출
    }
}
