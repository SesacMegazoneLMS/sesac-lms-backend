package com.sesac.backend.users.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class CommonResponse {
    String statusCode;
    String errorCode;
    String message;
}
