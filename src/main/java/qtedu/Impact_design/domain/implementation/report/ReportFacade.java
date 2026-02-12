package qtedu.Impact_design.domain.implementation.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.report.ReportResponse;
import qtedu.Impact_design.domain.repository.teach.TbGameRepository;

@Component
@RequiredArgsConstructor
public class ReportFacade {

    private final ReportDataLoader reportDataLoader;
    private final ReportAnalysisService reportAnalysisService;
    private final WinCanvasScoreCalculator winCanvasScoreCalculator;
    private final TbGameRepository tbGameRepository;

    @Transactional(readOnly = true)
    public ReportResponse getReport(Long userId) {
        ReportRawData raw = reportDataLoader.load(userId);
        ReportAiResult aiResult = reportAnalysisService.analyze(raw);
        ReportScoreResult scoreResult = winCanvasScoreCalculator.calculate(raw);
        String imageUrl = tbGameRepository.findGameImageUrlByUserId(userId);

        return ReportResponse.builder()
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
