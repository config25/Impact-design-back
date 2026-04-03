package qtedu.Impact_design.domain.implementation.report;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.report.ReportResponse;
import qtedu.Impact_design.api.dto.response.report.TeamCanvasResponse;
import qtedu.Impact_design.domain.implementation.game.GameReader;
import qtedu.Impact_design.domain.implementation.teach.TeachTeamReader;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReportFacade {

    private final ReportDataAggregator reportDataAggregator;
    private final ReportAnalyzer reportAnalyzer;
    private final WinCanvasScoreCalculator winCanvasScoreCalculator;
    private final TeamCanvasAggregator teamCanvasAggregator;
    private final GameReader gameReader;
    private final TeachTeamReader teachTeamReader;
    private final ExecutorService ioExecutor;

    public ReportFacade(
            ReportDataAggregator reportDataAggregator,
            ReportAnalyzer reportAnalyzer,
            WinCanvasScoreCalculator winCanvasScoreCalculator,
            TeamCanvasAggregator teamCanvasAggregator,
            GameReader gameReader,
            TeachTeamReader teachTeamReader,
            @Qualifier("ioExecutor") ExecutorService ioExecutor
    ) {
        this.reportDataAggregator = reportDataAggregator;
        this.reportAnalyzer = reportAnalyzer;
        this.winCanvasScoreCalculator = winCanvasScoreCalculator;
        this.teamCanvasAggregator = teamCanvasAggregator;
        this.gameReader = gameReader;
        this.teachTeamReader = teachTeamReader;
        this.ioExecutor = ioExecutor;
    }

    @Transactional(readOnly = true)
    public ReportResponse getReport(Integer teamId) {
        log.info("리포트 생성 시작 - teamId: {}", teamId);
        ReportRawData raw = reportDataAggregator.load(teamId);

        // AI 분석, 점수 계산, 메타데이터 조회를 병렬 실행
        CompletableFuture<ReportAiResult> aiResultFuture =
                CompletableFuture.supplyAsync(() -> reportAnalyzer.analyze(raw), ioExecutor);
        CompletableFuture<ReportScoreResult> scoreResultFuture =
                CompletableFuture.supplyAsync(() -> winCanvasScoreCalculator.calculate(raw), ioExecutor);
        CompletableFuture<Optional<TbGameModel>> gameFuture =
                CompletableFuture.supplyAsync(() -> gameReader.findByTeamId(teamId), ioExecutor);
        CompletableFuture<Optional<TbTeamModel>> teamFuture =
                CompletableFuture.supplyAsync(() -> teachTeamReader.findByTeamId(teamId), ioExecutor);

        CompletableFuture.allOf(aiResultFuture, scoreResultFuture, gameFuture, teamFuture).join();

        ReportAiResult aiResult = aiResultFuture.join();
        ReportScoreResult scoreResult = scoreResultFuture.join();
        Optional<TbGameModel> gameOpt = gameFuture.join();
        Optional<TbTeamModel> teamOpt = teamFuture.join();

        log.info("리포트 생성 완료 - teamId: {}", teamId);
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
    public List<TeamCanvasResponse> getTeamCanvases(Integer gameId) {
        log.info("전체 팀 캔버스 조회 시작 - gameId: {}", gameId);
        List<Integer> teamIds = gameReader.findTeamIdsByGameId(gameId);
        Map<Integer, TbTeamModel> teamMap = teachTeamReader.findByTeamIds(teamIds).stream()
                .collect(Collectors.toMap(TbTeamModel::getTeamId, Function.identity()));
        String imageUrl = gameReader.findById(gameId)
                .map(g -> gameReader.resolveImageUrl(g.getImageUrl()))
                .orElse(null);

        Map<Integer, Long> writerByTeam = teamCanvasAggregator.findWriterByTeam(teamIds);

        List<CompletableFuture<TeamCanvasResponse>> futures = teamIds.stream()
                .filter(writerByTeam::containsKey)
                .map(teamId -> CompletableFuture.supplyAsync(() -> {
                    Long writerId = writerByTeam.get(teamId);
                    String teamName = Optional.ofNullable(teamMap.get(teamId))
                            .map(TbTeamModel::getName).orElse(null);
                    return teamCanvasAggregator.buildForTeam(teamId, writerId, teamName, imageUrl);
                }, ioExecutor))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

}
