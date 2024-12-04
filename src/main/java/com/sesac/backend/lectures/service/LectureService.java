package com.sesac.backend.lectures.service;

import com.sesac.backend.lectures.repository.LectureRepository;
import org.springframework.stereotype.Service;

@Service
public class LectureService {

    private final LectureRepository lectureRepository;

    public LectureService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

}
