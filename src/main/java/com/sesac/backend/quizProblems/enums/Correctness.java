package com.sesac.backend.quizProblems.enums;

import lombok.Getter;

@Getter
public enum Correctness {
    CORRECT(1), WRONG(0);

    private final Integer value;

    Correctness(Integer value) {
        this.value = value;
    }
}
