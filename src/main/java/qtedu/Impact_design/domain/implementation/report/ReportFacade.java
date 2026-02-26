package qtedu.Impact_design.domain.implementation.report;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.report.ReportResponse;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.model.team.TbTeamModel;
import qtedu.Impact_design.domain.repository.auth.TbTeamRepository;
import qtedu.Impact_design.domain.repository.teach.GameTeamRepository;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
public class ReportFacade {

    private final ReportDataLoader reportDataLoader;
    private final ReportAnalysisService reportAnalysisService;
    private final WinCanvasScoreCalculator winCanvasScoreCalculator;
    private final TbGameRepository tbGameRepository;
    private final TbTeamRepository tbTeamRepository;
    private final GameTeamRepository gameTeamRepository;
    private final ExecutorService ioExecutor;

    @Value("${file.base-url:}")
    private String fileBaseUrl;

    public ReportFacade(
            ReportDataLoader reportDataLoader,
            ReportAnalysisService reportAnalysisService,
            WinCanvasScoreCalculator winCanvasScoreCalculator,
            TbGameRepository tbGameRepository,
            TbTeamRepository tbTeamRepository,
            GameTeamRepository gameTeamRepository,
            @Qualifier("ioExecutor") ExecutorService ioExecutor
    ) {
        this.reportDataLoader = reportDataLoader;
        this.reportAnalysisService = reportAnalysisService;
        this.winCanvasScoreCalculator = winCanvasScoreCalculator;
        this.tbGameRepository = tbGameRepository;
        this.tbTeamRepository = tbTeamRepository;
        this.gameTeamRepository = gameTeamRepository;
        this.ioExecutor = ioExecutor;
    }

    @Transactional(readOnly = true)
    public ReportResponse getReport(Integer teamId) {
        ReportRawData raw = reportDataLoader.load(teamId);
        ReportAiResult aiResult = reportAnalysisService.analyze(raw);
        ReportScoreResult scoreResult = winCanvasScoreCalculator.calculate(raw);
        TbGameModel game = tbGameRepository.findByTeamId(teamId).orElse(null);
        TbTeamModel team = tbTeamRepository.findByTeamId(teamId).orElse(null);

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
        List<Integer> teamIds = gameTeamRepository.findTeamIdsByGameId(gameId);

        List<CompletableFuture<ReportResponse>> futures = teamIds.stream()
                .map(teamId -> CompletableFuture.supplyAsync(
                        () -> getReport(teamId), ioExecutor
                ))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }
}
