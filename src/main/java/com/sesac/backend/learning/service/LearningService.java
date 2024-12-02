package com.sesac.backend.learning.service;

import com.sesac.backend.learning.repository.LearningRepository;
import org.springframework.stereotype.Service;

@Service
public class LearningService {

    private final LearningRepository learningRepository;

    public LearningService(LearningRepository learningRepository) {
        this.learningRepository = learningRepository;
    }
}
