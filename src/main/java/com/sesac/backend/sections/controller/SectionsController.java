package com.sesac.backend.sections.controller;

import com.sesac.backend.sections.service.SectionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/sections")
@RestController
public class SectionsController {

    private final SectionsService sectionsService;

    public SectionsController(SectionsService sectionsService) {
        this.sectionsService = sectionsService;
    }
}
