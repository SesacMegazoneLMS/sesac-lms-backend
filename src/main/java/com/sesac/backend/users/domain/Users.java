package com.sesac.backend.users.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Users {

    @Id
    private long id;
    private UUID user_id;

    private String email;
    private String address;
    private String name;
    private String phone_number;
    private String created_at;
    private String updated_at;
    private String userType;
}
