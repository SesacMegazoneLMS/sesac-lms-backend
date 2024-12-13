package com.sesac.backend.auths.constants;

public class SecurityConstants {

    private SecurityConstants() {

    }

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public static final String AUTHS_API = "/auths/**";
    public static final String HEALTH_CHECK_URL = "/health-check";
    public static final String STUDENTS_API = "/students/**";
    public static final String INSTRUCTORS_API = "/instructors/**";

    public static final String KEY_USERNAME = "sub";
    public static final String KEY_USER_TYPE = "custom:userType";
}