package com.sesac.backend.courses.enums;

public enum Level {
    BASIC("초급"), MEDIUM("중급"), HIGH("고급");

    private final String level;

    Level(String level) {
        this.level = level;
    }
}
