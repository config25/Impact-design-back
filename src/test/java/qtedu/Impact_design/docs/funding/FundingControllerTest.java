package qtedu.Impact_design.docs.funding;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import qtedu.Impact_design.api.config.SecurityConfig;
import qtedu.Impact_design.api.controller.FundingController;
import qtedu.Impact_design.api.dto.response.funding.*;
import qtedu.Impact_design.api.util.security.JwtAuthenticationFilter;
import qtedu.Impact_design.api.util.security.UserArgumentResolver;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.docs.RestDocsTestSupport;
import qtedu.Impact_design.domain.service.FundingService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
// MockMvcRequestBuilders 대신 RestDocumentationRequestBuilders 사용 (pathParameters 지원)
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = FundingController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
        ),
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = UserArgumentResolver.class
        )
)
class FundingControllerTest extends RestDocsTestSupport {

    @MockitoBean
    private FundingService fundingService;

    private String sampleInvestmentRequestJson() throws Exception {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("investmentTarget", 2);
        body.put("investmentPrice", "50000000");
        body.put("score1", 5);
        body.put("score2", 4);
        body.put("score3", 5);
        body.put("score4", 3);
        body.put("score5", 4);
        body.put("score6", 5);
        body.put("score7", 4);
        body.put("score8", 3);
        body.put("score9", 5);
        body.put("opinion", "성장 가능성이 높은 팀입니다.");
        body.put("submit", false);
        return objectMapper.writeValueAsString(body);
    }

    // ===== 1. 투자 대상 팀 목록 조회 =====

    @Test
    @DisplayName("투자 대상 팀 목록 조회")
    void getTeamList() throws Exception {
        authenticateAs(1L);
        FundingTeamListResponse response = FundingTeamListResponse.builder()
                .teams(List.of(
                        FundingTeamListResponse.TeamItem.builder()
                                .teamId(2).teamName("2팀").businessName("그린테크").build(),
                        FundingTeamListResponse.TeamItem.builder()
                                .teamId(3).teamName("3팀").businessName("에듀플러스").build()
                ))
                .myTeamId(1)
                .build();

        given(fundingService.getTeamList(anyString(), anyLong())).willReturn(response);

        mockMvc.perform(get("/api/funding/{canvasType}/teams", "build"))
                .andExpect(status().isOk())
                .andDo(document("funding/teams",
                        pathParameters(
                                parameterWithName("canvasType").description("캔버스 타입 (build 또는 quick)")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.teams[]").description("투자 대상 팀 목록"),
                                fieldWithPath("data.teams[].teamId").description("팀 ID"),
                                fieldWithPath("data.teams[].teamName").description("팀 이름"),
                                fieldWithPath("data.teams[].businessName").description("실행 과제명"),
                                fieldWithPath("data.myTeamId").description("내 팀 ID")
                        )
                ));
    }

    // ===== 2. 특정 팀 투자 정보 조회 =====

    @Test
    @DisplayName("특정 팀 투자 정보 조회")
    void getInvestment() throws Exception {
        authenticateAs(1L);
        FundingInvestmentResponse response = FundingInvestmentResponse.builder()
                .intentIndex(1)
                .investmentTarget(2)
                .teamName("2팀")
                .businessName("그린테크")
                .investmentPrice("50000000")
                .score1(5).score2(4).score3(5).score4(3).score5(4)
                .score6(5).score7(4).score8(3).score9(5)
                .opinion("성장 가능성이 높은 팀입니다.")
                .submitted(false)
                .build();

        given(fundingService.getInvestment(anyString(), anyLong(), anyInt())).willReturn(response);

        mockMvc.perform(get("/api/funding/{canvasType}/investment", "build")
                        .param("teamId", "2"))
                .andExpect(status().isOk())
                .andDo(document("funding/investment-get",
                        pathParameters(
                                parameterWithName("canvasType").description("캔버스 타입 (build 또는 quick)")
                        ),
                        queryParameters(
                                parameterWithName("teamId").description("대상 팀 ID")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.intentIndex").description("투자 의향서 인덱스").optional(),
                                fieldWithPath("data.investmentTarget").description("투자 대상 팀 ID"),
                                fieldWithPath("data.teamName").description("팀 이름"),
                                fieldWithPath("data.businessName").description("실행 과제명"),
                                fieldWithPath("data.investmentPrice").description("투자 금액"),
                                fieldWithPath("data.score1").description("문제정의 A1 점수"),
                                fieldWithPath("data.score2").description("문제정의 A2 점수"),
                                fieldWithPath("data.score3").description("솔루션 B1 점수"),
                                fieldWithPath("data.score4").description("솔루션 B2 점수"),
                                fieldWithPath("data.score5").description("솔루션 B3 점수"),
                                fieldWithPath("data.score6").description("실행력 C1 점수"),
                                fieldWithPath("data.score7").description("실행력 C2 점수"),
                                fieldWithPath("data.score8").description("파급효과 D1 점수"),
                                fieldWithPath("data.score9").description("파급효과 D2 점수"),
                                fieldWithPath("data.opinion").description("종합 의견"),
                                fieldWithPath("data.submitted").description("제출 여부")
                        )
                ));
    }

    // ===== 3. 투자 의향서 저장/수정 =====

    @Test
    @DisplayName("투자 의향서 저장")
    void saveInvestment() throws Exception {
        authenticateAs(1L);
        FundingInvestmentResponse response = FundingInvestmentResponse.builder()
                .intentIndex(1)
                .investmentTarget(2)
                .teamName("2팀")
                .businessName("그린테크")
                .investmentPrice("50000000")
                .score1(5).score2(4).score3(5).score4(3).score5(4)
                .score6(5).score7(4).score8(3).score9(5)
                .opinion("성장 가능성이 높은 팀입니다.")
                .submitted(false)
                .build();

        given(fundingService.saveInvestment(anyString(), anyLong(), any())).willReturn(response);

        mockMvc.perform(post("/api/funding/{canvasType}/investment", "build")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleInvestmentRequestJson()))
                .andExpect(status().isOk())
                .andDo(document("funding/investment-save",
                        pathParameters(
                                parameterWithName("canvasType").description("캔버스 타입 (build 또는 quick)")
                        ),
                        requestFields(
                                fieldWithPath("investmentTarget").description("투자 대상 팀 ID"),
                                fieldWithPath("investmentPrice").description("투자 금액"),
                                fieldWithPath("score1").description("문제정의 A1 점수"),
                                fieldWithPath("score2").description("문제정의 A2 점수"),
                                fieldWithPath("score3").description("솔루션 B1 점수"),
                                fieldWithPath("score4").description("솔루션 B2 점수"),
                                fieldWithPath("score5").description("솔루션 B3 점수"),
                                fieldWithPath("score6").description("실행력 C1 점수"),
                                fieldWithPath("score7").description("실행력 C2 점수"),
                                fieldWithPath("score8").description("파급효과 D1 점수"),
                                fieldWithPath("score9").description("파급효과 D2 점수"),
                                fieldWithPath("opinion").description("종합 의견"),
                                fieldWithPath("submit").description("제출 여부 (true: 제출, false: 임시저장)")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.intentIndex").description("투자 의향서 인덱스").optional(),
                                fieldWithPath("data.investmentTarget").description("투자 대상 팀 ID"),
                                fieldWithPath("data.teamName").description("팀 이름"),
                                fieldWithPath("data.businessName").description("실행 과제명"),
                                fieldWithPath("data.investmentPrice").description("투자 금액"),
                                fieldWithPath("data.score1").description("문제정의 A1 점수"),
                                fieldWithPath("data.score2").description("문제정의 A2 점수"),
                                fieldWithPath("data.score3").description("솔루션 B1 점수"),
                                fieldWithPath("data.score4").description("솔루션 B2 점수"),
                                fieldWithPath("data.score5").description("솔루션 B3 점수"),
                                fieldWithPath("data.score6").description("실행력 C1 점수"),
                                fieldWithPath("data.score7").description("실행력 C2 점수"),
                                fieldWithPath("data.score8").description("파급효과 D1 점수"),
                                fieldWithPath("data.score9").description("파급효과 D2 점수"),
                                fieldWithPath("data.opinion").description("종합 의견"),
                                fieldWithPath("data.submitted").description("제출 여부")
                        )
                ));
    }

    @Test
    @DisplayName("투자 의향서 저장 - 이미 제출 완료")
    void saveInvestment_alreadySubmitted() throws Exception {
        authenticateAs(1L);
        given(fundingService.saveInvestment(anyString(), anyLong(), any()))
                .willThrow(new ConflictException(ErrorCode.ALREADY_SUBMITTED));

        mockMvc.perform(post("/api/funding/{canvasType}/investment", "build")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleInvestmentRequestJson()))
                .andExpect(status().isConflict())
                .andDo(document("funding/investment-save-already-submitted",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("CANVAS_1"),
                                fieldWithPath("data.message").description("이미 제출 완료된 캔버스입니다.")
                        )
                ));
    }

    @Test
    @DisplayName("투자 의향서 저장 - 투자 한도 초과")
    void saveInvestment_limitExceeded() throws Exception {
        authenticateAs(1L);
        given(fundingService.saveInvestment(anyString(), anyLong(), any()))
                .willThrow(new ConflictException(ErrorCode.INVESTMENT_LIMIT_EXCEEDED));

        mockMvc.perform(post("/api/funding/{canvasType}/investment", "build")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleInvestmentRequestJson()))
                .andExpect(status().isConflict())
                .andDo(document("funding/investment-save-limit-exceeded",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("FUNDING_1"),
                                fieldWithPath("data.message").description("총 투자 금액이 1억을 초과할 수 없습니다.")
                        )
                ));
    }

    @Test
    @DisplayName("투자 의향서 저장 - 필수값 누락")
    void saveInvestment_invalidInput() throws Exception {
        authenticateAs(1L);
        given(fundingService.saveInvestment(anyString(), anyLong(), any()))
                .willThrow(new IllegalArgumentException("투자 대상은 필수입니다."));

        String requestJson = objectMapper.writeValueAsString(Map.of(
                "investmentPrice", "50000000"
        ));

        mockMvc.perform(post("/api/funding/{canvasType}/investment", "build")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andDo(document("funding/investment-save-invalid-input",
                        responseFields(
                                fieldWithPath("status").description("400"),
                                fieldWithPath("data.errorCode").description("COMMON_2"),
                                fieldWithPath("data.message").description("요청 변수가 잘못되었습니다.")
                        )
                ));
    }

    // ===== 4. 내 투자 포트폴리오 조회 =====

    @Test
    @DisplayName("내 투자 포트폴리오 조회")
    void getPortfolio() throws Exception {
        authenticateAs(1L);
        FundingPortfolioResponse response = FundingPortfolioResponse.builder()
                .investments(List.of(
                        FundingPortfolioResponse.PortfolioItem.builder()
                                .teamId(2).teamName("2팀").investmentPrice(50000000L).build(),
                        FundingPortfolioResponse.PortfolioItem.builder()
                                .teamId(3).teamName("3팀").investmentPrice(30000000L).build()
                ))
                .totalInvested(80000000L)
                .remainingBudget(20000000L)
                .maxBudget(100000000L)
                .build();

        given(fundingService.getPortfolio(anyString(), anyLong())).willReturn(response);

        mockMvc.perform(get("/api/funding/{canvasType}/portfolio", "build"))
                .andExpect(status().isOk())
                .andDo(document("funding/portfolio",
                        pathParameters(
                                parameterWithName("canvasType").description("캔버스 타입 (build 또는 quick)")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.investments[]").description("투자 목록"),
                                fieldWithPath("data.investments[].teamId").description("팀 ID"),
                                fieldWithPath("data.investments[].teamName").description("팀 이름"),
                                fieldWithPath("data.investments[].investmentPrice").description("투자 금액"),
                                fieldWithPath("data.totalInvested").description("총 투자 금액"),
                                fieldWithPath("data.remainingBudget").description("잔여 예산"),
                                fieldWithPath("data.maxBudget").description("최대 예산 (1억)")
                        )
                ));
    }

    // ===== 5. 전체 팀 평가 점수 조회 =====

    @Test
    @DisplayName("전체 팀 평가 점수 조회")
    void getScores() throws Exception {
        authenticateAs(1L);
        FundingScoresResponse response = FundingScoresResponse.builder()
                .teamScores(List.of(
                        FundingScoresResponse.TeamScoreItem.builder()
                                .teamId(1).teamName("1팀")
                                .problemScore(9).solutionScore(12).scaleUpScore(9).effectScore(8).totalScore(38)
                                .build(),
                        FundingScoresResponse.TeamScoreItem.builder()
                                .teamId(2).teamName("2팀")
                                .problemScore(8).solutionScore(11).scaleUpScore(7).effectScore(9).totalScore(35)
                                .build()
                ))
                .build();

        given(fundingService.getScores(anyString(), anyLong())).willReturn(response);

        mockMvc.perform(get("/api/funding/{canvasType}/scores", "build"))
                .andExpect(status().isOk())
                .andDo(document("funding/scores",
                        pathParameters(
                                parameterWithName("canvasType").description("캔버스 타입 (build 또는 quick)")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.teamScores[]").description("팀별 점수 목록"),
                                fieldWithPath("data.teamScores[].teamId").description("팀 ID"),
                                fieldWithPath("data.teamScores[].teamName").description("팀 이름"),
                                fieldWithPath("data.teamScores[].problemScore").description("문제정의 점수 (A1+A2)"),
                                fieldWithPath("data.teamScores[].solutionScore").description("솔루션 점수 (B1+B2+B3)"),
                                fieldWithPath("data.teamScores[].scaleUpScore").description("실행력 점수 (C1+C2)"),
                                fieldWithPath("data.teamScores[].effectScore").description("파급효과 점수 (D1+D2)"),
                                fieldWithPath("data.teamScores[].totalScore").description("총점")
                        )
                ));
    }

    // ===== 6. 제출 완료 =====

    @Test
    @DisplayName("제출 완료")
    void submit() throws Exception {
        authenticateAs(1L);
        FundingSubmitResponse submitResponse = FundingSubmitResponse.builder().submitted(true).build();
        given(fundingService.submit(anyString(), anyLong(), any())).willReturn(submitResponse);

        mockMvc.perform(patch("/api/funding/{canvasType}/submit", "build")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleInvestmentRequestJson()))
                .andExpect(status().isOk())
                .andDo(document("funding/submit",
                        pathParameters(
                                parameterWithName("canvasType").description("캔버스 타입 (build 또는 quick)")
                        ),
                        requestFields(
                                fieldWithPath("investmentTarget").description("투자 대상 팀 ID"),
                                fieldWithPath("investmentPrice").description("투자 금액"),
                                fieldWithPath("score1").description("문제정의 A1 점수"),
                                fieldWithPath("score2").description("문제정의 A2 점수"),
                                fieldWithPath("score3").description("솔루션 B1 점수"),
                                fieldWithPath("score4").description("솔루션 B2 점수"),
                                fieldWithPath("score5").description("솔루션 B3 점수"),
                                fieldWithPath("score6").description("실행력 C1 점수"),
                                fieldWithPath("score7").description("실행력 C2 점수"),
                                fieldWithPath("score8").description("파급효과 D1 점수"),
                                fieldWithPath("score9").description("파급효과 D2 점수"),
                                fieldWithPath("opinion").description("종합 의견"),
                                fieldWithPath("submit").description("제출 여부 (true: 제출, false: 임시저장)")
                        ),
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.submitted").description("제출 여부")
                        )
                ));
    }

    @Test
    @DisplayName("제출 완료 - 이미 제출됨")
    void submit_alreadySubmitted() throws Exception {
        authenticateAs(1L);
        given(fundingService.submit(anyString(), anyLong(), any()))
                .willThrow(new ConflictException(ErrorCode.ALREADY_SUBMITTED));

        mockMvc.perform(patch("/api/funding/{canvasType}/submit", "build")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sampleInvestmentRequestJson()))
                .andExpect(status().isConflict())
                .andDo(document("funding/submit-already-submitted",
                        responseFields(
                                fieldWithPath("status").description("409"),
                                fieldWithPath("data.errorCode").description("CANVAS_1"),
                                fieldWithPath("data.message").description("이미 제출 완료된 캔버스입니다.")
                        )
                ));
    }

    // ===== 7. 내 팀 결과 조회 =====

    @Test
    @DisplayName("내 팀 결과 조회")
    void getMyResult() throws Exception {
        authenticateAs(1L);
        FundingMyResultResponse response = FundingMyResultResponse.builder()
                .build(FundingMyResultResponse.ResultSection.builder()
                        .scores(FundingMyResultResponse.ScoreResult.builder()
                                .problemScore(9).solutionScore(12).scaleUpScore(9)
                                .effectScore(8).totalScore(38).evaluatorCount(5)
                                .build())
                        .opinions(List.of("좋은 사업 모델입니다.", "실현 가능성이 높습니다."))
                        .build())
                .quick(FundingMyResultResponse.ResultSection.builder()
                        .scores(FundingMyResultResponse.ScoreResult.builder()
                                .problemScore(8).solutionScore(11).scaleUpScore(7)
                                .effectScore(9).totalScore(35).evaluatorCount(4)
                                .build())
                        .opinions(List.of("빠른 실행이 가능합니다."))
                        .build())
                .build();

        given(fundingService.getMyResult(anyLong())).willReturn(response);

        mockMvc.perform(get("/api/funding/my-result"))
                .andExpect(status().isOk())
                .andDo(document("funding/my-result",
                        responseFields(
                                fieldWithPath("status").description("상태 코드"),
                                fieldWithPath("data.build").description("Build 캔버스 결과"),
                                fieldWithPath("data.build.scores").description("Build 점수"),
                                fieldWithPath("data.build.scores.problemScore").description("문제정의 점수"),
                                fieldWithPath("data.build.scores.solutionScore").description("솔루션 점수"),
                                fieldWithPath("data.build.scores.scaleUpScore").description("실행력 점수"),
                                fieldWithPath("data.build.scores.effectScore").description("파급효과 점수"),
                                fieldWithPath("data.build.scores.totalScore").description("총점"),
                                fieldWithPath("data.build.scores.evaluatorCount").description("평가 참가자 수"),
                                fieldWithPath("data.build.opinions[]").description("Build 종합 의견 목록"),
                                fieldWithPath("data.quick").description("Quick 캔버스 결과"),
                                fieldWithPath("data.quick.scores").description("Quick 점수"),
                                fieldWithPath("data.quick.scores.problemScore").description("문제정의 점수"),
                                fieldWithPath("data.quick.scores.solutionScore").description("솔루션 점수"),
                                fieldWithPath("data.quick.scores.scaleUpScore").description("실행력 점수"),
                                fieldWithPath("data.quick.scores.effectScore").description("파급효과 점수"),
                                fieldWithPath("data.quick.scores.totalScore").description("총점"),
                                fieldWithPath("data.quick.scores.evaluatorCount").description("평가 참가자 수"),
                                fieldWithPath("data.quick.opinions[]").description("Quick 종합 의견 목록")
                        )
                ));
    }
}
