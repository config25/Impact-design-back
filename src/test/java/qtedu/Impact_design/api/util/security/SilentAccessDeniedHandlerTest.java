package qtedu.Impact_design.api.util.security;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SilentAccessDeniedHandlerTest {

    private final SilentAccessDeniedHandler handler = new SilentAccessDeniedHandler();

    @Test
    @DisplayName("AccessDeniedException 발생 시 403 상태와 JSON 본문을 반환한다")
    void writes403Json() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("denied"));

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(response.getContentAsString())
                .contains("\"status\":403")
                .contains("\"message\":\"Access Denied\"");
    }
}
