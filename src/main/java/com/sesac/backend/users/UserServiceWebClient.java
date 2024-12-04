package com.sesac.backend.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserServiceWebClient {

    private final WebClient webClient;

    public UserServiceWebClient(@Value("${api.user-service}") String baseUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    // 단일 사용자 조회
    public Mono<UserDto> getUser(String userId) {
        return webClient.get()
            .uri("/users/{userId}", userId)
            .retrieve()
            .bodyToMono(UserDto.class)
            .doOnError(error -> log.error("Error fetching user: {}", error.getMessage()));
    }

    // 전체 사용자 조회
    public Flux<UserDto> getAllUsers() {
        return webClient.get()
            .uri("/users")
            .retrieve()
            .bodyToFlux(UserDto.class)
            .doOnError(error -> log.error("Error fetching users: {}", error.getMessage()));
    }
}
