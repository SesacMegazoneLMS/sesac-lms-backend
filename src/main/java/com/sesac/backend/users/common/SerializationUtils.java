package com.sesac.backend.users.common;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SerializationUtils {

    // List<String> -> String (리스트 객체를 쉼표로 구분된 문자열로 직렬화)
    public static String serializeTechStack(List<String> techStack) {
        return String.join(",", techStack);
    }

    // String -> List<String> (쉼표로 구분된 문자열을 리스트 형태로 역직렬화)
    public static List<String> deserializeTechStack(String techStack) {
        if (techStack == null || techStack.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(techStack.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }
}