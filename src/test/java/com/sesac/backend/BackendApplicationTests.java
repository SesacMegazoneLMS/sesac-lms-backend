package com.sesac.backend;

import com.sesac.backend.auths.utils.JwtAuthenticationFilter;
import com.sesac.backend.auths.utils.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@AutoConfigureMockMvc
class BackendApplicationTests {

    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Test
    void contextLoads() {
    }
}
