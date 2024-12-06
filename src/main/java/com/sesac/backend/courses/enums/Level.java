package com.sesac.backend.courses.enums;

import java.util.Arrays;

public enum Level {
    BASIC("초급"), MEDIUM("중급"), HIGH("고급");

    private final String level;

    Level(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public static Level from(String level) {
        return Arrays.stream(Level.values())
                .filter(l -> l.level.equals(level))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(level + " is not a valid level"));
    }
}
