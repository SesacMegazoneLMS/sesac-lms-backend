package com.sesac.backend.sections.service;

import com.sesac.backend.sections.repository.SectionsRepository;
import org.springframework.stereotype.Service;

@Service
public class SectionsService {

    private final SectionsRepository sectionsRepository;

    public SectionsService(SectionsRepository sectionsRepository) {
        this.sectionsRepository = sectionsRepository;
    }
}
