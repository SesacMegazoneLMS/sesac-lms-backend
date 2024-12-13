package com.sesac.backend.orders.repository;

import com.sesac.backend.orders.domain.OrderedCourses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderedCoursesRepository extends JpaRepository<OrderedCourses, Long> {
}
