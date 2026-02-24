package qtedu.Impact_design.docs.game;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import qtedu.Impact_design.api.config.SecurityConfig;
import qtedu.Impact_design.api.controller.GameController;
import qtedu.Impact_design.api.util.security.JwtAuthenticationFilter;
import qtedu.Impact_design.api.util.security.UserArgumentResolver;
import qtedu.Impact_design.docs.RestDocsTestSupport;
import qtedu.Impact_design.domain.service.GameService;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = GameController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        ),
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = UserArgumentResolver.class
        )
)
class GameControllerTest extends RestDocsTestSupport {

    @MockitoBean
    private GameService gameService;

    @Test
    @DisplayName("학생용 공개범위(step) 조회")
    void getUserStep() throws Exception {
        authenticateAs(1L);
        given(gameService.getUserStep(anyLong()))
                .willReturn("FLOW");

        mockMvc.perform(get("/api/game/step"))
                .andExpect(status().isOk())
                .andDo(document("game/step",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.step").description("현재 공개된 단계 (예: IMPACT_CHECK, IDENTITY, FLOW, QUICK_WIN, BUILD_WIN, FUNDING)")
                        )
                ));
    }
}
