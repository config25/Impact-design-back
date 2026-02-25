package qtedu.Impact_design.docs.identitycanvas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import qtedu.Impact_design.api.config.SecurityConfig;
import qtedu.Impact_design.api.controller.IdentityCanvasController;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.api.util.security.JwtAuthenticationFilter;
import qtedu.Impact_design.api.util.security.UserArgumentResolver;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.docs.RestDocsTestSupport;
import qtedu.Impact_design.domain.service.IdentityCanvasService;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = IdentityCanvasController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        ),
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = UserArgumentResolver.class
        )
)
class IdentityCanvasControllerTest extends RestDocsTestSupport {

    @MockitoBean
    private IdentityCanvasService identityCanvasService;

    private IdentityCanvasResponse sampleResponse(boolean submitted) {
        return IdentityCanvasResponse.builder()
                .identityId(1L)
                .mission("사회 문제 해결")
                .vision("지속 가능한 미래")
                .value("협력과 혁신")
                .macro("고령화 사회")
                .tech("AI 기술")
                .customer("지역 주민")
                .competitor("기존 복지 서비스")
                .capability("데이터 분석 역량")
                .culture("수평적 조직 문화")
                .structure("팀 기반 구조")
                .etc("기타 사항")
                .newMission("새로운 미션")
                .newVision("새로운 비전")
                .newValue("새로운 가치")
                .userId(1L)
                .submitted(submitted)
                .imageUrl("http://iptdesign.mycafe24.com/uploads/class/1/image.png")
                .build();
    }

    private Map<String, Object> sampleRequestBody() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("mission", "사회 문제 해결");
        body.put("vision", "지속 가능한 미래");
        body.put("value", "협력과 혁신");
        body.put("macro", "고령화 사회");
        body.put("tech", "AI 기술");
        body.put("customer", "지역 주민");
        body.put("competitor", "기존 복지 서비스");
        body.put("capability", "데이터 분석 역량");
        body.put("culture", "수평적 조직 문화");
        body.put("structure", "팀 기반 구조");
        body.put("etc", "기타 사항");
        body.put("newMission", "새로운 미션");
        body.put("newVision", "새로운 비전");
        body.put("newValue", "새로운 가치");
        return body;
    }

    // ===== 1. Identity 캔버스 조회 =====

    @Test
    @DisplayName("Identity 캔버스 조회")
    void getIdentityCanvas() throws Exception {
        authenticateAs(1L);
        given(identityCanvasService.getIdentityCanvas(anyLong()))
                .willReturn(sampleResponse(false));

        mockMvc.perform(get("/api/identity-canvas"))
                .andExpect(status().isOk())
                .andDo(document("identity-canvas/get",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.identityId").description("Identity 캔버스 ID"),
                                fieldWithPath("data.mission").description("미션"),
                                fieldWithPath("data.vision").description("비전"),
                                fieldWithPath("data.value").description("가치"),
                                fieldWithPath("data.macro").description("정책/경제"),
                                fieldWithPath("data.tech").description("기술"),
                                fieldWithPath("data.customer").description("고객/사회"),
                                fieldWithPath("data.competitor").description("경쟁자"),
                                fieldWithPath("data.capability").description("역량"),
                                fieldWithPath("data.culture").description("문화"),
                                fieldWithPath("data.structure").description("구조"),
                                fieldWithPath("data.etc").description("기타"),
                                fieldWithPath("data.newMission").description("새로운 미션"),
                                fieldWithPath("data.newVision").description("새로운 비전"),
                                fieldWithPath("data.newValue").description("새로운 가치"),
                                fieldWithPath("data.userId").description("사용자 ID"),
                                fieldWithPath("data.submitted").description("제출 여부"),
                                fieldWithPath("data.imageUrl").description("이미지 URL").optional()
                        )
                ));
    }

    // ===== 2. Identity 캔버스 저장 =====

    @Test
    @DisplayName("Identity 캔버스 저장")
    void saveIdentityCanvas() throws Exception {
        authenticateAs(1L);
        given(identityCanvasService.saveIdentityCanvas(anyLong(), any()))
                .willReturn(sampleResponse(false));

        mockMvc.perform(post("/api/identity-canvas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestBody())))
                .andExpect(status().isOk())
                .andDo(document("identity-canvas/save",
                        requestFields(
                                fieldWithPath("mission").description("미션"),
                                fieldWithPath("vision").description("비전"),
                                fieldWithPath("value").description("가치"),
                                fieldWithPath("macro").description("정책/경제"),
                                fieldWithPath("tech").description("기술"),
                                fieldWithPath("customer").description("고객/사회"),
                                fieldWithPath("competitor").description("경쟁자"),
                                fieldWithPath("capability").description("역량"),
                                fieldWithPath("culture").description("문화"),
                                fieldWithPath("structure").description("구조"),
                                fieldWithPath("etc").description("기타"),
                                fieldWithPath("newMission").description("새로운 미션"),
                                fieldWithPath("newVision").description("새로운 비전"),
                                fieldWithPath("newValue").description("새로운 가치")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.identityId").description("Identity 캔버스 ID"),
                                fieldWithPath("data.mission").description("미션"),
                                fieldWithPath("data.vision").description("비전"),
                                fieldWithPath("data.value").description("가치"),
                                fieldWithPath("data.macro").description("정책/경제"),
                                fieldWithPath("data.tech").description("기술"),
                                fieldWithPath("data.customer").description("고객/사회"),
                                fieldWithPath("data.competitor").description("경쟁자"),
                                fieldWithPath("data.capability").description("역량"),
                                fieldWithPath("data.culture").description("문화"),
                                fieldWithPath("data.structure").description("구조"),
                                fieldWithPath("data.etc").description("기타"),
                                fieldWithPath("data.newMission").description("새로운 미션"),
                                fieldWithPath("data.newVision").description("새로운 비전"),
                                fieldWithPath("data.newValue").description("새로운 가치"),
                                fieldWithPath("data.userId").description("사용자 ID"),
                                fieldWithPath("data.submitted").description("제출 여부"),
                                fieldWithPath("data.imageUrl").description("이미지 URL").optional()
                        )
                ));
    }

    @Test
    @DisplayName("Identity 캔버스 저장 - 이미 제출 완료")
    void saveIdentityCanvas_alreadySubmitted() throws Exception {
        authenticateAs(1L);
        given(identityCanvasService.saveIdentityCanvas(anyLong(), any()))
                .willThrow(new ConflictException(ErrorCode.ALREADY_SUBMITTED));

        mockMvc.perform(post("/api/identity-canvas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestBody())))
                .andExpect(status().isConflict())
                .andDo(document("identity-canvas/save-already-submitted",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("CANVAS_1"),
                                fieldWithPath("data.message").description("이미 제출 완료된 캔버스입니다.")
                        )
                ));
    }

    // ===== 3. Identity 캔버스 제출 =====

    @Test
    @DisplayName("Identity 캔버스 제출")
    void submitIdentityCanvas() throws Exception {
        authenticateAs(1L);
        given(identityCanvasService.submitIdentityCanvas(anyLong(), any()))
                .willReturn(sampleResponse(true));

        mockMvc.perform(patch("/api/identity-canvas/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestBody())))
                .andExpect(status().isOk())
                .andDo(document("identity-canvas/submit",
                        requestFields(
                                fieldWithPath("mission").description("미션"),
                                fieldWithPath("vision").description("비전"),
                                fieldWithPath("value").description("가치"),
                                fieldWithPath("macro").description("정책/경제"),
                                fieldWithPath("tech").description("기술"),
                                fieldWithPath("customer").description("고객/사회"),
                                fieldWithPath("competitor").description("경쟁자"),
                                fieldWithPath("capability").description("역량"),
                                fieldWithPath("culture").description("문화"),
                                fieldWithPath("structure").description("구조"),
                                fieldWithPath("etc").description("기타"),
                                fieldWithPath("newMission").description("새로운 미션"),
                                fieldWithPath("newVision").description("새로운 비전"),
                                fieldWithPath("newValue").description("새로운 가치")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.identityId").description("Identity 캔버스 ID"),
                                fieldWithPath("data.mission").description("미션"),
                                fieldWithPath("data.vision").description("비전"),
                                fieldWithPath("data.value").description("가치"),
                                fieldWithPath("data.macro").description("정책/경제"),
                                fieldWithPath("data.tech").description("기술"),
                                fieldWithPath("data.customer").description("고객/사회"),
                                fieldWithPath("data.competitor").description("경쟁자"),
                                fieldWithPath("data.capability").description("역량"),
                                fieldWithPath("data.culture").description("문화"),
                                fieldWithPath("data.structure").description("구조"),
                                fieldWithPath("data.etc").description("기타"),
                                fieldWithPath("data.newMission").description("새로운 미션"),
                                fieldWithPath("data.newVision").description("새로운 비전"),
                                fieldWithPath("data.newValue").description("새로운 가치"),
                                fieldWithPath("data.userId").description("사용자 ID"),
                                fieldWithPath("data.submitted").description("제출 여부"),
                                fieldWithPath("data.imageUrl").description("이미지 URL").optional()
                        )
                ));
    }

    @Test
    @DisplayName("Identity 캔버스 제출 - 이미 제출 완료")
    void submitIdentityCanvas_alreadySubmitted() throws Exception {
        authenticateAs(1L);
        given(identityCanvasService.submitIdentityCanvas(anyLong(), any()))
                .willThrow(new ConflictException(ErrorCode.ALREADY_SUBMITTED));

        mockMvc.perform(patch("/api/identity-canvas/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestBody())))
                .andExpect(status().isConflict())
                .andDo(document("identity-canvas/submit-already-submitted",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("CANVAS_1"),
                                fieldWithPath("data.message").description("이미 제출 완료된 캔버스입니다.")
                        )
                ));
    }
}
