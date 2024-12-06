package com.sesac.backend.courses.enums;

public enum Category {
    PROGRAMMING("PROGRAMMING"),
    FRONTEND("FRONTEND"),
    BACKEND("BACKEND"),
    AI("AI");

    private String value;
    
    Category(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static Category fromValue(String value) {
        for (Category category : Category.values()) {
            if(category.getValue().equals(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다.");
    }
}
