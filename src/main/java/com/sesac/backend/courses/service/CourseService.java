package com.sesac.backend.courses.service;

import com.sesac.backend.audit.CurrentUser;
import com.sesac.backend.courses.repository.CourseRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }
}
