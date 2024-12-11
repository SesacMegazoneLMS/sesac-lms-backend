package com.sesac.backend.users.dto;

import com.sesac.backend.users.domain.User;
import com.sesac.backend.users.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    private String nickname; // 닉네임(이름)
    private String email;
    private UserType userType;
    private String phoneNumber;
    private String address;

    public UserProfile(User user){
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.userType = user.getUserType();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
    }
}
