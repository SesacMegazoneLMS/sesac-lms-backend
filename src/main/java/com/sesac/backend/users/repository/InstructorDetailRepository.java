package com.sesac.backend.users.repository;

import com.sesac.backend.users.domain.InstructorDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorDetailRepository extends JpaRepository<InstructorDetail, Long> {
    InstructorDetail findByUser_id(Long id);
}
