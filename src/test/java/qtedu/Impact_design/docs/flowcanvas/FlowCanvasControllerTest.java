package qtedu.Impact_design.docs.flowcanvas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import qtedu.Impact_design.api.config.SecurityConfig;
import qtedu.Impact_design.api.controller.FlowCanvasController;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.api.util.security.JwtAuthenticationFilter;
import qtedu.Impact_design.api.util.security.UserArgumentResolver;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.docs.RestDocsTestSupport;
import qtedu.Impact_design.domain.service.FlowCanvasService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = FlowCanvasController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        ),
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = UserArgumentResolver.class
        )
)
class FlowCanvasControllerTest extends RestDocsTestSupport {

    @MockitoBean
    private FlowCanvasService flowCanvasService;

    private FlowCanvasResponse sampleResponse(boolean submitted) {
        FlowCanvasResponse.TacticalItem tactical = FlowCanvasResponse.TacticalItem.builder()
                .metricId(1L)
                .tacticalMetric("고객 만족도")
                .tacticalGoal("90% 이상")
                .orderNo(1)
                .build();

        FlowCanvasResponse.StrategicActivityItem activity = FlowCanvasResponse.StrategicActivityItem.builder()
                .activityId(1L)
                .activityMetric("서비스 개선 횟수")
                .interCriteria("월 2회 이상")
                .orderNo(1)
                .build();

        FlowCanvasResponse.GoalItem goal = FlowCanvasResponse.GoalItem.builder()
                .goalId(1L)
                .goalTitle("사회적 가치 창출")
                .goalDescription("지역 사회 문제를 해결하는 솔루션 개발")
                .orderNo(1)
                .tacticals(List.of(tactical))
                .strategicActivities(List.of(activity))
                .build();

        return FlowCanvasResponse.builder()
                .newVision("지속 가능한 미래")
                .goals(List.of(goal))
                .submitted(submitted)
                .imageUrl("http://localhost:8080/uploads/class/1/image.png")
                .build();
    }

    private String sampleRequestJson() throws Exception {
        return objectMapper.writeValueAsString(java.util.Map.of(
                "goals", List.of(java.util.Map.of(
                        "goalId", 1,
                        "goalTitle", "사회적 가치 창출",
                        "goalDescription", "지역 사회 문제를 해결하는 솔루션 개발",
                        "orderNo", 1,
                        "tacticals", List.of(java.util.Map.of(
                                "metricId", 1,
                                "tacticalMetric", "고객 만족도",
                                "tacticalGoal", "90% 이상",
                                "orderNo", 1
                        )),
                        "strategicActivities", List.of(java.util.Map.of(
                                "activityId", 1,
                                "activityMetric", "서비스 개선 횟수",
                                "interCriteria", "월 2회 이상",
                                "orderNo", 1
                        ))
                ))
        ));
    }

    // ===== 1. Flow 캔버스 조회 =====

    @Test
    @DisplayName("Flow 캔버스 조회")
    void getFlowCanvas() throws Exception {
        authenticateAs(1L);
        given(flowCanvasService.getFlowCanvas(anyLong()))
                .willReturn(sampleResponse(false));

        mockMvc.perform(get("/api/flow-canvas"))
                .andExpect(status().isOk())
                .andDo(document("flow-canvas/get",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.newVision").description("새로운 비전 (Identity 캔버스에서 가져옴)").optional(),
                                fieldWithPath("data.goals[]").description("목표 목록"),
                                fieldWithPath("data.goals[].goalId").description("목표 ID"),
                                fieldWithPath("data.goals[].goalTitle").description("목표 제목"),
                                fieldWithPath("data.goals[].goalDescription").description("목표 설명"),
                                fieldWithPath("data.goals[].orderNo").description("목표 순서(1인당 3개)"),
                                fieldWithPath("data.goals[].tacticals[]").description("전술 지표 목록(목표당 3개)"),
                                fieldWithPath("data.goals[].tacticals[].metricId").description("전술적 성과 지표 ID"),
                                fieldWithPath("data.goals[].tacticals[].tacticalMetric").description("전술적 성과 지표"),
                                fieldWithPath("data.goals[].tacticals[].tacticalGoal").description("전술 목표"),
                                fieldWithPath("data.goals[].tacticals[].orderNo").description("지표 순서"),
                                fieldWithPath("data.goals[].strategicActivities[]").description("전략 활동 목록(목표당 3개)"),
                                fieldWithPath("data.goals[].strategicActivities[].activityId").description("전략적 활동 지표 ID"),
                                fieldWithPath("data.goals[].strategicActivities[].activityMetric").description("전략적 활동 지표"),
                                fieldWithPath("data.goals[].strategicActivities[].interCriteria").description("내재화 기준"),
                                fieldWithPath("data.goals[].strategicActivities[].orderNo").description("활동 순서"),
                                fieldWithPath("data.submitted").description("제출 여부"),
                                fieldWithPath("data.imageUrl").description("이미지 URL").optional()
                        )
                ));
    }

    // ===== 2. Flow 캔버스 저장 =====

    @Test
    @DisplayName("Flow 캔버스 저장")
    void saveFlowCanvas() throws Exception {
        authenticateAs(1L);
        given(flowCanvasService.saveFlowCanvas(anyLong(), any()))
                .willReturn(sampleResponse(false));

        mockMvc.perform(post("/api/flow-canvas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson()))
                .andExpect(status().isOk())
                .andDo(document("flow-canvas/save",
                        requestFields(
                                fieldWithPath("goals[]").description("목표 목록"),
                                fieldWithPath("goals[].goalId").description("목표 ID (수정 시)"),
                                fieldWithPath("goals[].goalTitle").description("목표 제목"),
                                fieldWithPath("goals[].goalDescription").description("목표 설명"),
                                fieldWithPath("goals[].orderNo").description("목표 순서(1인당 3개)"),
                                fieldWithPath("goals[].tacticals[]").description("전술 지표 목록(목표당 3개)"),
                                fieldWithPath("goals[].tacticals[].metricId").description("전술적 성과 지표 ID (수정 시)"),
                                fieldWithPath("goals[].tacticals[].tacticalMetric").description("전술적 성과 지표"),
                                fieldWithPath("goals[].tacticals[].tacticalGoal").description("전술 목표"),
                                fieldWithPath("goals[].tacticals[].orderNo").description("지표 순서"),
                                fieldWithPath("goals[].strategicActivities[]").description("전략 활동 목록(목표당 3개)"),
                                fieldWithPath("goals[].strategicActivities[].activityId").description("전략적 활동 지표 ID (수정 시)"),
                                fieldWithPath("goals[].strategicActivities[].activityMetric").description("전략적 활동 지표"),
                                fieldWithPath("goals[].strategicActivities[].interCriteria").description("내재화 기준 기준"),
                                fieldWithPath("goals[].strategicActivities[].orderNo").description("활동 순서")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.newVision").description("새로운 비전").optional(),
                                fieldWithPath("data.goals[]").description("목표 목록"),
                                fieldWithPath("data.goals[].goalId").description("목표 ID"),
                                fieldWithPath("data.goals[].goalTitle").description("목표 제목"),
                                fieldWithPath("data.goals[].goalDescription").description("목표 설명"),
                                fieldWithPath("data.goals[].orderNo").description("목표 순서(1인당 3개)"),
                                fieldWithPath("data.goals[].tacticals[]").description("전술 지표 목록(목표당 3개)"),
                                fieldWithPath("data.goals[].tacticals[].metricId").description("전술적 성과 지표 ID"),
                                fieldWithPath("data.goals[].tacticals[].tacticalMetric").description("전술적 성과 지표"),
                                fieldWithPath("data.goals[].tacticals[].tacticalGoal").description("전술 목표"),
                                fieldWithPath("data.goals[].tacticals[].orderNo").description("지표 순서"),
                                fieldWithPath("data.goals[].strategicActivities[]").description("전략 활동 목록(목표당 3개)"),
                                fieldWithPath("data.goals[].strategicActivities[].activityId").description("전략적 활동 지표 ID"),
                                fieldWithPath("data.goals[].strategicActivities[].activityMetric").description("전략적 활동 지표"),
                                fieldWithPath("data.goals[].strategicActivities[].interCriteria").description("내재화 기준"),
                                fieldWithPath("data.goals[].strategicActivities[].orderNo").description("활동 순서"),
                                fieldWithPath("data.submitted").description("제출 여부"),
                                fieldWithPath("data.imageUrl").description("이미지 URL").optional()
                        )
                ));
    }

    @Test
    @DisplayName("Flow 캔버스 저장 - 이미 제출 완료")
    void saveFlowCanvas_alreadySubmitted() throws Exception {
        authenticateAs(1L);
        given(flowCanvasService.saveFlowCanvas(anyLong(), any()))
                .willThrow(new ConflictException(ErrorCode.ALREADY_SUBMITTED));

        mockMvc.perform(post("/api/flow-canvas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson()))
                .andExpect(status().isConflict())
                .andDo(document("flow-canvas/save-already-submitted",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("CANVAS_1"),
                                fieldWithPath("data.message").description("이미 제출 완료된 캔버스입니다.")
                        )
                ));
    }

    // ===== 3. Flow 캔버스 제출 =====

    @Test
    @DisplayName("Flow 캔버스 제출")
    void submitFlowCanvas() throws Exception {
        authenticateAs(1L);
        given(flowCanvasService.submitFlowCanvas(anyLong(), any()))
                .willReturn(sampleResponse(true));

        mockMvc.perform(patch("/api/flow-canvas/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson()))
                .andExpect(status().isOk())
                .andDo(document("flow-canvas/submit",
                        requestFields(
                                fieldWithPath("goals[]").description("목표 목록"),
                                fieldWithPath("goals[].goalId").description("목표 ID (수정 시)"),
                                fieldWithPath("goals[].goalTitle").description("목표 제목"),
                                fieldWithPath("goals[].goalDescription").description("목표 설명"),
                                fieldWithPath("goals[].orderNo").description("목표 순서(1인당 3개)"),
                                fieldWithPath("goals[].tacticals[]").description("전술 지표 목록(목표당 3개)"),
                                fieldWithPath("goals[].tacticals[].metricId").description("전술적 성과 지표 ID (수정 시)"),
                                fieldWithPath("goals[].tacticals[].tacticalMetric").description("전술적 성과 지표"),
                                fieldWithPath("goals[].tacticals[].tacticalGoal").description("전술 목표"),
                                fieldWithPath("goals[].tacticals[].orderNo").description("지표 순서"),
                                fieldWithPath("goals[].strategicActivities[]").description("전략 활동 목록(목표당 3개)"),
                                fieldWithPath("goals[].strategicActivities[].activityId").description("전략적 활동 지표 ID (수정 시)"),
                                fieldWithPath("goals[].strategicActivities[].activityMetric").description("전략적 활동 지표"),
                                fieldWithPath("goals[].strategicActivities[].interCriteria").description("내재화 기준"),
                                fieldWithPath("goals[].strategicActivities[].orderNo").description("활동 순서")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.newVision").description("새로운 비전").optional(),
                                fieldWithPath("data.goals[]").description("목표 목록"),
                                fieldWithPath("data.goals[].goalId").description("목표 ID"),
                                fieldWithPath("data.goals[].goalTitle").description("목표 제목"),
                                fieldWithPath("data.goals[].goalDescription").description("목표 설명"),
                                fieldWithPath("data.goals[].orderNo").description("목표 순서(1인당 3개)"),
                                fieldWithPath("data.goals[].tacticals[]").description("전술 지표 목록(목표당 3개)"),
                                fieldWithPath("data.goals[].tacticals[].metricId").description("전술적 성과 지표 ID"),
                                fieldWithPath("data.goals[].tacticals[].tacticalMetric").description("전술적 성과 지표"),
                                fieldWithPath("data.goals[].tacticals[].tacticalGoal").description("전술 목표"),
                                fieldWithPath("data.goals[].tacticals[].orderNo").description("지표 순서"),
                                fieldWithPath("data.goals[].strategicActivities[]").description("전략 활동 목록(목표당 3개)"),
                                fieldWithPath("data.goals[].strategicActivities[].activityId").description("전략적 활동 지표 ID"),
                                fieldWithPath("data.goals[].strategicActivities[].activityMetric").description("전략적 활동 지표"),
                                fieldWithPath("data.goals[].strategicActivities[].interCriteria").description("내재화 기준"),
                                fieldWithPath("data.goals[].strategicActivities[].orderNo").description("활동 순서"),
                                fieldWithPath("data.submitted").description("제출 여부"),
                                fieldWithPath("data.imageUrl").description("이미지 URL").optional()
                        )
                ));
    }

    @Test
    @DisplayName("Flow 캔버스 제출 - 이미 제출 완료")
    void submitFlowCanvas_alreadySubmitted() throws Exception {
        authenticateAs(1L);
        given(flowCanvasService.submitFlowCanvas(anyLong(), any()))
                .willThrow(new ConflictException(ErrorCode.ALREADY_SUBMITTED));

        mockMvc.perform(patch("/api/flow-canvas/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson()))
                .andExpect(status().isConflict())
                .andDo(document("flow-canvas/submit-already-submitted",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("CANVAS_1"),
                                fieldWithPath("data.message").description("이미 제출 완료된 캔버스입니다.")
                        )
                ));
    }
}
