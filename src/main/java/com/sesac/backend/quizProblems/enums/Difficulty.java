package com.sesac.backend.quizProblems.enums;

import lombok.Getter;

@Getter
public enum Difficulty {
    EASY(1), BASIC(2), NORMAL(3), HARD(4);

    private final Integer point;

    Difficulty(Integer point) {
        this.point = point;
    }
}
