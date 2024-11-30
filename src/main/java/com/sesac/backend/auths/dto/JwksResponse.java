package com.sesac.backend.auths.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwksResponse {
    private List<JwkKey> keys;
}