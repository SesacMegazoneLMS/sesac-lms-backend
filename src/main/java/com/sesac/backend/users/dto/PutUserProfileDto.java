package com.sesac.backend.users.dto;

import java.util.UUID;

import com.sesac.backend.users.annotation.ValidUUID;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PutUserProfileDto {

    @NotNull
    @NotEmpty
    private String address;
    @NotNull
    @NotEmpty
    private String name;
    @NotNull
    @NotEmpty
    private String phone_number;
}
