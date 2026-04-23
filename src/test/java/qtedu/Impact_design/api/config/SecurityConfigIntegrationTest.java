package qtedu.Impact_design.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import qtedu.Impact_design.api.util.GlobalExceptionHandler;
import qtedu.Impact_design.api.util.security.JwtAuthenticationEntryPoint;
import qtedu.Impact_design.api.util.security.JwtAuthenticationFilter;
import qtedu.Impact_design.api.util.security.JwtTokenUtil;
import qtedu.Impact_design.api.util.security.SilentAccessDeniedHandler;
import qtedu.Impact_design.domain.model.UserId;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityConfigIntegrationTest.ProbeController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtAuthenticationEntryPoint.class,
        SilentAccessDeniedHandler.class,
        JwtTokenUtil.class,
        GlobalExceptionHandler.class
})
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-for-security-config-integration-at-least-64-bytes-1234567890",
        "jwt.access-expiration=3600000",
        "jwt.refresh-expiration=604800000",
        "cors.allowed-origins=http://localhost:3000"
})
class SecurityConfigIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Test
    @DisplayName("permitAll 경로(/api/auth/login)는 토큰 없이도 200을 반환한다")
    void permitAllPathWithoutToken() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("health 엔드포인트는 토큰 없이 200을 반환한다")
    void healthWithoutToken() throws Exception {
        mockMvc.perform(get("/health"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("/api/** 보호 경로는 토큰이 없으면 401을 반환한다")
    void protectedPathWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/teach/ping"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("/api/** 보호 경로는 잘못된 토큰이면 401을 반환한다")
    void protectedPathWithInvalidTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/teach/ping")
                        .header("Authorization", "Bearer not.a.real.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("/api/** 보호 경로는 인증된 Authentication이 있으면 정상 응답한다")
    void protectedPathWithAuthenticationReturns200() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                UserId.of(1L), null, Collections.emptyList());

        mockMvc.perform(get("/api/teach/ping").with(authentication(auth)))
                .andExpect(status().isOk());
    }

    @RestController
    static class ProbeController {

        @GetMapping("/api/auth/login")
        String publicLogin() {
            return "ok";
        }

        @GetMapping("/health")
        String health() {
            return "ok";
        }

        @GetMapping("/api/teach/ping")
        String protectedEndpoint() {
            return "ok";
        }
    }
}
