package qtedu.Impact_design.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.request.funding.FundingInvestmentRequest;
import qtedu.Impact_design.api.dto.response.funding.*;
import qtedu.Impact_design.domain.implementation.funding.FundingAppender;
import qtedu.Impact_design.domain.implementation.funding.FundingReader;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class FundingServiceTest {

    @InjectMocks
    private FundingService fundingService;

    @Mock
    private FundingReader fundingReader;
    @Mock
    private FundingAppender fundingAppender;

    @Test
    @DisplayName("getTeamList - reader에서 팀 목록을 조회한다")
    void getTeamList() {
        FundingTeamListResponse expected = FundingTeamListResponse.builder()
                .teams(Collections.emptyList()).myTeamId(1).build();
        given(fundingReader.getTeamList("build", 1L)).willReturn(expected);

        FundingTeamListResponse result = fundingService.getTeamList("build", 1L);

        assertThat(result.getMyTeamId()).isEqualTo(1);
        then(fundingReader).should().getTeamList("build", 1L);
    }

    @Test
    @DisplayName("getInvestment - reader에서 투자 정보를 조회한다")
    void getInvestment() {
        FundingInvestmentResponse expected = FundingInvestmentResponse.builder()
                .intentIndex(1).teamName("팀1").investmentPrice("1,000,000").build();
        given(fundingReader.getInvestment("build", 1L, 10)).willReturn(expected);

        FundingInvestmentResponse result = fundingService.getInvestment("build", 1L, 10);

        assertThat(result.getTeamName()).isEqualTo("팀1");
    }

    @Test
    @DisplayName("saveInvestment - appender에 투자 저장을 위임한다")
    void saveInvestment() {
        FundingInvestmentRequest request = new FundingInvestmentRequest();
        FundingInvestmentResponse expected = FundingInvestmentResponse.builder()
                .intentIndex(1).submitted(false).build();
        given(fundingAppender.saveInvestment("build", 1L, request)).willReturn(expected);

        FundingInvestmentResponse result = fundingService.saveInvestment("build", 1L, request);

        assertThat(result.getSubmitted()).isFalse();
    }

    @Test
    @DisplayName("getPortfolio - reader에서 포트폴리오를 조회한다")
    void getPortfolio() {
        FundingPortfolioResponse expected = FundingPortfolioResponse.builder()
                .investments(Collections.emptyList()).totalInvested(50_000_000L)
                .remainingBudget(50_000_000L).maxBudget(100_000_000L).build();
        given(fundingReader.getPortfolio("build", 1L)).willReturn(expected);

        FundingPortfolioResponse result = fundingService.getPortfolio("build", 1L);

        assertThat(result.getTotalInvested()).isEqualTo(50_000_000L);
        assertThat(result.getRemainingBudget()).isEqualTo(50_000_000L);
    }

    @Test
    @DisplayName("getScores - reader에서 점수를 조회한다")
    void getScores() {
        FundingScoresResponse expected = FundingScoresResponse.builder()
                .teamScores(Collections.emptyList()).build();
        given(fundingReader.getScores("quick", 1L)).willReturn(expected);

        FundingScoresResponse result = fundingService.getScores("quick", 1L);

        assertThat(result.getTeamScores()).isEmpty();
    }

    @Test
    @DisplayName("submit - appender에 제출을 위임한다")
    void submit() {
        FundingInvestmentRequest request = new FundingInvestmentRequest();
        FundingSubmitResponse expected = FundingSubmitResponse.builder().submitted(true).build();
        given(fundingAppender.submit("build", 1L, request)).willReturn(expected);

        FundingSubmitResponse result = fundingService.submit("build", 1L, request);

        assertThat(result.getSubmitted()).isTrue();
    }

    @Test
    @DisplayName("getMyResult - reader에서 내 결과를 조회한다")
    void getMyResult() {
        FundingMyResultResponse expected = FundingMyResultResponse.builder().build();
        given(fundingReader.getMyResult(1L)).willReturn(expected);

        FundingMyResultResponse result = fundingService.getMyResult(1L);

        then(fundingReader).should().getMyResult(1L);
    }
}
