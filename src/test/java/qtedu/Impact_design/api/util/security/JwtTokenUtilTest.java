package qtedu.Impact_design.api.util.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import qtedu.Impact_design.common.error.AuthorizationException;
import qtedu.Impact_design.domain.model.JwtToken;
import qtedu.Impact_design.domain.model.RefreshToken;
import qtedu.Impact_design.domain.model.UserId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenUtilTest {

    private JwtTokenUtil jwtTokenUtil;

    // 테스트용 시크릿 (64바이트 이상, HS512 요구사항)
    private static final String SECRET = "test-secret-key-for-jwt-token-util-test-must-be-at-least-64-bytes-long-1234567890";
    private static final long ACCESS_EXPIRATION = 3600000L;  // 1시간
    private static final long REFRESH_EXPIRATION = 604800000L; // 7일

    @BeforeEach
    void setUp() {
        jwtTokenUtil = new JwtTokenUtil(SECRET, ACCESS_EXPIRATION, REFRESH_EXPIRATION);
    }

    @Nested
    @DisplayName("createJwtToken")
    class CreateJwtToken {

        @Test
        @DisplayName("userId로 accessToken과 refreshToken을 생성한다")
        void createsTokens() {
            UserId userId = UserId.of(1L);

            JwtToken jwtToken = jwtTokenUtil.createJwtToken(userId);

            assertThat(jwtToken.getAccessToken()).isNotBlank();
            assertThat(jwtToken.getRefreshToken()).isNotNull();
            assertThat(jwtToken.getRefreshToken().getToken()).isNotBlank();
            assertThat(jwtToken.getRefreshToken().getExpiredAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("getUserIdFromToken")
    class GetUserIdFromToken {

        @Test
        @DisplayName("토큰에서 userId를 추출한다")
        void extractsUserId() {
            UserId userId = UserId.of(42L);
            String accessToken = jwtTokenUtil.createAccessToken(userId);

            UserId extracted = jwtTokenUtil.getUserIdFromToken(accessToken);

            assertThat(extracted.getId()).isEqualTo(42L);
        }

        @Test
        @DisplayName("Bearer 접두사가 있어도 userId를 추출한다")
        void extractsUserIdWithBearerPrefix() {
            UserId userId = UserId.of(42L);
            String accessToken = "Bearer " + jwtTokenUtil.createAccessToken(userId);

            UserId extracted = jwtTokenUtil.getUserIdFromToken(accessToken);

            assertThat(extracted.getId()).isEqualTo(42L);
        }
    }

    @Nested
    @DisplayName("validateToken")
    class ValidateToken {

        @Test
        @DisplayName("유효한 토큰이면 예외가 발생하지 않는다")
        void doesNotThrowForValidToken() {
            String token = jwtTokenUtil.createAccessToken(UserId.of(1L));

            jwtTokenUtil.validateToken(token);
        }

        @Test
        @DisplayName("잘못된 토큰이면 AuthorizationException이 발생한다")
        void throwsForInvalidToken() {
            assertThatThrownBy(() -> jwtTokenUtil.validateToken("invalid.token.here"))
                    .isInstanceOf(AuthorizationException.class);
        }

        @Test
        @DisplayName("만료된 토큰이면 AuthorizationException(TOKEN_EXPIRED)이 발생한다")
        void throwsForExpiredToken() {
            // 만료시간 0ms로 토큰 생성
            JwtTokenUtil expiredUtil = new JwtTokenUtil(SECRET, 0L, 0L);
            String token = expiredUtil.createAccessToken(UserId.of(1L));

            assertThatThrownBy(() -> jwtTokenUtil.validateToken(token))
                    .isInstanceOf(AuthorizationException.class);
        }
    }

    @Nested
    @DisplayName("cleanedToken")
    class CleanedToken {

        @Test
        @DisplayName("Bearer 접두사를 제거한다")
        void removesBearerPrefix() {
            String result = jwtTokenUtil.cleanedToken("Bearer abc123");

            assertThat(result).isEqualTo("abc123");
        }

        @Test
        @DisplayName("Bearer가 없으면 그대로 반환한다")
        void returnsAsIsWithoutBearer() {
            String result = jwtTokenUtil.cleanedToken("abc123");

            assertThat(result).isEqualTo("abc123");
        }
    }

    @Nested
    @DisplayName("createRefreshToken")
    class CreateRefreshToken {

        @Test
        @DisplayName("refreshToken과 만료시간을 생성한다")
        void createsRefreshToken() {
            RefreshToken refreshToken = jwtTokenUtil.createRefreshToken(UserId.of(1L));

            assertThat(refreshToken.getToken()).isNotBlank();
            assertThat(refreshToken.getExpiredAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("refresh")
    class Refresh {

        @Test
        @DisplayName("유효한 refreshToken으로 새 토큰 쌍을 발급한다")
        void refreshesToken() {
            RefreshToken refreshToken = jwtTokenUtil.createRefreshToken(UserId.of(5L));

            var result = jwtTokenUtil.refresh(refreshToken.getToken());

            assertThat(result.getLeft().getAccessToken()).isNotBlank();
            assertThat(result.getRight().getId()).isEqualTo(5L);
        }
    }
}
