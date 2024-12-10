package com.sesac.backend.users.common;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public abstract class CommonResponse {
    String statusCode;
    String errorCode;
    String message;
}
