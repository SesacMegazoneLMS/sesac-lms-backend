package com.sesac.backend.users.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetUserProfileResponse {
    private String statusCode;
    private String errorCode;
    private String message;
    private UserProfile profile;
}
