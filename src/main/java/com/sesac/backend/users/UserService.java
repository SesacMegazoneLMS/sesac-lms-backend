package com.sesac.backend.users;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class UserService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public UserService(@Value("${api.user-service}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }

    // 단일 사용자 조회
    public UserDto getUser(String userId) {
        String url = baseUrl + "/users/" + userId;
        return restTemplate.getForObject(url, UserDto.class);
    }

    // 전체 사용자 목록 조회
    public List<UserDto> getAllUsers() {
        String url = baseUrl + "/users";
        // List로 받기 위해 ParameterizedTypeReference 사용
        ResponseEntity<List<UserDto>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<UserDto>>() {
            }
        );
        return response.getBody();
    }
}