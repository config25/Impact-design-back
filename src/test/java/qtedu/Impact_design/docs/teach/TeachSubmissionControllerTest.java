package qtedu.Impact_design.docs.teach;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import qtedu.Impact_design.api.config.SecurityConfig;
import qtedu.Impact_design.api.controller.TeachSubmissionController;
import qtedu.Impact_design.api.dto.response.buildwin.BuildWinCanvasResponse;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.api.dto.response.funding.FundingInvestmentResponse;
import qtedu.Impact_design.api.dto.response.funding.FundingMyResultResponse;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.api.dto.response.impactcheck.ImpactCheckResponse;
import qtedu.Impact_design.api.dto.response.quickwin.QuickWinCanvasResponse;
import qtedu.Impact_design.api.dto.response.teach.TeamSubmissionListResponse;
import qtedu.Impact_design.api.util.security.JwtAuthenticationFilter;
import qtedu.Impact_design.domain.model.en.OutcomeType;
import qtedu.Impact_design.api.util.security.UserArgumentResolver;
import qtedu.Impact_design.docs.RestDocsTestSupport;
import qtedu.Impact_design.domain.service.TeachSubmissionService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = TeachSubmissionController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        ),
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = UserArgumentResolver.class
        )
)
class TeachSubmissionControllerTest extends RestDocsTestSupport {

    @MockitoBean
    private TeachSubmissionService teachSubmissionService;

    // ===== 1. 게임별 팀 제출 현황 목록 =====

    @Test
    @DisplayName("게임별 팀 제출 현황 목록")
    void getSubmissionList() throws Exception {
        given(teachSubmissionService.getSubmissionList(anyInt()))
                .willReturn(List.of(
                        TeamSubmissionListResponse.builder()
                                .teamId(1).teamName("팀1").sequence(1).numUser(3)
                                .submitA("Y").submitB("Y").submitC("N")
                                .submitD("N").submitE("N").submitF("N")
                                .build()
                ));

        mockMvc.perform(get("/api/teach/submission/list?gameId={gameId}", "1"))
                .andExpect(status().isOk())
                .andDo(document("teach-submission/list",
                        queryParameters(
                                parameterWithName("gameId").description("강의실 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data[].teamId").description("팀 ID"),
                                fieldWithPath("data[].teamName").description("팀 이름"),
                                fieldWithPath("data[].sequence").description("팀 순서 번호"),
                                fieldWithPath("data[].numUser").description("팀원 수"),
                                fieldWithPath("data[].submitA").description("A: 성과관리 현황진단 제출 여부"),
                                fieldWithPath("data[].submitB").description("B: 정체성 설계 제출 여부"),
                                fieldWithPath("data[].submitC").description("C: 성과경로 설계 제출 여부"),
                                fieldWithPath("data[].submitD").description("D: 전술적 실행과제 제출 여부"),
                                fieldWithPath("data[].submitE").description("E: 전략적 실행과제 제출 여부"),
                                fieldWithPath("data[].submitF").description("F: 실행과제 검증 제출 여부")
                        )
                ));
    }

    // ===== 2. A: 성과관리 현황진단 열람 =====

    @Test
    @DisplayName("A: 성과관리 현황진단 열람")
    void getImpactCheck() throws Exception {
        given(teachSubmissionService.getImpactCheck(anyInt()))
                .willReturn(ImpactCheckResponse.builder()
                        .answerId(1L)
                        .q1Score(4).q2Score(3).q3Score(5).q4Score(2)
                        .q5Score(4).q6Score(3).q7Score(5).q8Score(4)
                        .q9Score(3).q10Score(4).q11Score(5).q12Score(3)
                        .q13Text("답변13").q14Text("답변14")
                        .q15Text("답변15").q16Text("답변16")
                        .userId(10L).submitted(true)
                        .build());

        mockMvc.perform(get("/api/teach/submission/impact-check?teamId={teamId}", "1"))
                .andExpect(status().isOk())
                .andDo(document("teach-submission/impact-check",
                        queryParameters(
                                parameterWithName("teamId").description("팀 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.answerId").description("답변 ID"),
                                fieldWithPath("data.q1Score").description("문항1 점수"),
                                fieldWithPath("data.q2Score").description("문항2 점수"),
                                fieldWithPath("data.q3Score").description("문항3 점수"),
                                fieldWithPath("data.q4Score").description("문항4 점수"),
                                fieldWithPath("data.q5Score").description("문항5 점수"),
                                fieldWithPath("data.q6Score").description("문항6 점수"),
                                fieldWithPath("data.q7Score").description("문항7 점수"),
                                fieldWithPath("data.q8Score").description("문항8 점수"),
                                fieldWithPath("data.q9Score").description("문항9 점수"),
                                fieldWithPath("data.q10Score").description("문항10 점수"),
                                fieldWithPath("data.q11Score").description("문항11 점수"),
                                fieldWithPath("data.q12Score").description("문항12 점수"),
                                fieldWithPath("data.q13Text").description("문항13 텍스트"),
                                fieldWithPath("data.q14Text").description("문항14 텍스트"),
                                fieldWithPath("data.q15Text").description("문항15 텍스트"),
                                fieldWithPath("data.q16Text").description("문항16 텍스트"),
                                fieldWithPath("data.userId").description("작성자 ID"),
                                fieldWithPath("data.submitted").description("제출 여부")
                        )
                ));
    }

    // ===== 3. B: 정체성 설계 열람 =====

    @Test
    @DisplayName("B: 정체성 설계 열람")
    void getIdentityCanvas() throws Exception {
        given(teachSubmissionService.getIdentityCanvas(anyInt()))
                .willReturn(IdentityCanvasResponse.builder()
                        .identityId(1L)
                        .mission("사회적 가치 창출").vision("지속 가능한 미래")
                        .value("혁신과 협력").macro("인구 감소").tech("AI 기술")
                        .customer("청년층").competitor("타 사회적 기업")
                        .capability("기술력").culture("수평적 문화")
                        .structure("팀 기반").etc("기타 사항")
                        .newMission("새로운 미션").newVision("새로운 비전").newValue("새로운 가치")
                        .userId(10L).submitted(true)
                        .build());

        mockMvc.perform(get("/api/teach/submission/identity-canvas?teamId={teamId}", "1"))
                .andExpect(status().isOk())
                .andDo(document("teach-submission/identity-canvas",
                        queryParameters(
                                parameterWithName("teamId").description("팀 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.identityId").description("Identity ID"),
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
                                fieldWithPath("data.userId").description("작성자 ID"),
                                fieldWithPath("data.submitted").description("제출 여부")
                        )
                ));
    }

    // ===== 4. C: 성과경로 설계 열람 =====

    @Test
    @DisplayName("C: 성과경로 설계 열람")
    void getFlowCanvas() throws Exception {
        FlowCanvasResponse.TacticalItem tactical = FlowCanvasResponse.TacticalItem.builder()
                .metricId(1L).tacticalMetric("고객 만족도").tacticalGoal("90% 이상").orderNo(1).build();
        FlowCanvasResponse.StrategicActivityItem activity = FlowCanvasResponse.StrategicActivityItem.builder()
                .activityId(1L).activityMetric("서비스 개선 횟수").interCriteria("월 2회 이상").orderNo(1).build();
        FlowCanvasResponse.GoalItem goal = FlowCanvasResponse.GoalItem.builder()
                .goalId(1L).goalTitle("사회적 가치 창출").goalDescription("지역 사회 문제 해결")
                .orderNo(1).tacticals(List.of(tactical)).strategicActivities(List.of(activity)).build();

        given(teachSubmissionService.getFlowCanvas(anyInt()))
                .willReturn(FlowCanvasResponse.builder()
                        .newVision("지속 가능한 미래").goals(List.of(goal)).submitted(true).build());

        mockMvc.perform(get("/api/teach/submission/flow-canvas?teamId={teamId}", "1"))
                .andExpect(status().isOk())
                .andDo(document("teach-submission/flow-canvas",
                        queryParameters(
                                parameterWithName("teamId").description("팀 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.newVision").description("새로운 비전").optional(),
                                fieldWithPath("data.goals[]").description("목표 목록"),
                                fieldWithPath("data.goals[].goalId").description("목표 ID"),
                                fieldWithPath("data.goals[].goalTitle").description("목표 제목"),
                                fieldWithPath("data.goals[].goalDescription").description("존재 이유"),
                                fieldWithPath("data.goals[].orderNo").description("목표 순서"),
                                fieldWithPath("data.goals[].tacticals[]").description("전술적 성과 목록"),
                                fieldWithPath("data.goals[].tacticals[].metricId").description("지표 ID"),
                                fieldWithPath("data.goals[].tacticals[].tacticalMetric").description("전술적 성과 지표"),
                                fieldWithPath("data.goals[].tacticals[].tacticalGoal").description("전술 목표"),
                                fieldWithPath("data.goals[].tacticals[].orderNo").description("성과 순서"),
                                fieldWithPath("data.goals[].strategicActivities[]").description("전략적 활동 목록"),
                                fieldWithPath("data.goals[].strategicActivities[].activityId").description("활동 ID"),
                                fieldWithPath("data.goals[].strategicActivities[].activityMetric").description("전략적 활동 지표"),
                                fieldWithPath("data.goals[].strategicActivities[].interCriteria").description("내재화 기준"),
                                fieldWithPath("data.goals[].strategicActivities[].orderNo").description("활동 순서"),
                                fieldWithPath("data.submitted").description("제출 여부")
                        )
                ));
    }

    // ===== 5. D: 전술적 실행과제 열람 =====

    @Test
    @DisplayName("D: 전술적 실행과제 (QuickWin) 열람")
    void getQuickWinCanvas() throws Exception {
        given(teachSubmissionService.getQuickWinCanvas(anyInt()))
                .willReturn(QuickWinCanvasResponse.builder()
                        .canvasId(1L).strategicGoal("전략 목표").taskName("과제명")
                        .taskDescription("주요 내용").crisisSignal("위기의 신호").painTouchPoint("페인 포인트")
                        .userId(10L).submitted(true)
                        .taskInputs(List.of(QuickWinCanvasResponse.TaskInputItem.builder()
                                .inputId(1L).resourceName("인적 자원").quantity(3).orderNo(1).build()))
                        .taskActivities(List.of(QuickWinCanvasResponse.TaskActivityItem.builder()
                                .activityId(1L).processStep("1단계").activityContent("활동 내용").duration("2주").orderNo(1).build()))
                        .teamwork(QuickWinCanvasResponse.TeamworkItem.builder()
                                .teamworkId(1L).activityTeamwork("협업 내용").workType("협업").build())
                        .taskOutcomes(List.of(QuickWinCanvasResponse.TaskOutcomeItem.builder()
                                .outcomeNo(1L).outcomeType(OutcomeType.QUALITATIVE)
                                .outcomeContent("산출물").orderNo(1).build()))
                        .build());

        mockMvc.perform(get("/api/teach/submission/quick-win?teamId={teamId}", "1"))
                .andExpect(status().isOk())
                .andDo(document("teach-submission/quick-win",
                        queryParameters(
                                parameterWithName("teamId").description("팀 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.canvasId").description("캔버스 ID"),
                                fieldWithPath("data.strategicGoal").description("전략 목표").optional(),
                                fieldWithPath("data.taskName").description("과제명").optional(),
                                fieldWithPath("data.taskDescription").description("주요 내용").optional(),
                                fieldWithPath("data.crisisSignal").description("위기의 신호").optional(),
                                fieldWithPath("data.painTouchPoint").description("페인/터치포인트").optional(),
                                fieldWithPath("data.userId").description("작성자 ID"),
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
                                fieldWithPath("data.teamwork").description("팀워크 정보").optional(),
                                fieldWithPath("data.teamwork.teamworkId").description("팀워크 ID"),
                                fieldWithPath("data.teamwork.activityTeamwork").description("활동 팀워크"),
                                fieldWithPath("data.teamwork.workType").description("산출 팀워크"),
                                fieldWithPath("data.taskOutcomes[]").description("산출물 목록"),
                                fieldWithPath("data.taskOutcomes[].outcomeNo").description("산출물 번호"),
                                fieldWithPath("data.taskOutcomes[].outcomeType").description("산출물 유형 (QUALITATIVE/QUANTITATIVE)"),
                                fieldWithPath("data.taskOutcomes[].outcomeContent").description("산출물 내용"),
                                fieldWithPath("data.taskOutcomes[].orderNo").description("순서")
                        )
                ));
    }

    // ===== 6. E: 전략적 실행과제 열람 =====

    @Test
    @DisplayName("E: 전략적 실행과제 (BuildWin) 열람")
    void getBuildWinCanvas() throws Exception {
        given(teachSubmissionService.getBuildWinCanvas(anyInt()))
                .willReturn(BuildWinCanvasResponse.builder()
                        .canvasId(1L).strategicGoal("전략 목표").taskName("과제명")
                        .taskDescription("주요 내용").crisisSignal("변화의 신호").painTouchPoint("페인포인트")
                        .userId(10L).submitted(true)
                        .taskInputs(List.of(BuildWinCanvasResponse.TaskInputItem.builder()
                                .inputId(1L).resourceName("인적 자원").quantity(3).orderNo(1).build()))
                        .taskActivities(List.of(BuildWinCanvasResponse.TaskActivityItem.builder()
                                .activityId(1L).processStep("1단계").activityContent("활동 내용").duration("2주").orderNo(1).build()))
                        .teamwork(BuildWinCanvasResponse.TeamworkItem.builder()
                                .teamworkId(1L).activityTeamwork("협업 내용").workType("협업").build())
                        .taskOutcomes(List.of(BuildWinCanvasResponse.TaskOutcomeItem.builder()
                                .outcomeNo(1L).outcomeType(OutcomeType.QUALITATIVE)
                                .outcomeContent("산출물").orderNo(1).build()))
                        .build());

        mockMvc.perform(get("/api/teach/submission/build-win?teamId={teamId}", "1"))
                .andExpect(status().isOk())
                .andDo(document("teach-submission/build-win",
                        queryParameters(
                                parameterWithName("teamId").description("팀 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.canvasId").description("캔버스 ID"),
                                fieldWithPath("data.strategicGoal").description("전략 목표").optional(),
                                fieldWithPath("data.taskName").description("과제명").optional(),
                                fieldWithPath("data.taskDescription").description("주요 내용").optional(),
                                fieldWithPath("data.crisisSignal").description("변화의 신호").optional(),
                                fieldWithPath("data.painTouchPoint").description("페인/터치포인트").optional(),
                                fieldWithPath("data.userId").description("작성자 ID"),
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
                                fieldWithPath("data.teamwork").description("팀워크 정보").optional(),
                                fieldWithPath("data.teamwork.teamworkId").description("팀워크 ID"),
                                fieldWithPath("data.teamwork.activityTeamwork").description("활동 팀워크"),
                                fieldWithPath("data.teamwork.workType").description("산출 팀워크"),
                                fieldWithPath("data.taskOutcomes[]").description("산출물 목록"),
                                fieldWithPath("data.taskOutcomes[].outcomeNo").description("산출물 번호"),
                                fieldWithPath("data.taskOutcomes[].outcomeType").description("산출물 유형 (QUALITATIVE/QUANTITATIVE)"),
                                fieldWithPath("data.taskOutcomes[].outcomeContent").description("산출물 내용"),
                                fieldWithPath("data.taskOutcomes[].orderNo").description("순서")
                        )
                ));
    }

    // ===== 7. F: 실행과제 검증 열람 =====

    @Test
    @DisplayName("F: 실행과제 검증 (Funding) 열람")
    void getFunding() throws Exception {
        given(teachSubmissionService.getFunding(anyString(), anyInt()))
                .willReturn(List.of(
                        FundingInvestmentResponse.builder()
                                .intentIndex(1).investmentTarget(2)
                                .teamName("팀2").businessName("사업명")
                                .investmentPrice("1000000")
                                .score1(4).score2(3).score3(5).score4(4)
                                .score5(3).score6(4).score7(5).score8(3).score9(4)
                                .opinion("좋은 사업입니다").submitted(true)
                                .build()
                ));

        mockMvc.perform(get("/api/teach/submission/funding/{canvasType}?teamId={teamId}", "build", "1"))
                .andExpect(status().isOk())
                .andDo(document("teach-submission/funding",
                        pathParameters(
                                parameterWithName("canvasType").description("캔버스 유형 (build 또는 quick)")
                        ),
                        queryParameters(
                                parameterWithName("teamId").description("팀 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data[].intentIndex").description("투자 의향서 인덱스").optional(),
                                fieldWithPath("data[].investmentTarget").description("투자 대상 팀 ID"),
                                fieldWithPath("data[].teamName").description("대상 팀 이름"),
                                fieldWithPath("data[].businessName").description("사업명"),
                                fieldWithPath("data[].investmentPrice").description("투자 금액"),
                                fieldWithPath("data[].score1").description("평가 점수 1"),
                                fieldWithPath("data[].score2").description("평가 점수 2"),
                                fieldWithPath("data[].score3").description("평가 점수 3"),
                                fieldWithPath("data[].score4").description("평가 점수 4"),
                                fieldWithPath("data[].score5").description("평가 점수 5"),
                                fieldWithPath("data[].score6").description("평가 점수 6"),
                                fieldWithPath("data[].score7").description("평가 점수 7"),
                                fieldWithPath("data[].score8").description("평가 점수 8"),
                                fieldWithPath("data[].score9").description("평가 점수 9"),
                                fieldWithPath("data[].opinion").description("투자 의견"),
                                fieldWithPath("data[].submitted").description("제출 여부")
                        )
                ));
    }

    // ===== 8. F-3: 팀별 결과 열람 =====

    @Test
    @DisplayName("F-3: 팀별 결과 열람")
    void getFundingResult() throws Exception {
        FundingMyResultResponse.ScoreResult scoreResult = FundingMyResultResponse.ScoreResult.builder()
                .problemScore(7).solutionScore(12).scaleUpScore(9)
                .effectScore(7).totalScore(35).evaluatorCount(3)
                .build();

        given(teachSubmissionService.getFundingResult(anyInt()))
                .willReturn(FundingMyResultResponse.builder()
                        .build(FundingMyResultResponse.ResultSection.builder()
                                .scores(scoreResult)
                                .opinions(List.of("좋은 사업입니다", "발전 가능성이 높습니다"))
                                .build())
                        .quick(FundingMyResultResponse.ResultSection.builder()
                                .scores(FundingMyResultResponse.ScoreResult.empty())
                                .opinions(List.of())
                                .build())
                        .build());

        mockMvc.perform(get("/api/teach/submission/funding/result?teamId={teamId}", "1"))
                .andExpect(status().isOk())
                .andDo(document("teach-submission/funding-result",
                        queryParameters(
                                parameterWithName("teamId").description("팀 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.build").description("BUILD 캔버스 결과"),
                                fieldWithPath("data.build.scores.problemScore").description("문제 점수 (score1+score2)"),
                                fieldWithPath("data.build.scores.solutionScore").description("솔루션 점수 (score3+score4+score5)"),
                                fieldWithPath("data.build.scores.scaleUpScore").description("확장 점수 (score6+score7)"),
                                fieldWithPath("data.build.scores.effectScore").description("효과 점수 (score8+score9)"),
                                fieldWithPath("data.build.scores.totalScore").description("총 점수"),
                                fieldWithPath("data.build.scores.evaluatorCount").description("평가 참가자 수"),
                                fieldWithPath("data.build.opinions[]").description("투자 의견 목록"),
                                fieldWithPath("data.quick").description("QUICK 캔버스 결과"),
                                fieldWithPath("data.quick.scores.problemScore").description("문제정의 점수"),
                                fieldWithPath("data.quick.scores.solutionScore").description("솔루션 점수"),
                                fieldWithPath("data.quick.scores.scaleUpScore").description("실행력 점수"),
                                fieldWithPath("data.quick.scores.effectScore").description("파급효과 점수"),
                                fieldWithPath("data.quick.scores.totalScore").description("총 점수"),
                                fieldWithPath("data.quick.scores.evaluatorCount").description("평가 참가자 수"),
                                fieldWithPath("data.quick.opinions[]").description("투자 의견 목록")
                        )
                ));
    }
}
