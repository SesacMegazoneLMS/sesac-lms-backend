package com.sesac.backend.courses.service;

import com.sesac.backend.courses.repository.CoursesRepository;
import org.springframework.stereotype.Service;

@Service
public class CoursesService {

    private final CoursesRepository coursesRepository;

    public CoursesService(CoursesRepository coursesRepository) {
        this.coursesRepository = coursesRepository;
    }
}
