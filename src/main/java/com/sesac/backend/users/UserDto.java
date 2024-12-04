package com.sesac.backend.users;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDto {

    private UUID user_id;
    private String email;
    private String address;
    private String name;
    private String phone_number;
    private String created_at;
    private String updated_at;
    private String userType;
}
