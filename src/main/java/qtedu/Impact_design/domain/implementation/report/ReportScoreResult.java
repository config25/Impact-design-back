package qtedu.Impact_design.domain.implementation.report;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.ImpactCheckScoreItem;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.WinCanvasWithScore;

import java.util.List;

@Getter
@Builder
public class ReportScoreResult {
    private final List<ImpactCheckScoreItem> impactCheckScores;
    private final List<WinCanvasWithScore> quickWinCanvasList;
    private final List<WinCanvasWithScore> buildWinCanvasList;
}
