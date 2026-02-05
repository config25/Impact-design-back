package qtedu.Impact_design.domain.implementation.report;

import lombok.Builder;
import lombok.Getter;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.FrequencyAnalysis;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.GoalAnalysis;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.StrategicActivityItem;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.TacticalItem;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.VisionMissionValue;

import java.util.List;

@Getter
@Builder
public class ReportAiResult {
    private final FrequencyAnalysis externalThreats;
    private final FrequencyAnalysis internalLimitations;
    private final VisionMissionValue visionMissionValue;
    private final GoalAnalysis flowCanvasGoals;
    private final List<TacticalItem> tacticals;
    private final List<StrategicActivityItem> strategicActivities;
}
