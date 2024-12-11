package com.sesac.backend.users.dto.request;

import com.sesac.backend.users.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileUpdateRequest {
    private String nickname; // 닉네임(이름)
    private String phoneNumber;
    private String address;
}
