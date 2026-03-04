package qtedu.Impact_design.domain.implementation.report;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.report.ReportResponse;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ReportFacade {

    private final ReportDataLoader reportDataLoader;
    private final ReportAnalyzer reportAnalyzer;
    private final WinCanvasScoreCalculator winCanvasScoreCalculator;
    private final ReportReader reportReader;
    private final ExecutorService ioExecutor;

    @Value("${file.base-url:}")
    private String fileBaseUrl;

    public ReportFacade(
            ReportDataLoader reportDataLoader,
            ReportAnalyzer reportAnalyzer,
            WinCanvasScoreCalculator winCanvasScoreCalculator,
            ReportReader reportReader,
            @Qualifier("ioExecutor") ExecutorService ioExecutor
    ) {
        this.reportDataLoader = reportDataLoader;
        this.reportAnalyzer = reportAnalyzer;
        this.winCanvasScoreCalculator = winCanvasScoreCalculator;
        this.reportReader = reportReader;
        this.ioExecutor = ioExecutor;
    }

    @Transactional(readOnly = true)
    public ReportResponse getReport(Integer teamId) {
        ReportRawData raw = reportDataLoader.load(teamId);
        ReportAiResult aiResult = reportAnalyzer.analyze(raw);
        ReportScoreResult scoreResult = winCanvasScoreCalculator.calculate(raw);
        TbGameModel game = reportReader.findGameByTeamId(teamId).orElse(null);
        TbTeamModel team = reportReader.findTeamById(teamId).orElse(null);

        String imageUrl = null;
        String className = null;
        String target = null;
        String projectDate = null;
        if (game != null) {
            imageUrl = game.getImageUrl() != null && !game.getImageUrl().isBlank()
                    ? fileBaseUrl + "/" + game.getImageUrl() : null;
            className = game.getName();
            target = game.getTarget();
            projectDate = game.getProjectDate();
        }

        return ReportResponse.builder()
                .teamId(teamId)
                .teamName(team != null ? team.getName() : null)
                .className(className)
                .target(target)
                .projectDate(projectDate)
                .imageUrl(imageUrl)
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
        List<Integer> teamIds = reportReader.findTeamIdsByGameId(gameId);

        // 1. 전체 팀 데이터를 한번에 배치 로드
        Map<Integer, ReportRawData> rawDataMap = reportDataLoader.loadBulk(teamIds);

        // 2. 게임/팀 정보를 한번에 조회
        TbGameModel game = reportReader.findGameById(gameId).orElse(null);
        Map<Integer, TbTeamModel> teamMap = reportReader.findTeamsByIds(teamIds).stream()
                .collect(Collectors.toMap(TbTeamModel::getTeamId, Function.identity()));

        String imageUrl = null;
        String className = null;
        String target = null;
        String projectDate = null;
        if (game != null) {
            imageUrl = game.getImageUrl() != null && !game.getImageUrl().isBlank()
                    ? fileBaseUrl + "/" + game.getImageUrl() : null;
            className = game.getName();
            target = game.getTarget();
            projectDate = game.getProjectDate();
        }

        final String finalImageUrl = imageUrl;
        final String finalClassName = className;
        final String finalTarget = target;
        final String finalProjectDate = projectDate;

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
                    TbTeamModel team = teamMap.get(teamId);

                    return ReportResponse.builder()
                            .teamId(teamId)
                            .teamName(team != null ? team.getName() : null)
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
