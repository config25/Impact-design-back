package qtedu.Impact_design.docs.quickwin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import qtedu.Impact_design.api.config.SecurityConfig;
import qtedu.Impact_design.api.controller.QuickWinCanvasController;
import qtedu.Impact_design.api.dto.response.quickwin.QuickWinCanvasResponse;
import qtedu.Impact_design.api.util.security.JwtAuthenticationFilter;
import qtedu.Impact_design.api.util.security.UserArgumentResolver;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.docs.RestDocsTestSupport;
import qtedu.Impact_design.domain.model.en.OutcomeType;
import qtedu.Impact_design.domain.service.QuickWinCanvasService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = QuickWinCanvasController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        ),
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = UserArgumentResolver.class
        )
)
class QuickWinCanvasControllerTest extends RestDocsTestSupport {

    @MockitoBean
    private QuickWinCanvasService quickWinCanvasService;

    private QuickWinCanvasResponse sampleResponse(boolean submitted) {
        return QuickWinCanvasResponse.builder()
                .canvasId(1L)
                .strategicGoal("지역 사회 문제 해결")
                .taskName("환경 캠페인")
                .taskDescription("지역 환경 개선을 위한 캠페인 기획")
                .crisisSignal("환경 오염 심화")
                .painTouchPoint("주민 불편 사항")
                .userId(1L)
                .submitted(submitted)
                .taskInputs(List.of(
                        QuickWinCanvasResponse.TaskInputItem.builder()
                                .inputId(1L).resourceName("홍보물").quantity(100).orderNo(1).build()
                ))
                .taskActivities(List.of(
                        QuickWinCanvasResponse.TaskActivityItem.builder()
                                .activityId(1L).processStep("기획").activityContent("캠페인 기획 회의")
                                .duration("2주").orderNo(1).build()
                ))
                .teamwork(QuickWinCanvasResponse.TeamworkItem.builder()
                        .teamworkId(1L).activityTeamwork("팀 회의 진행").workType("협업").build())
                .taskOutcomes(List.of(
                        QuickWinCanvasResponse.TaskOutcomeItem.builder()
                                .outcomeNo(1L).outcomeType(OutcomeType.QUANTITATIVE)
                                .outcomeContent("참여 인원 50명").orderNo(1).build(),
                        QuickWinCanvasResponse.TaskOutcomeItem.builder()
                                .outcomeNo(2L).outcomeType(OutcomeType.QUALITATIVE)
                                .outcomeContent("주민 만족도 향상").orderNo(2).build()
                ))
                .imageUrl("http://iptdesign.mycafe24.com/uploads/class/1/image.png")
                .build();
    }

    private String sampleRequestJson() throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("strategicGoal", "지역 사회 문제 해결");
        body.put("taskName", "환경 캠페인");
        body.put("taskDescription", "지역 환경 개선을 위한 캠페인 기획");
        body.put("crisisSignal", "환경 오염 심화");
        body.put("painTouchPoint", "주민 불편 사항");
        body.put("taskInputs", List.of(Map.of(
                "inputId", 1, "resourceName", "홍보물", "quantity", 100, "orderNo", 1
        )));
        body.put("taskActivities", List.of(Map.of(
                "activityId", 1, "processStep", "기획",
                "activityContent", "캠페인 기획 회의", "duration", "2주", "orderNo", 1
        )));
        body.put("teamwork", Map.of(
                "teamworkId", 1, "activityTeamwork", "팀 회의 진행", "workType", "협업"
        ));
        body.put("taskOutcomes", List.of(Map.of(
                "outcomeNo", 1, "outcomeType", "QUANTITATIVE",
                "outcomeContent", "참여 인원 50명", "orderNo", 1
        )));
        return objectMapper.writeValueAsString(body);
    }

    // ===== 1. QuickWin 캔버스 조회 =====

    @Test
    @DisplayName("QuickWin 캔버스 조회")
    void getQuickWinCanvas() throws Exception {
        authenticateAs(1L);
        given(quickWinCanvasService.getQuickWinCanvas(anyLong()))
                .willReturn(sampleResponse(false));

        mockMvc.perform(get("/api/quick-win-canvas"))
                .andExpect(status().isOk())
                .andDo(document("quick-win-canvas/get",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.canvasId").description("캔버스 ID"),
                                fieldWithPath("data.strategicGoal").description("전략 목표"),
                                fieldWithPath("data.taskName").description("과제명"),
                                fieldWithPath("data.taskDescription").description("주요 내용"),
                                fieldWithPath("data.crisisSignal").description("위기의 신호"),
                                fieldWithPath("data.painTouchPoint").description("페인 포인트"),
                                fieldWithPath("data.userId").description("사용자 ID"),
                                fieldWithPath("data.submitted").description("제출 여부"),
                                fieldWithPath("data.taskInputs[]").description("투입 자원 목록"),
                                fieldWithPath("data.taskInputs[].inputId").description("투입 ID"),
                                fieldWithPath("data.taskInputs[].resourceName").description("필요 자원"),
                                fieldWithPath("data.taskInputs[].quantity").description("수량"),
                                fieldWithPath("data.taskInputs[].orderNo").description("순서"),
                                fieldWithPath("data.taskActivities[]").description("활동 목록"),
                                fieldWithPath("data.taskActivities[].activityId").description("활동 ID"),
                                fieldWithPath("data.taskActivities[].processStep").description("추진 절차"),
                                fieldWithPath("data.taskActivities[].activityContent").description("주요 내용"),
                                fieldWithPath("data.taskActivities[].duration").description("소요 기간"),
                                fieldWithPath("data.taskActivities[].orderNo").description("순서"),
                                fieldWithPath("data.teamwork").description("팀워크"),
                                fieldWithPath("data.teamwork.teamworkId").description("팀워크 ID"),
                                fieldWithPath("data.teamwork.activityTeamwork").description("활동 팀워크"),
                                fieldWithPath("data.teamwork.workType").description("산출 팀워크"),
                                fieldWithPath("data.taskOutcomes[]").description("성과 목록"),
                                fieldWithPath("data.taskOutcomes[].outcomeNo").description("성과 번호"),
                                fieldWithPath("data.taskOutcomes[].outcomeType").description("성과 유형 (QUANTITATIVE, QUALITATIVE)"),
                                fieldWithPath("data.taskOutcomes[].outcomeContent").description("성과 내용"),
                                fieldWithPath("data.taskOutcomes[].orderNo").description("순서"),
                                fieldWithPath("data.imageUrl").description("이미지 URL").optional()
                        )
                ));
    }

    // ===== 2. QuickWin 캔버스 저장 =====

    @Test
    @DisplayName("QuickWin 캔버스 저장")
    void saveQuickWinCanvas() throws Exception {
        authenticateAs(1L);
        given(quickWinCanvasService.saveQuickWinCanvas(anyLong(), any()))
                .willReturn(sampleResponse(false));

        mockMvc.perform(post("/api/quick-win-canvas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson()))
                .andExpect(status().isOk())
                .andDo(document("quick-win-canvas/save",
                        requestFields(
                                fieldWithPath("strategicGoal").description("전략 목표"),
                                fieldWithPath("taskName").description("과제명"),
                                fieldWithPath("taskDescription").description("주요 내용"),
                                fieldWithPath("crisisSignal").description("위기의 신호"),
                                fieldWithPath("painTouchPoint").description("페인 포인트"),
                                fieldWithPath("taskInputs[]").description("투입 자원 목록"),
                                fieldWithPath("taskInputs[].inputId").description("투입 ID (수정 시)"),
                                fieldWithPath("taskInputs[].resourceName").description("필요 자원"),
                                fieldWithPath("taskInputs[].quantity").description("수량"),
                                fieldWithPath("taskInputs[].orderNo").description("순서"),
                                fieldWithPath("taskActivities[]").description("활동 목록"),
                                fieldWithPath("taskActivities[].activityId").description("활동 ID (수정 시)"),
                                fieldWithPath("taskActivities[].processStep").description("추진 절차"),
                                fieldWithPath("taskActivities[].activityContent").description("주요 내용"),
                                fieldWithPath("taskActivities[].duration").description("소요 기간"),
                                fieldWithPath("taskActivities[].orderNo").description("순서"),
                                fieldWithPath("teamwork").description("팀워크"),
                                fieldWithPath("teamwork.teamworkId").description("팀워크 ID (수정 시)"),
                                fieldWithPath("teamwork.activityTeamwork").description("활동 팀워크"),
                                fieldWithPath("teamwork.workType").description("산출 팀워크"),
                                fieldWithPath("taskOutcomes[]").description("성과 목록"),
                                fieldWithPath("taskOutcomes[].outcomeNo").description("성과 번호 (수정 시)"),
                                fieldWithPath("taskOutcomes[].outcomeType").description("성과 유형 (QUANTITATIVE, QUALITATIVE)"),
                                fieldWithPath("taskOutcomes[].outcomeContent").description("성과 내용"),
                                fieldWithPath("taskOutcomes[].orderNo").description("순서")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.canvasId").description("캔버스 ID"),
                                fieldWithPath("data.strategicGoal").description("전략 목표"),
                                fieldWithPath("data.taskName").description("과제명"),
                                fieldWithPath("data.taskDescription").description("주요 내용"),
                                fieldWithPath("data.crisisSignal").description("위기의 신호"),
                                fieldWithPath("data.painTouchPoint").description("페인 포인트"),
                                fieldWithPath("data.userId").description("사용자 ID"),
                                fieldWithPath("data.submitted").description("제출 여부"),
                                fieldWithPath("data.taskInputs[]").description("투입 자원 목록"),
                                fieldWithPath("data.taskInputs[].inputId").description("투입 ID"),
                                fieldWithPath("data.taskInputs[].resourceName").description("필요 자원"),
                                fieldWithPath("data.taskInputs[].quantity").description("수량"),
                                fieldWithPath("data.taskInputs[].orderNo").description("순서"),
                                fieldWithPath("data.taskActivities[]").description("활동 목록"),
                                fieldWithPath("data.taskActivities[].activityId").description("활동 ID"),
                                fieldWithPath("data.taskActivities[].processStep").description("추진 절차"),
                                fieldWithPath("data.taskActivities[].activityContent").description("주요 내용"),
                                fieldWithPath("data.taskActivities[].duration").description("소요 기간"),
                                fieldWithPath("data.taskActivities[].orderNo").description("순서"),
                                fieldWithPath("data.teamwork").description("팀워크"),
                                fieldWithPath("data.teamwork.teamworkId").description("팀워크 ID"),
                                fieldWithPath("data.teamwork.activityTeamwork").description("활동 팀워크"),
                                fieldWithPath("data.teamwork.workType").description("산출 팀워크"),
                                fieldWithPath("data.taskOutcomes[]").description("성과 목록"),
                                fieldWithPath("data.taskOutcomes[].outcomeNo").description("성과 번호"),
                                fieldWithPath("data.taskOutcomes[].outcomeType").description("성과 유형 (QUANTITATIVE, QUALITATIVE)"),
                                fieldWithPath("data.taskOutcomes[].outcomeContent").description("성과 내용"),
                                fieldWithPath("data.taskOutcomes[].orderNo").description("순서"),
                                fieldWithPath("data.imageUrl").description("이미지 URL").optional()
                        )
                ));
    }

    @Test
    @DisplayName("QuickWin 캔버스 저장 - 이미 제출 완료")
    void saveQuickWinCanvas_alreadySubmitted() throws Exception {
        authenticateAs(1L);
        given(quickWinCanvasService.saveQuickWinCanvas(anyLong(), any()))
                .willThrow(new ConflictException(ErrorCode.ALREADY_SUBMITTED));

        mockMvc.perform(post("/api/quick-win-canvas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson()))
                .andExpect(status().isConflict())
                .andDo(document("quick-win-canvas/save-already-submitted",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("CANVAS_1"),
                                fieldWithPath("data.message").description("이미 제출 완료된 캔버스입니다.")
                        )
                ));
    }

    // ===== 3. QuickWin 캔버스 제출 =====

    @Test
    @DisplayName("QuickWin 캔버스 제출")
    void submitQuickWinCanvas() throws Exception {
        authenticateAs(1L);
        given(quickWinCanvasService.submitQuickWinCanvas(anyLong(), any()))
                .willReturn(sampleResponse(true));

        mockMvc.perform(patch("/api/quick-win-canvas/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson()))
                .andExpect(status().isOk())
                .andDo(document("quick-win-canvas/submit",
                        requestFields(
                                fieldWithPath("strategicGoal").description("전략 목표"),
                                fieldWithPath("taskName").description("과제명"),
                                fieldWithPath("taskDescription").description("주요 내용"),
                                fieldWithPath("crisisSignal").description("위기의 신호"),
                                fieldWithPath("painTouchPoint").description("페인 포인트"),
                                fieldWithPath("taskInputs[]").description("투입 자원 목록"),
                                fieldWithPath("taskInputs[].inputId").description("투입 ID (수정 시)"),
                                fieldWithPath("taskInputs[].resourceName").description("필요 자원"),
                                fieldWithPath("taskInputs[].quantity").description("수량"),
                                fieldWithPath("taskInputs[].orderNo").description("순서"),
                                fieldWithPath("taskActivities[]").description("활동 목록"),
                                fieldWithPath("taskActivities[].activityId").description("활동 ID (수정 시)"),
                                fieldWithPath("taskActivities[].processStep").description("추진 절차"),
                                fieldWithPath("taskActivities[].activityContent").description("주요 내용"),
                                fieldWithPath("taskActivities[].duration").description("소요 기간"),
                                fieldWithPath("taskActivities[].orderNo").description("순서"),
                                fieldWithPath("teamwork").description("팀워크"),
                                fieldWithPath("teamwork.teamworkId").description("팀워크 ID (수정 시)"),
                                fieldWithPath("teamwork.activityTeamwork").description("활동 팀워크"),
                                fieldWithPath("teamwork.workType").description("산출 팀워크"),
                                fieldWithPath("taskOutcomes[]").description("성과 목록"),
                                fieldWithPath("taskOutcomes[].outcomeNo").description("성과 번호 (수정 시)"),
                                fieldWithPath("taskOutcomes[].outcomeType").description("성과 유형 (QUANTITATIVE, QUALITATIVE)"),
                                fieldWithPath("taskOutcomes[].outcomeContent").description("성과 내용"),
                                fieldWithPath("taskOutcomes[].orderNo").description("순서")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.canvasId").description("캔버스 ID"),
                                fieldWithPath("data.strategicGoal").description("전략 목표"),
                                fieldWithPath("data.taskName").description("과제명"),
                                fieldWithPath("data.taskDescription").description("주요 내용"),
                                fieldWithPath("data.crisisSignal").description("위기의 신호"),
                                fieldWithPath("data.painTouchPoint").description("페인 포인트"),
                                fieldWithPath("data.userId").description("사용자 ID"),
                                fieldWithPath("data.submitted").description("제출 여부"),
                                fieldWithPath("data.taskInputs[]").description("투입 자원 목록"),
                                fieldWithPath("data.taskInputs[].inputId").description("투입 ID"),
                                fieldWithPath("data.taskInputs[].resourceName").description("필요 자원"),
                                fieldWithPath("data.taskInputs[].quantity").description("수량"),
                                fieldWithPath("data.taskInputs[].orderNo").description("순서"),
                                fieldWithPath("data.taskActivities[]").description("활동 목록"),
                                fieldWithPath("data.taskActivities[].activityId").description("활동 ID"),
                                fieldWithPath("data.taskActivities[].processStep").description("추진 절차"),
                                fieldWithPath("data.taskActivities[].activityContent").description("주요 내용"),
                                fieldWithPath("data.taskActivities[].duration").description("소요 기간"),
                                fieldWithPath("data.taskActivities[].orderNo").description("순서"),
                                fieldWithPath("data.teamwork").description("팀워크"),
                                fieldWithPath("data.teamwork.teamworkId").description("팀워크 ID"),
                                fieldWithPath("data.teamwork.activityTeamwork").description("활동 팀워크"),
                                fieldWithPath("data.teamwork.workType").description("산출 팀워크"),
                                fieldWithPath("data.taskOutcomes[]").description("성과 목록"),
                                fieldWithPath("data.taskOutcomes[].outcomeNo").description("성과 번호"),
                                fieldWithPath("data.taskOutcomes[].outcomeType").description("성과 유형 (QUANTITATIVE, QUALITATIVE)"),
                                fieldWithPath("data.taskOutcomes[].outcomeContent").description("성과 내용"),
                                fieldWithPath("data.taskOutcomes[].orderNo").description("순서"),
                                fieldWithPath("data.imageUrl").description("이미지 URL").optional()
                        )
                ));
    }

    @Test
    @DisplayName("QuickWin 캔버스 제출 - 이미 제출 완료")
    void submitQuickWinCanvas_alreadySubmitted() throws Exception {
        authenticateAs(1L);
        given(quickWinCanvasService.submitQuickWinCanvas(anyLong(), any()))
                .willThrow(new ConflictException(ErrorCode.ALREADY_SUBMITTED));

        mockMvc.perform(patch("/api/quick-win-canvas/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson()))
                .andExpect(status().isConflict())
                .andDo(document("quick-win-canvas/submit-already-submitted",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("CANVAS_1"),
                                fieldWithPath("data.message").description("이미 제출 완료된 캔버스입니다.")
                        )
                ));
    }
}
