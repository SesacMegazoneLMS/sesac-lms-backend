package com.sesac.backend.auths.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwkKey {
    private String kid;    // Key ID
    private String alg;    // Algorithm
    private String kty;    // Key Type
    private String e;      // Exponent
    private String n;      // Modulus
    private String use;    // Use
}