package com.sesac.backend.orders.repository;

import com.sesac.backend.orders.domain.OrderedCourses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderedCoursesRepository extends JpaRepository<OrderedCourses, Long> {

    List<OrderedCourses> findAllByCourseId(Long courseId);

    @Query("SELECT SUM(e.price) FROM OrderedCourses e WHERE e.id IN :ids")
    BigDecimal sumPriceByIds(@Param("ids") List<Long> ids);

}
