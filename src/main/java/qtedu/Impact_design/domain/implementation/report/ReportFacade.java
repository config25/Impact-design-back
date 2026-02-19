package qtedu.Impact_design.domain.implementation.report;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.report.ReportResponse;
import qtedu.Impact_design.domain.model.team.TbGameModel;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;

@Component
@RequiredArgsConstructor
public class ReportFacade {

    private final ReportDataLoader reportDataLoader;
    private final ReportAnalysisService reportAnalysisService;
    private final WinCanvasScoreCalculator winCanvasScoreCalculator;
    private final TbGameRepository tbGameRepository;

    @Value("${file.base-url:}")
    private String fileBaseUrl;

    @Transactional(readOnly = true)
    public ReportResponse getReport(Integer teamId) {
        ReportRawData raw = reportDataLoader.load(teamId);
        ReportAiResult aiResult = reportAnalysisService.analyze(raw);
        ReportScoreResult scoreResult = winCanvasScoreCalculator.calculate(raw);
        TbGameModel game = tbGameRepository.findByTeamId(teamId).orElse(null);

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
}
