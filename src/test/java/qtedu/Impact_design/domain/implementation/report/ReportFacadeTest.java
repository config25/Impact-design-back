package qtedu.Impact_design.domain.implementation.report;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import qtedu.Impact_design.api.dto.response.report.ReportResponse;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.*;
import qtedu.Impact_design.domain.implementation.game.GameReader;
import qtedu.Impact_design.domain.implementation.teach.TeachTeamReader;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ReportFacadeTest {

    @Mock private ReportDataAggregator reportDataAggregator;
    @Mock private ReportAnalyzer reportAnalyzer;
    @Mock private WinCanvasScoreCalculator winCanvasScoreCalculator;
    @Mock private GameReader gameReader;
    @Mock private TeachTeamReader teachTeamReader;

    @InjectMocks
    private ReportFacade reportFacade;

    private ReportRawData emptyRawData() {
        return ReportRawData.builder()
                .impactChecks(List.of()).identityCanvases(List.of())
                .flowCanvases(List.of()).tacticals(List.of())
                .strategicActivities(List.of()).quickCanvases(List.of())
                .buildCanvases(List.of()).quickIntents(List.of())
                .buildIntents(List.of()).build();
    }

    private ReportAiResult emptyAiResult() {
        return ReportAiResult.builder()
                .externalThreats(FrequencyAnalysis.builder().build())
                .internalLimitations(FrequencyAnalysis.builder().build())
                .visionMissionValue(VisionMissionValue.builder().build())
                .flowCanvasGoals(GoalAnalysis.builder().goals(List.of()).keywords(List.of()).build())
                .tacticals(List.of()).strategicActivities(List.of()).build();
    }

    private ReportScoreResult emptyScoreResult() {
        return ReportScoreResult.builder()
                .impactCheckScores(List.of())
                .quickWinCanvasList(List.of())
                .buildWinCanvasList(List.of()).build();
    }

    @Nested
    @DisplayName("getReport - 단건 리포트 생성")
    class GetReport {

        @Test
        @DisplayName("팀/게임 정보가 있으면 리포트에 메타데이터가 포함된다")
        void returnsReportWithMetadata() {
            Integer teamId = 1;
            given(reportDataAggregator.load(teamId)).willReturn(emptyRawData());
            given(reportAnalyzer.analyze(any())).willReturn(emptyAiResult());
            given(winCanvasScoreCalculator.calculate(any())).willReturn(emptyScoreResult());
            given(gameReader.findByTeamId(teamId)).willReturn(Optional.of(
                    TbGameModel.builder().name("수업1").target("팀장급").projectDate("2026-03").imageUrl("img.png").build()));
            given(gameReader.resolveImageUrl("img.png")).willReturn("http://host/uploads/img.png");
            given(teachTeamReader.findByTeamId(teamId)).willReturn(Optional.of(
                    TbTeamModel.builder().teamId(1).name("알파팀").build()));

            ReportResponse result = reportFacade.getReport(teamId);

            assertThat(result.getTeamId()).isEqualTo(1);
            assertThat(result.getTeamName()).isEqualTo("알파팀");
            assertThat(result.getClassName()).isEqualTo("수업1");
            assertThat(result.getTarget()).isEqualTo("팀장급");
            assertThat(result.getImageUrl()).isEqualTo("http://host/uploads/img.png");
        }

        @Test
        @DisplayName("팀/게임 정보가 없으면 메타데이터가 null이다")
        void returnsNullMetadataWhenNoGameOrTeam() {
            Integer teamId = 99;
            given(reportDataAggregator.load(teamId)).willReturn(emptyRawData());
            given(reportAnalyzer.analyze(any())).willReturn(emptyAiResult());
            given(winCanvasScoreCalculator.calculate(any())).willReturn(emptyScoreResult());
            given(gameReader.findByTeamId(teamId)).willReturn(Optional.empty());
            given(teachTeamReader.findByTeamId(teamId)).willReturn(Optional.empty());

            ReportResponse result = reportFacade.getReport(teamId);

            assertThat(result.getTeamId()).isEqualTo(99);
            assertThat(result.getTeamName()).isNull();
            assertThat(result.getClassName()).isNull();
            assertThat(result.getImageUrl()).isNull();
        }

        @Test
        @DisplayName("AI 분석 결과와 점수가 리포트에 반영된다")
        void includesAiAndScoreResults() {
            Integer teamId = 1;
            given(reportDataAggregator.load(teamId)).willReturn(emptyRawData());

            ReportAiResult aiResult = ReportAiResult.builder()
                    .externalThreats(FrequencyAnalysis.builder().build())
                    .internalLimitations(FrequencyAnalysis.builder().build())
                    .visionMissionValue(VisionMissionValue.builder().build())
                    .flowCanvasGoals(GoalAnalysis.builder().goals(List.of()).keywords(List.of()).build())
                    .tacticals(List.of(TacticalItem.builder().metric("지표A").goal("목표A").build()))
                    .strategicActivities(List.of()).build();
            given(reportAnalyzer.analyze(any())).willReturn(aiResult);

            ReportScoreResult scoreResult = ReportScoreResult.builder()
                    .impactCheckScores(List.of(ImpactCheckScoreItem.builder().userId(5L).q1Score(4).build()))
                    .quickWinCanvasList(List.of())
                    .buildWinCanvasList(List.of()).build();
            given(winCanvasScoreCalculator.calculate(any())).willReturn(scoreResult);
            given(gameReader.findByTeamId(teamId)).willReturn(Optional.empty());
            given(teachTeamReader.findByTeamId(teamId)).willReturn(Optional.empty());

            ReportResponse result = reportFacade.getReport(teamId);

            assertThat(result.getTacticals()).hasSize(1);
            assertThat(result.getTacticals().get(0).getMetric()).isEqualTo("지표A");
            assertThat(result.getImpactCheckScores()).hasSize(1);
            assertThat(result.getImpactCheckScores().get(0).getQ1Score()).isEqualTo(4);
        }
    }
}
