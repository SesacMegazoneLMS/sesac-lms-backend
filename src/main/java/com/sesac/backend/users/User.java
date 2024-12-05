package com.sesac.backend.users;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID userId;

    private String email;

    private String address;

    private String name;

    private String phoneNumber;

    private String createdAt;

    private String updatedAt;

    private String userType;
}
