package qtedu.Impact_design.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.response.wincanvas.WinCanvasResponse;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.api.dto.response.funding.FundingInvestmentResponse;
import qtedu.Impact_design.api.dto.response.funding.FundingMyResultResponse;
import qtedu.Impact_design.api.dto.response.identitycanvas.IdentityCanvasResponse;
import qtedu.Impact_design.api.dto.response.impactcheck.ImpactCheckResponse;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.api.dto.response.teach.TeamSubmissionListResponse;
import qtedu.Impact_design.domain.implementation.teach.TeachSubmissionReader;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TeachSubmissionServiceTest {

    @InjectMocks
    private TeachSubmissionService teachSubmissionService;

    @Mock
    private TeachSubmissionReader teachSubmissionReader;

    @Test
    @DisplayName("getSubmissionList - reader에서 팀별 제출 현황을 조회한다")
    void getSubmissionList() {
        List<TeamSubmissionListResponse> expected = List.of(
                TeamSubmissionListResponse.builder().teamId(1).teamName("팀1").build()
        );
        given(teachSubmissionReader.getSubmissionList(1)).willReturn(expected);

        List<TeamSubmissionListResponse> result = teachSubmissionService.getSubmissionList(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTeamName()).isEqualTo("팀1");
    }

    @Test
    @DisplayName("getImpactCheck - reader에서 팀의 임팩트체크를 조회한다")
    void getImpactCheck() {
        ImpactCheckResponse expected = ImpactCheckResponse.builder()
                .answerId(1L).q1Score(5).submitted(true).build();
        given(teachSubmissionReader.getImpactCheck(10)).willReturn(expected);

        ImpactCheckResponse result = teachSubmissionService.getImpactCheck(10);

        assertThat(result.getQ1Score()).isEqualTo(5);
    }

    @Test
    @DisplayName("getIdentityCanvas - reader에서 팀의 아이덴티티캔버스를 조회한다")
    void getIdentityCanvas() {
        IdentityCanvasResponse expected = IdentityCanvasResponse.builder()
                .identityId(1L).mission("미션").submitted(true).build();
        given(teachSubmissionReader.getIdentityCanvas(10)).willReturn(expected);

        IdentityCanvasResponse result = teachSubmissionService.getIdentityCanvas(10);

        assertThat(result.getMission()).isEqualTo("미션");
    }

    @Test
    @DisplayName("getFlowCanvas - reader에서 팀의 플로우캔버스를 조회한다")
    void getFlowCanvas() {
        FlowCanvasResponse expected = FlowCanvasResponse.builder()
                .submitted(true).goals(Collections.emptyList()).build();
        given(teachSubmissionReader.getFlowCanvas(10)).willReturn(expected);

        FlowCanvasResponse result = teachSubmissionService.getFlowCanvas(10);

        assertThat(result.getSubmitted()).isTrue();
    }

    @Test
    @DisplayName("getWinCanvas - reader에서 팀의 윈캔버스를 조회한다")
    void getWinCanvas() {
        WinCanvasResponse expected = WinCanvasResponse.builder()
                .canvasId(1L).submitted(true)
                .taskInputs(Collections.emptyList()).taskActivities(Collections.emptyList())
                .taskOutcomes(Collections.emptyList()).build();
        given(teachSubmissionReader.getWinCanvas(10, CanvasType.QUICK)).willReturn(expected);

        WinCanvasResponse result = teachSubmissionService.getWinCanvas(10, CanvasType.QUICK);

        assertThat(result.getSubmitted()).isTrue();
    }

    @Test
    @DisplayName("getFunding - reader에서 팀의 펀딩 투자 목록을 조회한다")
    void getFunding() {
        List<FundingInvestmentResponse> expected = List.of(
                FundingInvestmentResponse.builder().intentIndex(1).teamName("팀1").build()
        );
        given(teachSubmissionReader.getFunding("build", 10)).willReturn(expected);

        List<FundingInvestmentResponse> result = teachSubmissionService.getFunding("build", 10);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getFundingResult - reader에서 팀의 펀딩 결과를 조회한다")
    void getFundingResult() {
        FundingMyResultResponse expected = FundingMyResultResponse.builder().build();
        given(teachSubmissionReader.getFundingResult(10)).willReturn(expected);

        FundingMyResultResponse result = teachSubmissionService.getFundingResult(10);

        then(teachSubmissionReader).should().getFundingResult(10);
    }
}
