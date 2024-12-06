package com.sesac.backend.users.repository;

import com.sesac.backend.users.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserProfileRepository extends JpaRepository<Users, UUID>{

}
