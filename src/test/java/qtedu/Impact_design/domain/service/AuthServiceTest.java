package qtedu.Impact_design.domain.service;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.common.error.AuthorizationException;
import qtedu.Impact_design.common.error.NotFoundException;
import qtedu.Impact_design.domain.implementation.auth.*;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.RefreshToken;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.model.en.UserRole;
import qtedu.Impact_design.domain.model.user.UserinfoModel;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthReader authReader;
    @Mock
    private AuthValidator authValidator;
    @Mock
    private AuthGenerator authGenerator;
    @Mock
    private AuthAppender authAppender;
    @Mock
    private AuthRemover authRemover;

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("정상 로그인 시 JwtToken을 반환한다")
        void returnsJwtTokenOnSuccessfulLogin() {
            UserinfoModel student = UserinfoModel.builder()
                    .userId(1L).loginId("a11").password("encoded")
                    .userRole(UserRole.STUDENT).build();
            JwtToken expectedToken = JwtToken.of("access", new RefreshToken("refresh", LocalDateTime.now().plusDays(7)));

            given(authReader.findUserByLoginId("a11")).willReturn(student);
            willDoNothing().given(authValidator).validatePassword("password", "encoded");
            willDoNothing().given(authValidator).validateStudentRole(student);
            given(authGenerator.generateToken(student)).willReturn(expectedToken);

            JwtToken result = authService.login("a11", "password");

            assertThat(result.getAccessToken()).isEqualTo("access");
            then(authValidator).should().validatePassword("password", "encoded");
            then(authValidator).should().validateStudentRole(student);
            then(authAppender).should().saveLoggedIn(1L, expectedToken);
        }

        @Test
        @DisplayName("존재하지 않는 유저면 NotFoundException이 발생한다")
        void throwsWhenUserNotFound() {
            given(authReader.findUserByLoginId("unknown")).willThrow(NotFoundException.class);

            assertThatThrownBy(() -> authService.login("unknown", "password"))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("비밀번호가 틀리면 AuthorizationException이 발생한다")
        void throwsWhenWrongPassword() {
            UserinfoModel student = UserinfoModel.builder()
                    .userId(1L).loginId("a11").password("encoded")
                    .userRole(UserRole.STUDENT).build();

            given(authReader.findUserByLoginId("a11")).willReturn(student);
            willThrow(AuthorizationException.class).given(authValidator).validatePassword("wrong", "encoded");

            assertThatThrownBy(() -> authService.login("a11", "wrong"))
                    .isInstanceOf(AuthorizationException.class);
        }
    }

    @Nested
    @DisplayName("teacherLogin")
    class TeacherLogin {

        @Test
        @DisplayName("정상 교사 로그인 시 JwtToken과 UserRole을 반환한다")
        void returnsTokenAndRoleOnSuccess() {
            UserinfoModel teacher = UserinfoModel.builder()
                    .userId(2L).loginId("teacher1").password("encoded")
                    .userRole(UserRole.TEACHER).build();
            JwtToken expectedToken = JwtToken.of("access", new RefreshToken("refresh", LocalDateTime.now().plusDays(7)));

            given(authReader.findUserByLoginId("teacher1")).willReturn(teacher);
            willDoNothing().given(authValidator).validatePassword("password", "encoded");
            willDoNothing().given(authValidator).validateTeacherRole(teacher);
            given(authGenerator.generateToken(teacher)).willReturn(expectedToken);

            Pair<JwtToken, UserRole> result = authService.teacherLogin("teacher1", "password");

            assertThat(result.getLeft().getAccessToken()).isEqualTo("access");
            assertThat(result.getRight()).isEqualTo(UserRole.TEACHER);
            then(authAppender).should().saveLoggedIn(2L, expectedToken);
        }

        @Test
        @DisplayName("STUDENT가 교사 로그인을 시도하면 AuthorizationException이 발생한다")
        void throwsWhenStudentTriesTeacherLogin() {
            UserinfoModel student = UserinfoModel.builder()
                    .userId(1L).loginId("a11").password("encoded")
                    .userRole(UserRole.STUDENT).build();

            given(authReader.findUserByLoginId("a11")).willReturn(student);
            willDoNothing().given(authValidator).validatePassword("password", "encoded");
            willThrow(AuthorizationException.class).given(authValidator).validateTeacherRole(student);

            assertThatThrownBy(() -> authService.teacherLogin("a11", "password"))
                    .isInstanceOf(AuthorizationException.class);
        }
    }

    @Nested
    @DisplayName("logout")
    class Logout {

        @Test
        @DisplayName("정상 로그아웃 시 authRemover.logout이 호출된다")
        void callsRemoverOnLogout() {
            UserId userId = UserId.of(1L);

            authService.logout(userId);

            then(authRemover).should().logout(userId);
        }
    }

    @Nested
    @DisplayName("signup")
    class Signup {

        @Test
        @DisplayName("정상 회원가입 시 authAppender.signup이 호출된다")
        void callsAppenderOnSignup() {
            authService.signup("newUser", "password", "CODE1", 1);

            then(authAppender).should().signup("newUser", "password", "CODE1", 1);
        }
    }
}
