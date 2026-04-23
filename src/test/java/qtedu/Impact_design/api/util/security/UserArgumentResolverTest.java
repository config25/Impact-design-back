package qtedu.Impact_design.api.util.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import qtedu.Impact_design.common.error.AuthorizationException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.model.UserId;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class UserArgumentResolverTest {

    private final UserArgumentResolver resolver = new UserArgumentResolver();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("SecurityContext에 UserId principal이 있으면 그대로 반환한다")
    void resolvesUserIdFromContext() throws Exception {
        UserId userId = UserId.of(42L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList()));

        Object result = resolver.resolveArgument(mock(org.springframework.core.MethodParameter.class), null, null, null);

        assertThat(result).isInstanceOf(UserId.class);
        assertThat(((UserId) result).getId()).isEqualTo(42L);
    }

    @Test
    @DisplayName("SecurityContext에 Authentication이 없으면 NOT_AUTHORIZED 예외가 발생한다")
    void throwsWhenAuthenticationIsNull() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> resolver.resolveArgument(
                mock(org.springframework.core.MethodParameter.class), null, null, null))
                .isInstanceOf(AuthorizationException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.NOT_AUTHORIZED);
    }

    @Test
    @DisplayName("principal이 UserId가 아니면 NOT_AUTHORIZED 예외가 발생한다")
    void throwsWhenPrincipalIsNotUserId() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("someString", null, Collections.emptyList()));

        assertThatThrownBy(() -> resolver.resolveArgument(
                mock(org.springframework.core.MethodParameter.class), null, null, null))
                .isInstanceOf(AuthorizationException.class)
                .extracting("errorCode").isEqualTo(ErrorCode.NOT_AUTHORIZED);
    }

    @Test
    @DisplayName("@CurrentUser 애노테이션이 붙은 파라미터만 지원한다")
    void supportsOnlyCurrentUserAnnotated() throws NoSuchMethodException {
        java.lang.reflect.Method withAnnotation = Holder.class.getMethod("annotated", UserId.class);
        java.lang.reflect.Method withoutAnnotation = Holder.class.getMethod("plain", UserId.class);

        org.springframework.core.MethodParameter annotated =
                new org.springframework.core.MethodParameter(withAnnotation, 0);
        org.springframework.core.MethodParameter plain =
                new org.springframework.core.MethodParameter(withoutAnnotation, 0);

        assertThat(resolver.supportsParameter(annotated)).isTrue();
        assertThat(resolver.supportsParameter(plain)).isFalse();
    }

    @SuppressWarnings("unused")
    private static class Holder {
        public void annotated(@CurrentUser UserId user) {}
        public void plain(UserId user) {}
    }
}
