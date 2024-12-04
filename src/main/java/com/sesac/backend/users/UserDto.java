package com.sesac.backend.users;

import java.util.UUID;

import com.sesac.backend.users.anotation.ValidUUID;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class UserDto {

    @ValidUUID
    @Id
    private UUID user_id;
    @Email
    @NotNull
    @NotEmpty
    private String email;
    @NotNull
    @NotEmpty
    private String address;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String phone_number;
    @NotNull
    @NotEmpty
    private String created_at;
    @NotNull
    @NotEmpty
    private String updated_at;
    @NotNull
    @NotEmpty
    private String userType;
}
