package qtedu.Impact_design.docs.buildwin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import qtedu.Impact_design.api.config.SecurityConfig;
import qtedu.Impact_design.api.controller.BuildWinCanvasController;
import qtedu.Impact_design.api.dto.response.buildwin.BuildWinCanvasResponse;
import qtedu.Impact_design.api.util.security.JwtAuthenticationFilter;
import qtedu.Impact_design.api.util.security.UserArgumentResolver;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.docs.RestDocsTestSupport;
import qtedu.Impact_design.domain.model.en.OutcomeType;
import qtedu.Impact_design.domain.service.BuildWinCanvasService;

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
        controllers = BuildWinCanvasController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        ),
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = UserArgumentResolver.class
        )
)
class BuildWinCanvasControllerTest extends RestDocsTestSupport {

    @MockitoBean
    private BuildWinCanvasService buildWinCanvasService;

    private BuildWinCanvasResponse sampleResponse(boolean submitted) {
        return BuildWinCanvasResponse.builder()
                .canvasId(1L)
                .strategicGoal("비즈니스 모델 구축")
                .taskName("서비스 프로토타입 개발")
                .taskDescription("MVP 기반 서비스 프로토타입 제작")
                .crisisSignal("시장 진입 지연")
                .painTouchPoint("사용자 피드백 부족")
                .userId(1L)
                .submitted(submitted)
                .taskInputs(List.of(
                        BuildWinCanvasResponse.TaskInputItem.builder()
                                .inputId(1L).resourceName("개발 인력").quantity(3).orderNo(1).build()
                ))
                .taskActivities(List.of(
                        BuildWinCanvasResponse.TaskActivityItem.builder()
                                .activityId(1L).processStep("설계").activityContent("서비스 아키텍처 설계")
                                .duration("3주").orderNo(1).build()
                ))
                .teamwork(BuildWinCanvasResponse.TeamworkItem.builder()
                        .teamworkId(1L).activityTeamwork("주간 스프린트 회의").workType("협업").build())
                .taskOutcomes(List.of(
                        BuildWinCanvasResponse.TaskOutcomeItem.builder()
                                .outcomeNo(1L).outcomeType(OutcomeType.QUANTITATIVE)
                                .outcomeContent("프로토타입 완성률 100%").orderNo(1).build(),
                        BuildWinCanvasResponse.TaskOutcomeItem.builder()
                                .outcomeNo(2L).outcomeType(OutcomeType.QUALITATIVE)
                                .outcomeContent("사용자 테스트 완료").orderNo(2).build()
                ))
                .imageUrl("http://localhost:8080/uploads/class/1/image.png")
                .build();
    }

    private String sampleRequestJson() throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("strategicGoal", "비즈니스 모델 구축");
        body.put("taskName", "서비스 프로토타입 개발");
        body.put("taskDescription", "MVP 기반 서비스 프로토타입 제작");
        body.put("crisisSignal", "시장 진입 지연");
        body.put("painTouchPoint", "사용자 피드백 부족");
        body.put("taskInputs", List.of(Map.of(
                "inputId", 1, "resourceName", "개발 인력", "quantity", 3, "orderNo", 1
        )));
        body.put("taskActivities", List.of(Map.of(
                "activityId", 1, "processStep", "설계",
                "activityContent", "서비스 아키텍처 설계", "duration", "3주", "orderNo", 1
        )));
        body.put("teamwork", Map.of(
                "teamworkId", 1, "activityTeamwork", "주간 스프린트 회의", "workType", "협업"
        ));
        body.put("taskOutcomes", List.of(Map.of(
                "outcomeNo", 1, "outcomeType", "QUANTITATIVE",
                "outcomeContent", "프로토타입 완성률 100%", "orderNo", 1
        )));
        return objectMapper.writeValueAsString(body);
    }

    // ===== 1. BuildWin 캔버스 조회 =====

    @Test
    @DisplayName("BuildWin 캔버스 조회")
    void getBuildWinCanvas() throws Exception {
        authenticateAs(1L);
        given(buildWinCanvasService.getBuildWinCanvas(anyLong()))
                .willReturn(sampleResponse(false));

        mockMvc.perform(get("/api/build-win-canvas"))
                .andExpect(status().isOk())
                .andDo(document("build-win-canvas/get",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.canvasId").description("캔버스 ID"),
                                fieldWithPath("data.strategicGoal").description("전략 목표"),
                                fieldWithPath("data.taskName").description("과제명"),
                                fieldWithPath("data.taskDescription").description("주요 내용"),
                                fieldWithPath("data.crisisSignal").description("변화의 신호"),
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
                                fieldWithPath("data.taskActivities[].processStep").description("젼환 단계"),
                                fieldWithPath("data.taskActivities[].activityContent").description("전환 활동"),
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

    // ===== 2. BuildWin 캔버스 저장 =====

    @Test
    @DisplayName("BuildWin 캔버스 저장")
    void saveBuildWinCanvas() throws Exception {
        authenticateAs(1L);
        given(buildWinCanvasService.saveBuildWinCanvas(anyLong(), any()))
                .willReturn(sampleResponse(false));

        mockMvc.perform(post("/api/build-win-canvas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson()))
                .andExpect(status().isOk())
                .andDo(document("build-win-canvas/save",
                        requestFields(
                                fieldWithPath("strategicGoal").description("전략 목표"),
                                fieldWithPath("taskName").description("과제명"),
                                fieldWithPath("taskDescription").description("주요 내용"),
                                fieldWithPath("crisisSignal").description("변화의 신호"),
                                fieldWithPath("painTouchPoint").description("페인 포인트"),
                                fieldWithPath("taskInputs[]").description("투입 자원 목록"),
                                fieldWithPath("taskInputs[].inputId").description("투입 ID (수정 시)"),
                                fieldWithPath("taskInputs[].resourceName").description("필요 자원"),
                                fieldWithPath("taskInputs[].quantity").description("수량"),
                                fieldWithPath("taskInputs[].orderNo").description("순서"),
                                fieldWithPath("taskActivities[]").description("활동 목록"),
                                fieldWithPath("taskActivities[].activityId").description("활동 ID (수정 시)"),
                                fieldWithPath("taskActivities[].processStep").description("전환 단계"),
                                fieldWithPath("taskActivities[].activityContent").description("전환 활동"),
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
                                fieldWithPath("data.crisisSignal").description("변화의 신호"),
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
                                fieldWithPath("data.taskActivities[].processStep").description("전환 단계"),
                                fieldWithPath("data.taskActivities[].activityContent").description("전환 활동"),
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
    @DisplayName("BuildWin 캔버스 저장 - 이미 제출 완료")
    void saveBuildWinCanvas_alreadySubmitted() throws Exception {
        authenticateAs(1L);
        given(buildWinCanvasService.saveBuildWinCanvas(anyLong(), any()))
                .willThrow(new ConflictException(ErrorCode.ALREADY_SUBMITTED));

        mockMvc.perform(post("/api/build-win-canvas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson()))
                .andExpect(status().isConflict())
                .andDo(document("build-win-canvas/save-already-submitted",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("CANVAS_1"),
                                fieldWithPath("data.message").description("이미 제출 완료된 캔버스입니다.")
                        )
                ));
    }

    // ===== 3. BuildWin 캔버스 제출 =====

    @Test
    @DisplayName("BuildWin 캔버스 제출")
    void submitBuildWinCanvas() throws Exception {
        authenticateAs(1L);
        given(buildWinCanvasService.submitBuildWinCanvas(anyLong(), any()))
                .willReturn(sampleResponse(true));

        mockMvc.perform(patch("/api/build-win-canvas/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson()))
                .andExpect(status().isOk())
                .andDo(document("build-win-canvas/submit",
                        requestFields(
                                fieldWithPath("strategicGoal").description("전략 목표"),
                                fieldWithPath("taskName").description("과제명"),
                                fieldWithPath("taskDescription").description("주요 내용"),
                                fieldWithPath("crisisSignal").description("변화의 신호"),
                                fieldWithPath("painTouchPoint").description("페인 포인트"),
                                fieldWithPath("taskInputs[]").description("투입 자원 목록"),
                                fieldWithPath("taskInputs[].inputId").description("투입 ID (수정 시)"),
                                fieldWithPath("taskInputs[].resourceName").description("필요 자원"),
                                fieldWithPath("taskInputs[].quantity").description("수량"),
                                fieldWithPath("taskInputs[].orderNo").description("순서"),
                                fieldWithPath("taskActivities[]").description("활동 목록"),
                                fieldWithPath("taskActivities[].activityId").description("활동 ID (수정 시)"),
                                fieldWithPath("taskActivities[].processStep").description("전환 단계"),
                                fieldWithPath("taskActivities[].activityContent").description("전환 활동"),
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
                                fieldWithPath("data.crisisSignal").description("변화의 신호"),
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
                                fieldWithPath("data.taskActivities[].processStep").description("전환 단계"),
                                fieldWithPath("data.taskActivities[].activityContent").description("전환 활동"),
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
    @DisplayName("BuildWin 캔버스 제출 - 이미 제출 완료")
    void submitBuildWinCanvas_alreadySubmitted() throws Exception {
        authenticateAs(1L);
        given(buildWinCanvasService.submitBuildWinCanvas(anyLong(), any()))
                .willThrow(new ConflictException(ErrorCode.ALREADY_SUBMITTED));

        mockMvc.perform(patch("/api/build-win-canvas/submit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleRequestJson()))
                .andExpect(status().isConflict())
                .andDo(document("build-win-canvas/submit-already-submitted",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("CANVAS_1"),
                                fieldWithPath("data.message").description("이미 제출 완료된 캔버스입니다.")
                        )
                ));
    }
}
