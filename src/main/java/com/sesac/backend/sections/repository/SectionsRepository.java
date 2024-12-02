package com.sesac.backend.sections.repository;

import com.sesac.backend.sections.domain.Sections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionsRepository extends JpaRepository<Sections, Long> {

}
