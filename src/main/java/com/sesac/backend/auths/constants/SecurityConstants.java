package com.sesac.backend.auths.constants;

public class SecurityConstants {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/auth/**";

    public static final String KEY_USERNAME = "cognito:username";
    public static final String KEY_USER_TYPE = "custom:userType";
}