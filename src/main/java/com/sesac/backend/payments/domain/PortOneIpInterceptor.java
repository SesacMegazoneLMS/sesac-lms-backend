package com.sesac.backend.payments.domain;

import com.sesac.backend.payments.annotation.PortOneIpOnly;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

// 인터셉터(Interceptor): 컨트롤러에 들어오는 요청을 가로채서 전/후 처리를 할 수 있는 컴포넌트
// 클라이언트 → [Filter] → [Interceptor] → [Controller] → [Service] → ...
// 전체 흐름: 포트원 서버(52.78.100.19)에서 요청 → AWS ALB가 요청을 받음 → ALB가 X-Forwarded-For 헤더 추가 → 요청이 우리 서버에 도달
// → 인터셉터가 실행됨 → IP 체크 후 요청 허용/거부
@Component
@Slf4j
public class PortOneIpInterceptor implements HandlerInterceptor {

    private final Set<String> allowedIps;

    public PortOneIpInterceptor(@Value("${portone.allowed-ips}") String allowedIpsString) {
        this.allowedIps = Arrays.stream(allowedIpsString.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        log.info("🔧 Initialized PortOne allowed IPs: {}", allowedIps);
    }

    // 클라이언트 요청 → preHandle() → Controller → postHandle() → afterCompletion()
    // preHandle: 컨트롤러 실행 전
    // postHandle: 컨트롤러 실행 후
    // afterCompletion: 뷰 렌더링 후
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 클라이언트의 요청 정보, 응답을 위한 객체, 실행될 컨트롤러 메서드 정보

        // 1. 핸들러 타입 체크
        if (!(handler instanceof HandlerMethod)) {
            return true; // 정적 리소스 등은 그냥 통과
        }

        // 2. 핸들러 메서드 변환
        // HandlerMethod: 실행될 컨트롤러 메서드의 정보를 담고 있는 객체(메서드의 파라미터, 어노테이션, 반환타입 등)
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // 3. 어노테이션 체크
        if (!handlerMethod.hasMethodAnnotation(PortOneIpOnly.class)) {
            return true; // @PortOneIpOnly 없으면 통과
        }

        // 4. IP 체크
        String clientIp = getClientIp(request);

        // 상세 로깅
        log.info("💰 Payment Request Details:");
        log.info("💰 Client IP: {}", clientIp);
        log.info("💰 X-Forwarded-For: {}", request.getHeader("X-Forwarded-For"));
        log.info("💰 Remote Address: {}", request.getRemoteAddr());
        log.info("💰 Request URI: {}", request.getRequestURI());
        log.info("💰 HTTP Method: {}", request.getMethod());

        if (!allowedIps.contains(clientIp)) {
            log.warn("🚫 Unauthorized IP attempt - IP: {}, URI: {}",
                    clientIp, request.getRequestURI());
            // 5. 거부 응답 설정
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().println("Access denied: Invalid IP address: " + clientIp);
            return false; // 요청 중단
        }

        log.info("✅ Authorized PortOne request - IP: {}, URI: {}",
                clientIp, request.getRequestURI());

        return true; // 요청 진행
    }

    // AWS, CloudFlare 등의 로드밸런서나 프록시를 사용하는 경우 Client(여기서는 PortOne)의 실제 IP를 알 수 없음
    // 실제 클라이언트 IP를 알기 위해 프록시들은 X-Forwarded-For 헤더를 사용
    // X-Forwarded-For 헤더 값: "123.123.123.123, 10.0.0.1, 10.0.0.2" → 맨 왼쪽이 원래 클라이언트 IP, 오른쪽으로 갈수록 거쳐온 프록시 서버들의 IP
    private String getClientIp(HttpServletRequest request) {
        // 1. X-Forwarded-For 헤더 값을 가져옴
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        // 2. 헤더가 존재하면(프록시를 통한 요청)
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // 3. 첫 번째 IP(실제 클라이언트 IP)만 추출
            return xForwardedFor.split(",")[0].trim();
        }
        // 4. 헤더가 없으면(직접 연결) getRemoteAddr() 사용
        return request.getRemoteAddr();
    }
}
