package qtedu.Impact_design.domain.implementation.report;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.report.ReportResponse;
import qtedu.Impact_design.domain.implementation.game.GameReader;
import qtedu.Impact_design.domain.implementation.teach.TeachTeamReader;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReportFacade {

    private final ReportDataAggregator reportDataAggregator;
    private final ReportAnalyzer reportAnalyzer;
    private final WinCanvasScoreCalculator winCanvasScoreCalculator;
    private final GameReader gameReader;
    private final TeachTeamReader teachTeamReader;
    private final ExecutorService ioExecutor;

    public ReportFacade(
            ReportDataAggregator reportDataAggregator,
            ReportAnalyzer reportAnalyzer,
            WinCanvasScoreCalculator winCanvasScoreCalculator,
            GameReader gameReader,
            TeachTeamReader teachTeamReader,
            @Qualifier("ioExecutor") ExecutorService ioExecutor
    ) {
        this.reportDataAggregator = reportDataAggregator;
        this.reportAnalyzer = reportAnalyzer;
        this.winCanvasScoreCalculator = winCanvasScoreCalculator;
        this.gameReader = gameReader;
        this.teachTeamReader = teachTeamReader;
        this.ioExecutor = ioExecutor;
    }

    @Transactional(readOnly = true)
    public ReportResponse getReport(Integer teamId) {
        ReportRawData raw = reportDataAggregator.load(teamId);
        ReportAiResult aiResult = reportAnalyzer.analyze(raw);
        ReportScoreResult scoreResult = winCanvasScoreCalculator.calculate(raw);
        Optional<TbGameModel> gameOpt = gameReader.findByTeamId(teamId);
        Optional<TbTeamModel> teamOpt = teachTeamReader.findByTeamId(teamId);

        return ReportResponse.builder()
                .teamId(teamId)
                .teamName(teamOpt.map(TbTeamModel::getName).orElse(null))
                .className(gameOpt.map(TbGameModel::getName).orElse(null))
                .target(gameOpt.map(TbGameModel::getTarget).orElse(null))
                .projectDate(gameOpt.map(TbGameModel::getProjectDate).orElse(null))
                .imageUrl(gameOpt.map(g -> gameReader.resolveImageUrl(g.getImageUrl())).orElse(null))
                .impactCheckScores(scoreResult.getImpactCheckScores())
                .externalThreats(aiResult.getExternalThreats())
                .internalLimitations(aiResult.getInternalLimitations())
                .visionMissionValue(aiResult.getVisionMissionValue())
                .flowCanvasGoals(aiResult.getFlowCanvasGoals())
                .tacticals(aiResult.getTacticals())
                .strategicActivities(aiResult.getStrategicActivities())
                .quickWinCanvasList(scoreResult.getQuickWinCanvasList())
                .buildWinCanvasList(scoreResult.getBuildWinCanvasList())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> getReportsByGameId(Integer gameId) {
        List<Integer> teamIds = gameReader.findTeamIdsByGameId(gameId);

        // 1. 전체 팀 데이터를 한번에 배치 로드
        Map<Integer, ReportRawData> rawDataMap = reportDataAggregator.loadBulk(teamIds);

        // 2. 게임/팀 정보를 한번에 조회
        Optional<TbGameModel> gameOpt = gameReader.findById(gameId);
        Map<Integer, TbTeamModel> teamMap = teachTeamReader.findByTeamIds(teamIds).stream()
                .collect(Collectors.toMap(TbTeamModel::getTeamId, Function.identity()));

        final String finalImageUrl = gameOpt.map(g -> gameReader.resolveImageUrl(g.getImageUrl())).orElse(null);
        final String finalClassName = gameOpt.map(TbGameModel::getName).orElse(null);
        final String finalTarget = gameOpt.map(TbGameModel::getTarget).orElse(null);
        final String finalProjectDate = gameOpt.map(TbGameModel::getProjectDate).orElse(null);

        // 3. AI 분석 + 점수 계산만 팀별 병렬 실행
        List<CompletableFuture<ReportResponse>> futures = teamIds.stream()
                .map(teamId -> CompletableFuture.supplyAsync(() -> {
                    ReportRawData raw = rawDataMap.get(teamId);
                    if (raw == null) {
                        raw = ReportRawData.builder()
                                .impactChecks(List.of()).identityCanvases(List.of())
                                .flowCanvases(List.of()).tacticals(List.of())
                                .strategicActivities(List.of()).quickCanvases(List.of())
                                .buildCanvases(List.of()).quickIntents(List.of())
                                .buildIntents(List.of()).build();
                    }
                    ReportAiResult aiResult = reportAnalyzer.analyze(raw);
                    ReportScoreResult scoreResult = winCanvasScoreCalculator.calculate(raw);
                    return ReportResponse.builder()
                            .teamId(teamId)
                            .teamName(Optional.ofNullable(teamMap.get(teamId)).map(TbTeamModel::getName).orElse(null))
                            .className(finalClassName)
                            .target(finalTarget)
                            .projectDate(finalProjectDate)
                            .imageUrl(finalImageUrl)
                            .impactCheckScores(scoreResult.getImpactCheckScores())
                            .externalThreats(aiResult.getExternalThreats())
                            .internalLimitations(aiResult.getInternalLimitations())
                            .visionMissionValue(aiResult.getVisionMissionValue())
                            .flowCanvasGoals(aiResult.getFlowCanvasGoals())
                            .tacticals(aiResult.getTacticals())
                            .strategicActivities(aiResult.getStrategicActivities())
                            .quickWinCanvasList(scoreResult.getQuickWinCanvasList())
                            .buildWinCanvasList(scoreResult.getBuildWinCanvasList())
                            .build();
                }, ioExecutor))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }
}
