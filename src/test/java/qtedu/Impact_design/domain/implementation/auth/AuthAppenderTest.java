package qtedu.Impact_design.domain.implementation.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.RefreshToken;
import qtedu.Impact_design.domain.model.auth.LoggedInModel;
import qtedu.Impact_design.domain.model.team.TeamUserModel;
import qtedu.Impact_design.domain.model.user.UserinfoModel;
import qtedu.Impact_design.domain.repository.auth.LoggedInRepository;
import qtedu.Impact_design.domain.repository.auth.TeamUserRepository;
import qtedu.Impact_design.domain.repository.user.UserinfoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuthAppenderTest {

    @InjectMocks
    private AuthAppender authAppender;

    @Mock private UserinfoRepository userinfoRepository;
    @Mock private TeamUserRepository teamUserRepository;
    @Mock private LoggedInRepository loggedInRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("signup")
    class Signup {

        @Test
        @DisplayName("회원가입 시 비밀번호를 인코딩하고 유저와 팀유저를 저장한다")
        void signupSuccess() {
            given(passwordEncoder.encode("rawPw")).willReturn("encodedPw");
            given(userinfoRepository.save(any())).willReturn(
                    UserinfoModel.builder().userId(1L).loginId("a11").build());

            authAppender.signup("a11", "rawPw", "ABC123", 5);

            ArgumentCaptor<UserinfoModel> userCaptor = ArgumentCaptor.forClass(UserinfoModel.class);
            then(userinfoRepository).should().save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPassword()).isEqualTo("encodedPw");
            assertThat(userCaptor.getValue().getLoginId()).isEqualTo("a11");
            assertThat(userCaptor.getValue().getCode()).isEqualTo("ABC123");

            ArgumentCaptor<TeamUserModel> teamCaptor = ArgumentCaptor.forClass(TeamUserModel.class);
            then(teamUserRepository).should().save(teamCaptor.capture());
            assertThat(teamCaptor.getValue().getUserId()).isEqualTo(1L);
            assertThat(teamCaptor.getValue().getTeamId()).isEqualTo(5);
        }

        @Test
        @DisplayName("생성된 UserinfoModel은 STUDENT 역할과 writer=0이다")
        void studentDefaults() {
            given(passwordEncoder.encode("pw")).willReturn("enc");
            given(userinfoRepository.save(any())).willReturn(
                    UserinfoModel.builder().userId(1L).build());

            authAppender.signup("b11", "pw", "CODE", 3);

            ArgumentCaptor<UserinfoModel> captor = ArgumentCaptor.forClass(UserinfoModel.class);
            then(userinfoRepository).should().save(captor.capture());
            UserinfoModel saved = captor.getValue();
            assertThat(saved.getUserRole()).isEqualTo(qtedu.Impact_design.domain.model.en.UserRole.STUDENT);
            assertThat(saved.getWriter()).isEqualTo("0");
        }
    }

    @Nested
    @DisplayName("saveLoggedIn")
    class SaveLoggedIn {

        private JwtToken createJwtToken() {
            RefreshToken refreshToken = RefreshToken.of("refresh-token-value",
                    LocalDateTime.of(2026, 4, 1, 0, 0));
            return JwtToken.of("access-token", refreshToken);
        }

        @Test
        @DisplayName("기존 로그인 기록이 있으면 업데이트한다")
        void updatesExistingLoggedIn() {
            LoggedInModel existing = LoggedInModel.builder()
                    .loggedInId(100L)
                    .userId(1L)
                    .refreshToken("old-token")
                    .expiredAt(LocalDateTime.of(2026, 3, 1, 0, 0))
                    .build();
            given(loggedInRepository.findByUserId(1L)).willReturn(Optional.of(existing));

            authAppender.saveLoggedIn(1L, createJwtToken());

            ArgumentCaptor<LoggedInModel> captor = ArgumentCaptor.forClass(LoggedInModel.class);
            then(loggedInRepository).should().save(captor.capture());
            LoggedInModel saved = captor.getValue();
            assertThat(saved.getLoggedInId()).isEqualTo(100L);
            assertThat(saved.getRefreshToken()).isEqualTo("refresh-token-value");
            assertThat(saved.getExpiredAt()).isEqualTo(LocalDateTime.of(2026, 4, 1, 0, 0));
        }

        @Test
        @DisplayName("기존 로그인 기록이 없으면 새로 생성한다")
        void createsNewLoggedIn() {
            given(loggedInRepository.findByUserId(2L)).willReturn(Optional.empty());

            authAppender.saveLoggedIn(2L, createJwtToken());

            ArgumentCaptor<LoggedInModel> captor = ArgumentCaptor.forClass(LoggedInModel.class);
            then(loggedInRepository).should().save(captor.capture());
            LoggedInModel saved = captor.getValue();
            assertThat(saved.getLoggedInId()).isNull();
            assertThat(saved.getUserId()).isEqualTo(2L);
            assertThat(saved.getRefreshToken()).isEqualTo("refresh-token-value");
        }
    }
}
