package com.sesac.backend.auths.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sesac.backend.auths.constants.SecurityConstants;
import com.sesac.backend.auths.dto.JwkKey;
import com.sesac.backend.auths.dto.JwksResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final ObjectMapper objectMapper;

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    @Value("${aws.cognito.region}")
    private String region;

    private RSAPublicKey publicKey;

    @PostConstruct
    public void init() {
        try {
            String jwksUrl = String.format("https://cognito-idp.%s.amazonaws.com/%s/.well-known/jwks.json",
                region, userPoolId);
            publicKey = loadPublicKeyFromJwks(jwksUrl);
        } catch (Exception e) {
            log.error("Failed to load public key", e);
            throw new RuntimeException("Failed to load public key", e);
        }
    }

    private RSAPublicKey loadPublicKeyFromJwks(String jwksUrl) throws Exception {
        URL url = new URL(jwksUrl);
        JwksResponse jwksResponse = objectMapper.readValue(url, JwksResponse.class);

        if (jwksResponse.getKeys().isEmpty()) {
            throw new RuntimeException("No keys found in JWKS response");
        }

        // 첫 번째 키 사용
        JwkKey publicKeyData = jwksResponse.getKeys().get(0);

        // RSA 공개키 생성
        BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(publicKeyData.getN()));
        BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(publicKeyData.getE()));

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory factory = KeyFactory.getInstance("RSA");

        return (RSAPublicKey) factory.generatePublic(spec);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    // 토큰에서 인증 정보 추출
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .build()
            .parseClaimsJws(token)
            .getBody();

        String username = claims.get(SecurityConstants.KEY_USERNAME, String.class);
        String userType = claims.get(SecurityConstants.KEY_USER_TYPE, String.class);

        // 기본 권한 설정
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + userType.toUpperCase()));

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}