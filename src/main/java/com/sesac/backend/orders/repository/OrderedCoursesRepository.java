package com.sesac.backend.orders.repository;

import com.sesac.backend.orders.domain.OrderedCourses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderedCoursesRepository extends JpaRepository<OrderedCourses, Long> {
}
