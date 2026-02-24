package qtedu.Impact_design.docs.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import qtedu.Impact_design.api.config.SecurityConfig;
import qtedu.Impact_design.api.controller.AuthController;
import qtedu.Impact_design.api.util.security.JwtAuthenticationFilter;
import qtedu.Impact_design.api.util.security.UserArgumentResolver;
import qtedu.Impact_design.common.error.AuthorizationException;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.api.dto.response.auth.TeamInfoResponse;
import qtedu.Impact_design.docs.RestDocsTestSupport;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.RefreshToken;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.model.en.UserRole;
import qtedu.Impact_design.domain.service.AuthService;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        ),
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = UserArgumentResolver.class
        )
)
class AuthControllerTest extends RestDocsTestSupport {

    @MockitoBean
    private AuthService authService;

    @Test
    @DisplayName("학생 로그인")
    void login() throws Exception {
        RefreshToken refreshToken = RefreshToken.of("refresh-token-example", LocalDateTime.now().plusDays(7));
        JwtToken jwtToken = JwtToken.of("access-token-example", refreshToken);
        given(authService.login(anyString(), anyString())).willReturn(jwtToken);

        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("loginId", "a1001", "password", "1234")
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(document("auth/login",
                        requestFields(
                                fieldWithPath("loginId").description("로그인 ID"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.accessToken").description("액세스 토큰"),
                                fieldWithPath("data.refreshToken").description("리프레시 토큰"),
                                fieldWithPath("data.userRole").description("사용자 역할 (학생 로그인 시 null)").optional()
                        )
                ));
    }

    @Test
    @DisplayName("학생 로그인 - 비밀번호 틀림")
    void login_wrongPassword() throws Exception {
        given(authService.login(anyString(), anyString()))
                .willThrow(new AuthorizationException(ErrorCode.WRONG_PASSWORD));

        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("loginId", "a1001", "password", "wrong")
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andDo(document("auth/login-wrong-password",
                        responseFields(
                                fieldWithPath("status").description("401"),
                                fieldWithPath("data.errorCode").description("AUTH_4"),
                                fieldWithPath("data.message").description("비밀번호가 틀렸습니다.")
                        )
                ));
    }

    @Test
    @DisplayName("학생 로그인 - 사용자 없음")
    void login_userNotFound() throws Exception {
        given(authService.login(anyString(), anyString()))
                .willThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND));

        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("loginId", "unknown", "password", "1234")
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andDo(document("auth/login-user-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("USER_1"),
                                fieldWithPath("data.message").description("회원을 찾을 수 없음.")
                        )
                ));
    }

    @Test
    @DisplayName("학생 로그인 - 권한 없음")
    void login_accessDenied() throws Exception {
        given(authService.login(anyString(), anyString()))
                .willThrow(new AuthorizationException(ErrorCode.ACCESS_DENIED));

        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("loginId", "teacher01", "password", "1234")
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andDo(document("auth/login-access-denied",
                        responseFields(
                                fieldWithPath("status").description("401"),
                                fieldWithPath("data.errorCode").description("AUTH_5"),
                                fieldWithPath("data.message").description("권한이 없습니다.")
                        )
                ));
    }

    @Test
    @DisplayName("코드 확인")
    void checkCode() throws Exception {
        given(authService.checkCode(anyString())).willReturn(java.util.List.of(
                TeamInfoResponse.builder().teamId(1).name("1팀").build(),
                TeamInfoResponse.builder().teamId(2).name("2팀").build()
        ));

        mockMvc.perform(get("/api/auth/check-code")
                        .param("code", "ABC123"))
                .andExpect(status().isOk())
                .andDo(document("auth/check-code",
                        queryParameters(
                                parameterWithName("code").description("참여 코드")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data[].teamId").description("팀 ID"),
                                fieldWithPath("data[].name").description("팀 이름")
                        )
                ));
    }

    @Test
    @DisplayName("코드 확인 - 존재하지 않는 코드")
    void checkCode_notFound() throws Exception {
        given(authService.checkCode(anyString()))
                .willThrow(new NotFoundException(ErrorCode.CODE_NOT_FOUND));

        mockMvc.perform(get("/api/auth/check-code")
                        .param("code", "INVALID"))
                .andExpect(status().isNotFound())
                .andDo(document("auth/check-code-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("TEAM_1"),
                                fieldWithPath("data.message").description("코드가 존재하지 않습니다.")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 ID 중복 확인")
    void checkLoginId() throws Exception {
        mockMvc.perform(get("/api/auth/check-id")
                        .param("loginId", "a1001"))
                .andExpect(status().isOk())
                .andDo(document("auth/check-id",
                        queryParameters(
                                parameterWithName("loginId").description("확인할 로그인 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 ID 중복 확인 - 이미 존재하는 ID")
    void checkLoginId_duplicate() throws Exception {
        willThrow(new ConflictException(ErrorCode.USER_ALREADY_CREATED))
                .given(authService).checkLoginIdDuplicate(anyString());

        mockMvc.perform(get("/api/auth/check-id")
                        .param("loginId", "a1001"))
                .andExpect(status().isConflict())
                .andDo(document("auth/check-id-duplicate",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("USER_3"),
                                fieldWithPath("data.message").description("이미 가입된 사용자입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("강사 로그인")
    void teacherLogin() throws Exception {
        RefreshToken refreshToken = RefreshToken.of("refresh-token-example", LocalDateTime.now().plusDays(7));
        JwtToken jwtToken = JwtToken.of("access-token-example", refreshToken);
        given(authService.teacherLogin(anyString(), anyString()))
                .willReturn(Pair.of(jwtToken, UserRole.TEACHER));

        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("loginId", "teacher01", "password", "1234")
        );

        mockMvc.perform(post("/api/auth/teacher/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(document("auth/teacher-login",
                        requestFields(
                                fieldWithPath("loginId").description("로그인 ID"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.accessToken").description("액세스 토큰"),
                                fieldWithPath("data.refreshToken").description("리프레시 토큰"),
                                fieldWithPath("data.userRole").description("사용자 역할 (TEACHER, ADMIN)")
                        )
                ));
    }

    @Test
    @DisplayName("강사 로그인 - 비밀번호 틀림")
    void teacherLogin_wrongPassword() throws Exception {
        given(authService.teacherLogin(anyString(), anyString()))
                .willThrow(new AuthorizationException(ErrorCode.WRONG_PASSWORD));

        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("loginId", "teacher01", "password", "wrong")
        );

        mockMvc.perform(post("/api/auth/teacher/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andDo(document("auth/teacher-login-wrong-password",
                        responseFields(
                                fieldWithPath("status").description("401"),
                                fieldWithPath("data.errorCode").description("AUTH_4"),
                                fieldWithPath("data.message").description("비밀번호가 틀렸습니다.")
                        )
                ));
    }

    @Test
    @DisplayName("강사 로그인 - 사용자 없음")
    void teacherLogin_userNotFound() throws Exception {
        given(authService.teacherLogin(anyString(), anyString()))
                .willThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND));

        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("loginId", "unknown", "password", "1234")
        );

        mockMvc.perform(post("/api/auth/teacher/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andDo(document("auth/teacher-login-user-not-found",
                        responseFields(
                                fieldWithPath("status").description("404"),
                                fieldWithPath("data.errorCode").description("USER_1"),
                                fieldWithPath("data.message").description("회원을 찾을 수 없음.")
                        )
                ));
    }

    @Test
    @DisplayName("강사 로그인 - 권한 없음")
    void teacherLogin_accessDenied() throws Exception {
        given(authService.teacherLogin(anyString(), anyString()))
                .willThrow(new AuthorizationException(ErrorCode.ACCESS_DENIED));

        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of("loginId", "a1001", "password", "1234")
        );

        mockMvc.perform(post("/api/auth/teacher/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andDo(document("auth/teacher-login-access-denied",
                        responseFields(
                                fieldWithPath("status").description("401"),
                                fieldWithPath("data.errorCode").description("AUTH_5"),
                                fieldWithPath("data.message").description("권한이 없습니다.")
                        )
                ));
    }

    @Test
    @DisplayName("로그아웃")
    void logout() throws Exception {
        authenticateAs(1L);

        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isOk())
                .andDo(document("auth/logout",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공 메시지")
                        )
                ));
    }

    @Test
    @DisplayName("회원가입")
    void signup() throws Exception {
        String requestBody = objectMapper.writeValueAsString(
                java.util.Map.of(
                        "loginId", "a1001",
                        "password", "1234",
                        "code", "ABC123",
                        "teamId", 1
                )
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(document("auth/signup",
                        requestFields(
                                fieldWithPath("loginId").description("로그인 ID"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("code").description("참여 코드"),
                                fieldWithPath("teamId").description("팀 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.message").description("성공 메시지")
                        )
                ));
    }
}
