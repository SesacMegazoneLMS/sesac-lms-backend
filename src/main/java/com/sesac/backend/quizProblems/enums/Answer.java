package com.sesac.backend.quizProblems.enums;

public enum Answer {
    NOT_SELECTED(-2), ALL(-1), FIRST(0), SECOND(1), THIRD(2), FOURTH(3), FIFTH(4);

    private final Integer index;

    Answer(Integer index) {
        this.index = index;
    }
}
