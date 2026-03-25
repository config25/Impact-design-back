package qtedu.Impact_design.domain.implementation.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.util.security.JwtTokenUtil;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.RefreshToken;
import qtedu.Impact_design.domain.model.UserId;
import qtedu.Impact_design.domain.model.user.UserinfoModel;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuthGeneratorTest {

    @InjectMocks
    private AuthGenerator authGenerator;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @Test
    @DisplayName("UserinfoModel의 userId로 JWT 토큰을 생성한다")
    void generateToken() {
        UserinfoModel userinfo = UserinfoModel.builder().userId(42L).loginId("a11").build();
        JwtToken expected = JwtToken.of("access",
                RefreshToken.of("refresh", LocalDateTime.of(2026, 4, 1, 0, 0)));
        given(jwtTokenUtil.createJwtToken(UserId.of(42L))).willReturn(expected);

        JwtToken result = authGenerator.generateToken(userinfo);

        assertThat(result.getAccessToken()).isEqualTo("access");
        assertThat(result.getRefreshToken().getToken()).isEqualTo("refresh");

        ArgumentCaptor<UserId> captor = ArgumentCaptor.forClass(UserId.class);
        then(jwtTokenUtil).should().createJwtToken(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo(42L);
    }
}
