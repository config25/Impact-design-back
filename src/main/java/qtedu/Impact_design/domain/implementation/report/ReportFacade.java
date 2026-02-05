package qtedu.Impact_design.domain.implementation.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.response.report.ReportResponse;

@Component
@RequiredArgsConstructor
public class ReportFacade {

    private final ReportDataLoader reportDataLoader;
    private final ReportAnalysisService reportAnalysisService;
    private final WinCanvasScoreCalculator winCanvasScoreCalculator;

    @Transactional(readOnly = true)
    public ReportResponse getReport(Long userId) {
        ReportRawData raw = reportDataLoader.load(userId);
        ReportAiResult aiResult = reportAnalysisService.analyze(raw);
        ReportScoreResult scoreResult = winCanvasScoreCalculator.calculate(raw);

        return ReportResponse.builder()
                .impactCheckScores(scoreResult.getImpactCheckScores())
                .externalThreats(aiResult.getExternalThreats())
                .internalLimitations(aiResult.getInternalLimitations())
                .visionMissionValue(aiResult.getVisionMissionValue())
                .flowCanvasGoals(aiResult.getFlowCanvasGoals())
                .tacticalMetrics(aiResult.getTacticalMetrics())
                .tacticalGoals(aiResult.getTacticalGoals())
                .activityMetrics(aiResult.getActivityMetrics())
                .interCriteria(aiResult.getInterCriteria())
                .quickWinCanvasList(scoreResult.getQuickWinCanvasList())
                .buildWinCanvasList(scoreResult.getBuildWinCanvasList())
                .build();
    }
}
