package qtedu.Impact_design.domain.implementation.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.domain.service.AiService;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.StrategicActivityModel;
import qtedu.Impact_design.domain.model.flow_canvas.TacticalModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReportAnalysisService {

    private final AiService aiService;

    public ReportAiResult analyze(ReportRawData raw) {
        List<String> externalThreatData = extractStrings(raw.getIdentityCanvases(),
                IdentityCanvasModel::getMacro, IdentityCanvasModel::getTech,
                IdentityCanvasModel::getCustomer, IdentityCanvasModel::getCompetitor);

        List<String> internalLimitationData = extractStrings(raw.getIdentityCanvases(),
                IdentityCanvasModel::getCapability, IdentityCanvasModel::getCulture,
                IdentityCanvasModel::getStructure, IdentityCanvasModel::getEtc);

        List<String> visions = extractField(raw.getIdentityCanvases(), IdentityCanvasModel::getNewVision);
        List<String> missions = extractField(raw.getIdentityCanvases(), IdentityCanvasModel::getNewMission);
        List<String> values = extractField(raw.getIdentityCanvases(), IdentityCanvasModel::getNewValue);

        List<String> goalData = extractStrings(raw.getFlowCanvases(),
                FlowCanvasModel::getGoalTitle, FlowCanvasModel::getGoalDescription);

        return ReportAiResult.builder()
                .externalThreats(aiService.analyzeFrequency(externalThreatData, "외부 위협 신호"))
                .internalLimitations(aiService.analyzeFrequency(internalLimitationData, "내부 한계점"))
                .visionMissionValue(aiService.analyzeVisionMissionValue(visions, missions, values))
                .flowCanvasGoals(aiService.analyzeGoals(goalData))
                .tacticalMetrics(aiService.analyzeSimpleFrequency(
                        extractField(raw.getTacticals(), TacticalModel::getTacticalMetric), "전술 지표"))
                .tacticalGoals(aiService.analyzeSimpleFrequency(
                        extractField(raw.getTacticals(), TacticalModel::getTacticalGoal), "전술 목표"))
                .activityMetrics(aiService.analyzeSimpleFrequency(
                        extractField(raw.getStrategicActivities(), StrategicActivityModel::getActivityMetric), "전략 활동 지표"))
                .interCriteria(aiService.analyzeSimpleFrequency(
                        extractField(raw.getStrategicActivities(), StrategicActivityModel::getInterCriteria), "상호 기준"))
                .build();
    }

    @SafeVarargs
    private <T> List<String> extractStrings(List<T> items, Function<T, String>... getters) {
        List<String> result = new ArrayList<>();
        for (T item : items) {
            for (Function<T, String> getter : getters) {
                String value = getter.apply(item);
                if (value != null && !value.isBlank()) {
                    result.add(value);
                }
            }
        }
        return result;
    }

    private <T> List<String> extractField(List<T> items, Function<T, String> getter) {
        if (items == null || items.isEmpty()) return Collections.emptyList();
        return items.stream()
                .map(getter)
                .filter(v -> v != null && !v.isBlank())
                .collect(Collectors.toList());
    }
}
