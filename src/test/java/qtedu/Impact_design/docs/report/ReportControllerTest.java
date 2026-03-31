package qtedu.Impact_design.docs.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import qtedu.Impact_design.api.config.SecurityConfig;
import qtedu.Impact_design.api.controller.ReportController;
import qtedu.Impact_design.api.dto.response.report.ReportResponse;
import qtedu.Impact_design.api.dto.response.report.TeamCanvasResponse;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.api.dto.response.wincanvas.WinCanvasResponse;
import qtedu.Impact_design.api.util.security.JwtAuthenticationFilter;
import qtedu.Impact_design.api.util.security.UserArgumentResolver;
import qtedu.Impact_design.docs.RestDocsTestSupport;
import qtedu.Impact_design.domain.implementation.report.ReportFacade;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ReportController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        ),
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = UserArgumentResolver.class
        )
)
class ReportControllerTest extends RestDocsTestSupport {

    @MockitoBean
    private ReportFacade reportFacade;

    @Test
    @DisplayName("팀별 리포트 조회")
    void getReport() throws Exception {
        authenticateAs(1L);

        ReportResponse.ScoreDetail scoreDetail = ReportResponse.ScoreDetail.builder()
                .intentIndex(1).userId(10L)
                .score1(4).score2(3).score3(5).score4(4).score5(3)
                .score6(4).score7(5).score8(3).score9(4).totalScore(35)
                .build();

        ReportResponse response = ReportResponse.builder()
                .teamId(1)
                .teamName("1팀")
                .className("임팩트 디자인 수업")
                .target("대학생")
                .projectDate("2026-06-30")
                .imageUrl("http://iptdesign.mycafe24.com/uploads/class/1/image.png")
                .impactCheckScores(List.of(
                        ReportResponse.ImpactCheckScoreItem.builder()
                                .userId(10L)
                                .q1Score(4).q2Score(3).q3Score(5).q4Score(2)
                                .q5Score(4).q6Score(3).q7Score(5).q8Score(4)
                                .q9Score(3).q10Score(4).q11Score(5).q12Score(3)
                                .build()
                ))
                .externalThreats(ReportResponse.FrequencyAnalysis.builder()
                        .allData(List.of("인구 감소", "기후 변화", "인구 감소"))
                        .totalCount(3)
                        .top4(List.of(ReportResponse.FrequencyItem.builder()
                                .content("인구 감소").count(2).build()))
                        .keywords(List.of("인구", "기후"))
                        .aiSummary(List.of("인구 감소가 가장 큰 위협입니다."))
                        .build())
                .internalLimitations(ReportResponse.FrequencyAnalysis.builder()
                        .allData(List.of("자금 부족"))
                        .totalCount(1)
                        .top4(List.of(ReportResponse.FrequencyItem.builder()
                                .content("자금 부족").count(1).build()))
                        .keywords(List.of("자금"))
                        .aiSummary(List.of("자금 부족이 주요 내부 한계입니다."))
                        .build())
                .visionMissionValue(ReportResponse.VisionMissionValue.builder()
                        .visionTop4(List.of(ReportResponse.FrequencyItem.builder()
                                .content("지속 가능한 미래").count(3).build()))
                        .missionTop4(List.of(ReportResponse.FrequencyItem.builder()
                                .content("사회적 가치 창출").count(2).build()))
                        .valueTop4(List.of(ReportResponse.FrequencyItem.builder()
                                .content("혁신").count(2).build()))
                        .aiVision("지속 가능한 미래를 향한 비전")
                        .aiMission("사회적 가치를 창출하는 미션")
                        .aiValue("혁신과 협력의 가치")
                        .build())
                .flowCanvasGoals(ReportResponse.GoalAnalysis.builder()
                        .goals(List.of(ReportResponse.GoalItem.builder()
                                .title("사회적 가치 창출").description("지역 사회 문제 해결").count(2)
                                .build()))
                        .totalCount(1)
                        .keywords(List.of("사회적 가치", "지역 사회"))
                        .build())
                .tacticals(List.of(ReportResponse.TacticalItem.builder()
                        .metric("고객 만족도").goal("90% 이상").build()))
                .strategicActivities(List.of(ReportResponse.StrategicActivityItem.builder()
                        .activityMetric("서비스 개선 횟수").interCriteria("월 2회 이상").build()))
                .quickWinCanvasList(List.of(ReportResponse.WinCanvasWithScore.builder()
                        .canvasId(1L).userId(10L).taskName("전술 과제")
                        .taskDescription("과제 설명").totalScore(35)
                        .evaluations(List.of(scoreDetail))
                        .build()))
                .buildWinCanvasList(List.of(ReportResponse.WinCanvasWithScore.builder()
                        .canvasId(2L).userId(10L).taskName("전략 과제")
                        .taskDescription("과제 설명").totalScore(38)
                        .evaluations(List.of(scoreDetail))
                        .build()))
                .build();

        given(reportFacade.getReport(anyInt())).willReturn(response);

        mockMvc.perform(get("/api/teach/report/{teamId}", 1))
                .andExpect(status().isOk())
                .andDo(document("report/get",
                        pathParameters(
                                parameterWithName("teamId").description("팀 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),

                                // 팀 정보
                                fieldWithPath("data.teamId").description("팀 ID").optional(),
                                fieldWithPath("data.teamName").description("팀 이름").optional(),

                                // 기본 정보
                                fieldWithPath("data.className").description("강의실 이름"),
                                fieldWithPath("data.target").description("대상").optional(),
                                fieldWithPath("data.projectDate").description("프로젝트 일자").optional(),
                                fieldWithPath("data.imageUrl").description("강의실 이미지 URL").optional(),

                                // Impact Check 점수
                                fieldWithPath("data.impactCheckScores[]").description("임팩트 체크 점수 목록"),
                                fieldWithPath("data.impactCheckScores[].userId").description("사용자 ID"),
                                fieldWithPath("data.impactCheckScores[].q1Score").description("문항1 점수"),
                                fieldWithPath("data.impactCheckScores[].q2Score").description("문항2 점수"),
                                fieldWithPath("data.impactCheckScores[].q3Score").description("문항3 점수"),
                                fieldWithPath("data.impactCheckScores[].q4Score").description("문항4 점수"),
                                fieldWithPath("data.impactCheckScores[].q5Score").description("문항5 점수"),
                                fieldWithPath("data.impactCheckScores[].q6Score").description("문항6 점수"),
                                fieldWithPath("data.impactCheckScores[].q7Score").description("문항7 점수"),
                                fieldWithPath("data.impactCheckScores[].q8Score").description("문항8 점수"),
                                fieldWithPath("data.impactCheckScores[].q9Score").description("문항9 점수"),
                                fieldWithPath("data.impactCheckScores[].q10Score").description("문항10 점수"),
                                fieldWithPath("data.impactCheckScores[].q11Score").description("문항11 점수"),
                                fieldWithPath("data.impactCheckScores[].q12Score").description("문항12 점수"),

                                // 외부 위협 빈도 분석
                                fieldWithPath("data.externalThreats").description("외부 위협 빈도 분석"),
                                fieldWithPath("data.externalThreats.allData[]").description("전체 데이터"),
                                fieldWithPath("data.externalThreats.totalCount").description("전체 의견 개수"),
                                fieldWithPath("data.externalThreats.top4[]").description("상위 4개"),
                                fieldWithPath("data.externalThreats.top4[].content").description("내용"),
                                fieldWithPath("data.externalThreats.top4[].count").description("빈도 수"),
                                fieldWithPath("data.externalThreats.keywords[]").description("키워드 목록"),
                                fieldWithPath("data.externalThreats.aiSummary[]").description("AI 요약"),

                                // 내부 한계 빈도 분석
                                fieldWithPath("data.internalLimitations").description("내부 한계 빈도 분석"),
                                fieldWithPath("data.internalLimitations.allData[]").description("전체 데이터"),
                                fieldWithPath("data.internalLimitations.totalCount").description("전체 의견 개수"),
                                fieldWithPath("data.internalLimitations.top4[]").description("상위 4개"),
                                fieldWithPath("data.internalLimitations.top4[].content").description("내용"),
                                fieldWithPath("data.internalLimitations.top4[].count").description("빈도 수"),
                                fieldWithPath("data.internalLimitations.keywords[]").description("키워드 목록"),
                                fieldWithPath("data.internalLimitations.aiSummary[]").description("AI 요약"),

                                // 비전/미션/가치
                                fieldWithPath("data.visionMissionValue").description("비전/미션/가치 분석"),
                                fieldWithPath("data.visionMissionValue.visionTop4[]").description("비전 상위 4개"),
                                fieldWithPath("data.visionMissionValue.visionTop4[].content").description("내용"),
                                fieldWithPath("data.visionMissionValue.visionTop4[].count").description("빈도 수"),
                                fieldWithPath("data.visionMissionValue.missionTop4[]").description("미션 상위 4개"),
                                fieldWithPath("data.visionMissionValue.missionTop4[].content").description("내용"),
                                fieldWithPath("data.visionMissionValue.missionTop4[].count").description("빈도 수"),
                                fieldWithPath("data.visionMissionValue.valueTop4[]").description("가치 상위 4개"),
                                fieldWithPath("data.visionMissionValue.valueTop4[].content").description("내용"),
                                fieldWithPath("data.visionMissionValue.valueTop4[].count").description("빈도 수"),
                                fieldWithPath("data.visionMissionValue.aiVision").description("AI 비전 요약").optional(),
                                fieldWithPath("data.visionMissionValue.aiMission").description("AI 미션 요약").optional(),
                                fieldWithPath("data.visionMissionValue.aiValue").description("AI 가치 요약").optional(),

                                // Flow Canvas 목표 분석
                                fieldWithPath("data.flowCanvasGoals").description("성과경로 목표 분석"),
                                fieldWithPath("data.flowCanvasGoals.goals[]").description("목표 목록"),
                                fieldWithPath("data.flowCanvasGoals.goals[].title").description("목표 제목"),
                                fieldWithPath("data.flowCanvasGoals.goals[].description").description("목표 설명"),
                                fieldWithPath("data.flowCanvasGoals.goals[].count").description("빈도 수"),
                                fieldWithPath("data.flowCanvasGoals.totalCount").description("전체 목표 개수"),
                                fieldWithPath("data.flowCanvasGoals.keywords[]").description("키워드 목록"),

                                // 전술 지표
                                fieldWithPath("data.tacticals[]").description("전술 지표 목록"),
                                fieldWithPath("data.tacticals[].metric").description("전술 지표"),
                                fieldWithPath("data.tacticals[].goal").description("전술 목표"),

                                // 전략 활동
                                fieldWithPath("data.strategicActivities[]").description("전략 활동 목록"),
                                fieldWithPath("data.strategicActivities[].activityMetric").description("활동 지표"),
                                fieldWithPath("data.strategicActivities[].interCriteria").description("중간 기준"),

                                // QuickWin 캔버스
                                fieldWithPath("data.quickWinCanvasList[]").description("QuickWin 캔버스 목록"),
                                fieldWithPath("data.quickWinCanvasList[].canvasId").description("캔버스 ID"),
                                fieldWithPath("data.quickWinCanvasList[].userId").description("작성자 ID"),
                                fieldWithPath("data.quickWinCanvasList[].taskName").description("과제명"),
                                fieldWithPath("data.quickWinCanvasList[].taskDescription").description("과제 설명"),
                                fieldWithPath("data.quickWinCanvasList[].totalScore").description("총 점수"),
                                fieldWithPath("data.quickWinCanvasList[].evaluations[]").description("평가 목록"),
                                fieldWithPath("data.quickWinCanvasList[].evaluations[].intentIndex").description("투자 의향서 인덱스"),
                                fieldWithPath("data.quickWinCanvasList[].evaluations[].userId").description("학생 번호"),
                                fieldWithPath("data.quickWinCanvasList[].evaluations[].score1").description("점수1"),
                                fieldWithPath("data.quickWinCanvasList[].evaluations[].score2").description("점수2"),
                                fieldWithPath("data.quickWinCanvasList[].evaluations[].score3").description("점수3"),
                                fieldWithPath("data.quickWinCanvasList[].evaluations[].score4").description("점수4"),
                                fieldWithPath("data.quickWinCanvasList[].evaluations[].score5").description("점수5"),
                                fieldWithPath("data.quickWinCanvasList[].evaluations[].score6").description("점수6"),
                                fieldWithPath("data.quickWinCanvasList[].evaluations[].score7").description("점수7"),
                                fieldWithPath("data.quickWinCanvasList[].evaluations[].score8").description("점수8"),
                                fieldWithPath("data.quickWinCanvasList[].evaluations[].score9").description("점수9"),
                                fieldWithPath("data.quickWinCanvasList[].evaluations[].totalScore").description("총 점수"),

                                // BuildWin 캔버스
                                fieldWithPath("data.buildWinCanvasList[]").description("BuildWin 캔버스 목록"),
                                fieldWithPath("data.buildWinCanvasList[].canvasId").description("캔버스 ID"),
                                fieldWithPath("data.buildWinCanvasList[].userId").description("작성자 ID"),
                                fieldWithPath("data.buildWinCanvasList[].taskName").description("과제명"),
                                fieldWithPath("data.buildWinCanvasList[].taskDescription").description("과제 설명"),
                                fieldWithPath("data.buildWinCanvasList[].totalScore").description("총 점수"),
                                fieldWithPath("data.buildWinCanvasList[].evaluations[]").description("평가 목록"),
                                fieldWithPath("data.buildWinCanvasList[].evaluations[].intentIndex").description("투자 의향서 인덱스"),
                                fieldWithPath("data.buildWinCanvasList[].evaluations[].userId").description("학생 번호"),
                                fieldWithPath("data.buildWinCanvasList[].evaluations[].score1").description("점수1"),
                                fieldWithPath("data.buildWinCanvasList[].evaluations[].score2").description("점수2"),
                                fieldWithPath("data.buildWinCanvasList[].evaluations[].score3").description("점수3"),
                                fieldWithPath("data.buildWinCanvasList[].evaluations[].score4").description("점수4"),
                                fieldWithPath("data.buildWinCanvasList[].evaluations[].score5").description("점수5"),
                                fieldWithPath("data.buildWinCanvasList[].evaluations[].score6").description("점수6"),
                                fieldWithPath("data.buildWinCanvasList[].evaluations[].score7").description("점수7"),
                                fieldWithPath("data.buildWinCanvasList[].evaluations[].score8").description("점수8"),
                                fieldWithPath("data.buildWinCanvasList[].evaluations[].score9").description("점수9"),
                                fieldWithPath("data.buildWinCanvasList[].evaluations[].totalScore").description("총 점수")
                        )
                ));
    }

    @Test
    @DisplayName("게임별 팀 캔버스 조회")
    void getTeamCanvases() throws Exception {
        authenticateAs(1L);

        TeamCanvasResponse response = TeamCanvasResponse.builder()
                .teamId(1)
                .teamName("1팀")
                .writerUserId(10L)
                .imageUrl("http://iptdesign.mycafe24.com/uploads/class/1/image.png")
                .identityCanvas(IdentityCanvasResponse.builder()
                        .identityId(1L).userId(10L).submitted(true)
                        .mission("기존 미션").vision("기존 비전").value("기존 가치")
                        .macro("인구 감소").tech("AI 마케팅").customer("MZ세대").competitor("BYD")
                        .capability("역량").culture("문화").structure("구조").etc("기타")
                        .newMission("새 미션").newVision("새 비전").newValue("새 가치")
                        .build())
                .flowCanvas(FlowCanvasResponse.builder()
                        .newVision("새 비전")
                        .submitted(true)
                        .goals(List.of(FlowCanvasResponse.GoalItem.builder()
                                .goalId(1L).goalTitle("사회적 가치 창출").goalDescription("지역 사회 문제 해결").orderNo(1)
                                .tacticals(List.of(FlowCanvasResponse.TacticalItem.builder()
                                        .metricId(1L).tacticalMetric("고객 만족도").tacticalGoal("90%").orderNo(1).build()))
                                .strategicActivities(List.of(FlowCanvasResponse.StrategicActivityItem.builder()
                                        .activityId(1L).activityMetric("서비스 개선").interCriteria("월 2회").orderNo(1).build()))
                                .build()))
                        .build())
                .quickWinCanvas(WinCanvasResponse.builder()
                        .canvasId(1L).userId(10L).submitted(true)
                        .strategicGoal("전략 목표").taskName("전술 과제")
                        .taskDescription("과제 설명").crisisSignal("위기 신호").painTouchPoint("고충점")
                        .taskInputs(List.of(WinCanvasResponse.TaskInputItem.builder()
                                .inputId(1L).resourceName("인력").quantity(3).orderNo(1).build()))
                        .taskActivities(List.of(WinCanvasResponse.TaskActivityItem.builder()
                                .activityId(1L).processStep("기획").activityContent("시장 조사").duration("2주").orderNo(1).build()))
                        .teamwork(WinCanvasResponse.TeamworkItem.builder()
                                .teamworkId(1L).activityTeamwork("협업 내용").workType("공동 작업").build())
                        .taskOutcomes(List.of(WinCanvasResponse.TaskOutcomeItem.builder()
                                .outcomeNo(1L).outcomeType(null).outcomeContent("고객 만족도 향상").orderNo(1).build()))
                        .build())
                .buildWinCanvas(WinCanvasResponse.builder()
                        .canvasId(2L).userId(10L).submitted(true)
                        .strategicGoal("전략 목표").taskName("전략 과제")
                        .taskDescription("과제 설명").crisisSignal("위기 신호").painTouchPoint("고충점")
                        .taskInputs(List.of(WinCanvasResponse.TaskInputItem.builder()
                                .inputId(2L).resourceName("예산").quantity(5).orderNo(1).build()))
                        .taskActivities(List.of(WinCanvasResponse.TaskActivityItem.builder()
                                .activityId(2L).processStep("실행").activityContent("서비스 개선").duration("4주").orderNo(1).build()))
                        .teamwork(WinCanvasResponse.TeamworkItem.builder()
                                .teamworkId(2L).activityTeamwork("팀워크 내용").workType("분담 작업").build())
                        .taskOutcomes(List.of(WinCanvasResponse.TaskOutcomeItem.builder()
                                .outcomeNo(2L).outcomeType(null).outcomeContent("매출 10% 증가").orderNo(1).build()))
                        .build())
                .build();

        given(reportFacade.getTeamCanvases(anyInt())).willReturn(List.of(response));

        mockMvc.perform(get("/api/teach/report/canvas/{gameId}", 1))
                .andExpect(status().isOk())
                .andDo(document("report/get-team-canvases",
                        pathParameters(
                                parameterWithName("gameId").description("게임 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data[].teamId").description("팀 ID"),
                                fieldWithPath("data[].teamName").description("팀 이름").optional(),
                                fieldWithPath("data[].writerUserId").description("대표작성자 ID"),
                                fieldWithPath("data[].imageUrl").description("강의실 이미지 URL").optional(),

                                // Identity Canvas
                                fieldWithPath("data[].identityCanvas").description("정체성 캔버스").optional(),
                                fieldWithPath("data[].identityCanvas.identityId").description("정체성 캔버스 ID"),
                                fieldWithPath("data[].identityCanvas.mission").description("미션").optional(),
                                fieldWithPath("data[].identityCanvas.vision").description("비전").optional(),
                                fieldWithPath("data[].identityCanvas.value").description("가치").optional(),
                                fieldWithPath("data[].identityCanvas.macro").description("거시 환경").optional(),
                                fieldWithPath("data[].identityCanvas.tech").description("기술 환경").optional(),
                                fieldWithPath("data[].identityCanvas.customer").description("고객").optional(),
                                fieldWithPath("data[].identityCanvas.competitor").description("경쟁자").optional(),
                                fieldWithPath("data[].identityCanvas.capability").description("역량").optional(),
                                fieldWithPath("data[].identityCanvas.culture").description("문화").optional(),
                                fieldWithPath("data[].identityCanvas.structure").description("구조").optional(),
                                fieldWithPath("data[].identityCanvas.etc").description("기타").optional(),
                                fieldWithPath("data[].identityCanvas.newMission").description("새 미션").optional(),
                                fieldWithPath("data[].identityCanvas.newVision").description("새 비전").optional(),
                                fieldWithPath("data[].identityCanvas.newValue").description("새 가치").optional(),
                                fieldWithPath("data[].identityCanvas.userId").description("작성자 ID"),
                                fieldWithPath("data[].identityCanvas.submitted").description("제출 여부"),

                                // Flow Canvas
                                fieldWithPath("data[].flowCanvas").description("성과경로 캔버스"),
                                fieldWithPath("data[].flowCanvas.newVision").description("새 비전").optional(),
                                fieldWithPath("data[].flowCanvas.submitted").description("제출 여부").optional(),
                                fieldWithPath("data[].flowCanvas.goals[]").description("목표 목록"),
                                fieldWithPath("data[].flowCanvas.goals[].goalId").description("목표 ID"),
                                fieldWithPath("data[].flowCanvas.goals[].goalTitle").description("목표 제목").optional(),
                                fieldWithPath("data[].flowCanvas.goals[].goalDescription").description("목표 설명").optional(),
                                fieldWithPath("data[].flowCanvas.goals[].orderNo").description("순서"),
                                fieldWithPath("data[].flowCanvas.goals[].tacticals[]").description("전술 지표 목록"),
                                fieldWithPath("data[].flowCanvas.goals[].tacticals[].metricId").description("지표 ID"),
                                fieldWithPath("data[].flowCanvas.goals[].tacticals[].tacticalMetric").description("전술 지표"),
                                fieldWithPath("data[].flowCanvas.goals[].tacticals[].tacticalGoal").description("전술 목표"),
                                fieldWithPath("data[].flowCanvas.goals[].tacticals[].orderNo").description("순서"),
                                fieldWithPath("data[].flowCanvas.goals[].strategicActivities[]").description("전략적 행동 목록"),
                                fieldWithPath("data[].flowCanvas.goals[].strategicActivities[].activityId").description("활동 ID"),
                                fieldWithPath("data[].flowCanvas.goals[].strategicActivities[].activityMetric").description("활동 지표"),
                                fieldWithPath("data[].flowCanvas.goals[].strategicActivities[].interCriteria").description("중간 기준"),
                                fieldWithPath("data[].flowCanvas.goals[].strategicActivities[].orderNo").description("순서"),

                                // Quick Win Canvas
                                fieldWithPath("data[].quickWinCanvas").description("전술적 실행과제 캔버스").optional(),
                                fieldWithPath("data[].quickWinCanvas.canvasId").description("캔버스 ID").optional(),
                                fieldWithPath("data[].quickWinCanvas.strategicGoal").description("전략 목표").optional(),
                                fieldWithPath("data[].quickWinCanvas.taskName").description("과제명").optional(),
                                fieldWithPath("data[].quickWinCanvas.taskDescription").description("과제 설명").optional(),
                                fieldWithPath("data[].quickWinCanvas.crisisSignal").description("위기 신호").optional(),
                                fieldWithPath("data[].quickWinCanvas.painTouchPoint").description("고충점").optional(),
                                fieldWithPath("data[].quickWinCanvas.userId").description("작성자 ID").optional(),
                                fieldWithPath("data[].quickWinCanvas.submitted").description("제출 여부").optional(),
                                fieldWithPath("data[].quickWinCanvas.taskInputs[]").description("투입 목록"),
                                fieldWithPath("data[].quickWinCanvas.taskInputs[].inputId").description("투입 ID"),
                                fieldWithPath("data[].quickWinCanvas.taskInputs[].resourceName").description("필요자원"),
                                fieldWithPath("data[].quickWinCanvas.taskInputs[].quantity").description("수량"),
                                fieldWithPath("data[].quickWinCanvas.taskInputs[].orderNo").description("순서"),
                                fieldWithPath("data[].quickWinCanvas.taskActivities[]").description("활동 목록"),
                                fieldWithPath("data[].quickWinCanvas.taskActivities[].activityId").description("활동 ID"),
                                fieldWithPath("data[].quickWinCanvas.taskActivities[].processStep").description("추진절차"),
                                fieldWithPath("data[].quickWinCanvas.taskActivities[].activityContent").description("주요내용"),
                                fieldWithPath("data[].quickWinCanvas.taskActivities[].duration").description("소요기간"),
                                fieldWithPath("data[].quickWinCanvas.taskActivities[].orderNo").description("순서"),
                                fieldWithPath("data[].quickWinCanvas.teamwork").description("팀워크").optional(),
                                fieldWithPath("data[].quickWinCanvas.teamwork.teamworkId").description("팀워크 ID"),
                                fieldWithPath("data[].quickWinCanvas.teamwork.activityTeamwork").description("팀워크 활동"),
                                fieldWithPath("data[].quickWinCanvas.teamwork.workType").description("작업 유형"),
                                fieldWithPath("data[].quickWinCanvas.taskOutcomes[]").description("성과 목록"),
                                fieldWithPath("data[].quickWinCanvas.taskOutcomes[].outcomeNo").description("성과 번호"),
                                fieldWithPath("data[].quickWinCanvas.taskOutcomes[].outcomeType").description("성과 유형(QUALITATIVE/QUANTITATIVE)").optional(),
                                fieldWithPath("data[].quickWinCanvas.taskOutcomes[].outcomeContent").description("성과 내용"),
                                fieldWithPath("data[].quickWinCanvas.taskOutcomes[].orderNo").description("순서"),

                                // Build Win Canvas
                                fieldWithPath("data[].buildWinCanvas").description("전략적 실행과제 캔버스").optional(),
                                fieldWithPath("data[].buildWinCanvas.canvasId").description("캔버스 ID").optional(),
                                fieldWithPath("data[].buildWinCanvas.strategicGoal").description("전략 목표").optional(),
                                fieldWithPath("data[].buildWinCanvas.taskName").description("과제명").optional(),
                                fieldWithPath("data[].buildWinCanvas.taskDescription").description("과제 설명").optional(),
                                fieldWithPath("data[].buildWinCanvas.crisisSignal").description("위기 신호").optional(),
                                fieldWithPath("data[].buildWinCanvas.painTouchPoint").description("고충점").optional(),
                                fieldWithPath("data[].buildWinCanvas.userId").description("작성자 ID").optional(),
                                fieldWithPath("data[].buildWinCanvas.submitted").description("제출 여부").optional(),
                                fieldWithPath("data[].buildWinCanvas.taskInputs[]").description("투입 목록"),
                                fieldWithPath("data[].buildWinCanvas.taskInputs[].inputId").description("투입 ID"),
                                fieldWithPath("data[].buildWinCanvas.taskInputs[].resourceName").description("필요자원"),
                                fieldWithPath("data[].buildWinCanvas.taskInputs[].quantity").description("수량"),
                                fieldWithPath("data[].buildWinCanvas.taskInputs[].orderNo").description("순서"),
                                fieldWithPath("data[].buildWinCanvas.taskActivities[]").description("활동 목록"),
                                fieldWithPath("data[].buildWinCanvas.taskActivities[].activityId").description("활동 ID"),
                                fieldWithPath("data[].buildWinCanvas.taskActivities[].processStep").description("추진절차"),
                                fieldWithPath("data[].buildWinCanvas.taskActivities[].activityContent").description("주요내용"),
                                fieldWithPath("data[].buildWinCanvas.taskActivities[].duration").description("소요기간"),
                                fieldWithPath("data[].buildWinCanvas.taskActivities[].orderNo").description("순서"),
                                fieldWithPath("data[].buildWinCanvas.teamwork").description("팀워크").optional(),
                                fieldWithPath("data[].buildWinCanvas.teamwork.teamworkId").description("팀워크 ID"),
                                fieldWithPath("data[].buildWinCanvas.teamwork.activityTeamwork").description("팀워크 활동"),
                                fieldWithPath("data[].buildWinCanvas.teamwork.workType").description("작업 유형"),
                                fieldWithPath("data[].buildWinCanvas.taskOutcomes[]").description("성과 목록"),
                                fieldWithPath("data[].buildWinCanvas.taskOutcomes[].outcomeNo").description("성과 번호"),
                                fieldWithPath("data[].buildWinCanvas.taskOutcomes[].outcomeType").description("성과 유형(QUALITATIVE/QUANTITATIVE)").optional(),
                                fieldWithPath("data[].buildWinCanvas.taskOutcomes[].outcomeContent").description("성과 내용"),
                                fieldWithPath("data[].buildWinCanvas.taskOutcomes[].orderNo").description("순서")
                        )
                ));
    }
}
