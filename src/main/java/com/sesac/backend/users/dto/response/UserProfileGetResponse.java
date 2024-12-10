package com.sesac.backend.users.dto.response;

import com.sesac.backend.users.common.CommonResponse;
import com.sesac.backend.users.dto.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class UserProfileGetResponse extends CommonResponse {
    private UserProfile user;

    public UserProfileGetResponse(String statusCode, String errorCode, String message, UserProfile user) {
        super(statusCode, errorCode, message);
        this.user = user;
    }
}
