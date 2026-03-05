package qtedu.Impact_design.domain.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.response.report.ReportResponse;
import qtedu.Impact_design.domain.implementation.report.ReportFacade;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock
    private ReportFacade reportFacade;

    @Test
    @DisplayName("getReport - facade에서 팀 리포트를 조회한다")
    void getReport() {
        ReportResponse expected = ReportResponse.builder()
                .teamId(10).teamName("팀1")
                .impactCheckScores(Collections.emptyList())
                .quickWinCanvasList(Collections.emptyList())
                .buildWinCanvasList(Collections.emptyList())
                .build();
        given(reportFacade.getReport(10)).willReturn(expected);

        ReportResponse result = reportService.getReport(10);

        assertThat(result.getTeamId()).isEqualTo(10);
        assertThat(result.getTeamName()).isEqualTo("팀1");
    }

    @Test
    @DisplayName("getReportsByGameId - facade에서 게임 전체 리포트를 조회한다")
    void getReportsByGameId() {
        List<ReportResponse> expected = List.of(
                ReportResponse.builder().teamId(10).teamName("팀1")
                        .impactCheckScores(Collections.emptyList())
                        .quickWinCanvasList(Collections.emptyList())
                        .buildWinCanvasList(Collections.emptyList()).build(),
                ReportResponse.builder().teamId(20).teamName("팀2")
                        .impactCheckScores(Collections.emptyList())
                        .quickWinCanvasList(Collections.emptyList())
                        .buildWinCanvasList(Collections.emptyList()).build()
        );
        given(reportFacade.getReportsByGameId(1)).willReturn(expected);

        List<ReportResponse> result = reportService.getReportsByGameId(1);

        assertThat(result).hasSize(2);
    }
}
