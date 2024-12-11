package com.sesac.backend.users.domain;

import com.sesac.backend.audit.BaseEntity;
import com.sesac.backend.reviews.domain.Review;
import com.sesac.backend.users.enums.UserType;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name="users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private UUID uuid;

    @Column(unique = true)
    private String nickname; // 닉네임(이름)

    @Column(updatable = false)
    private String email;

    @Column(updatable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    private String phoneNumber;

    private String address;

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>(); // User가 작성한 리뷰
}