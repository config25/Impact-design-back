package qtedu.Impact_design.api.util.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import qtedu.Impact_design.common.error.AuthorizationException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.model.UserId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class JwtAuthenticationFilterTest {

    private static final String SECRET =
            "test-secret-key-for-jwt-auth-filter-test-must-be-at-least-64-bytes-long-1234567890";

    private JwtTokenUtil jwtTokenUtil;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        jwtTokenUtil = new JwtTokenUtil(SECRET, 3600000L, 604800000L);
        filter = new JwtAuthenticationFilter(jwtTokenUtil, mock(RequestMappingHandlerMapping.class));
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("유효한 Bearer 토큰이 있으면 SecurityContext에 UserId를 세팅하고 다음 필터로 진행한다")
    void setsAuthenticationForValidToken() throws Exception {
        String token = jwtTokenUtil.createAccessToken(UserId.of(7L));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/teach/index");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isInstanceOf(UserId.class);
        assertThat(((UserId) auth.getPrincipal()).getId()).isEqualTo(7L);
        assertThat(request.getAttribute("userId")).isEqualTo(UserId.of(7L));
        assertThat(request.getAttribute("Exception")).isNull();
        assertThat(chain.getRequest()).isSameAs(request);
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 SecurityContext를 비우고 예외를 request attribute로 전달한다")
    void missingAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/teach/index");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        Object attr = request.getAttribute("Exception");
        assertThat(attr).isInstanceOf(AuthorizationException.class);
        assertThat(((AuthorizationException) attr).getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
        assertThat(chain.getRequest()).isSameAs(request);
    }

    @Test
    @DisplayName("만료된 토큰이면 TOKEN_EXPIRED 예외를 attribute로 전달하고 SecurityContext를 비운다")
    void expiredToken() throws Exception {
        JwtTokenUtil expiredUtil = new JwtTokenUtil(SECRET, 0L, 0L);
        String expired = expiredUtil.createAccessToken(UserId.of(1L));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/teach/index");
        request.addHeader("Authorization", "Bearer " + expired);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        Object attr = request.getAttribute("Exception");
        assertThat(attr).isInstanceOf(AuthorizationException.class);
        assertThat(((AuthorizationException) attr).getErrorCode()).isEqualTo(ErrorCode.TOKEN_EXPIRED);
    }

    @Test
    @DisplayName("서명이 다른 토큰이면 INVALID_TOKEN 예외를 attribute로 전달한다")
    void tamperedToken() throws Exception {
        String otherSecret = "another-secret-key-that-is-also-long-enough-for-hs512-xxxxxxxxxxxxxxxxxxxxx";
        String forged = new JwtTokenUtil(otherSecret, 3600000L, 604800000L)
                .createAccessToken(UserId.of(1L));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/teach/index");
        request.addHeader("Authorization", "Bearer " + forged);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        Object attr = request.getAttribute("Exception");
        assertThat(attr).isInstanceOf(AuthorizationException.class);
        assertThat(((AuthorizationException) attr).getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("shouldNotFilter 경로(/api/auth/login 등)는 토큰 없어도 통과하고 Authentication이 세팅되지 않는다")
    void skipsAuthForPublicPaths() throws Exception {
        String[] publicPaths = {
                "/api/auth/login",
                "/api/auth/signup",
                "/api/auth/admin/login",
                "/api/auth/encode",
                "/api/auth/create/account",
                "/docs/index.html",
                "/health",
                "/swagger-ui.html",
                "/swagger-ui/index.html",
                "/v3/api-docs/swagger-config"
        };

        for (String path : publicPaths) {
            SecurityContextHolder.clearContext();
            MockHttpServletRequest request = new MockHttpServletRequest("GET", path);
            MockHttpServletResponse response = new MockHttpServletResponse();
            MockFilterChain chain = new MockFilterChain();

            filter.doFilter(request, response, chain);

            assertThat(SecurityContextHolder.getContext().getAuthentication())
                    .as("public path must not set authentication: %s", path)
                    .isNull();
            assertThat(request.getAttribute("Exception"))
                    .as("public path must not record exception: %s", path)
                    .isNull();
            assertThat(chain.getRequest())
                    .as("public path must reach next filter: %s", path)
                    .isSameAs(request);
        }
    }

    @Test
    @DisplayName("Bearer 접두사가 없는 Authorization 헤더는 토큰 없음으로 처리한다")
    void nonBearerAuthorizationHeader() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/teach/index");
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        Object attr = request.getAttribute("Exception");
        assertThat(attr).isInstanceOf(AuthorizationException.class);
        assertThat(((AuthorizationException) attr).getErrorCode()).isEqualTo(ErrorCode.INVALID_TOKEN);
    }
}
