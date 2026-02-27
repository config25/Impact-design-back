package qtedu.Impact_design.domain.implementation.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.GoalAnalysis;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.GoalItem;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.StrategicActivityItem;
import qtedu.Impact_design.api.dto.response.report.ReportResponse.TacticalItem;
import qtedu.Impact_design.domain.implementation.ai.AiImplementation.FullReportAiResult;
import qtedu.Impact_design.domain.service.AiService;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.StrategicActivityModel;
import qtedu.Impact_design.domain.model.flow_canvas.TacticalModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReportAnalysisService {

    private final AiService aiService;

    public ReportAiResult analyze(ReportRawData raw) {
        // 데이터 추출
        List<String> externalThreatData = extractStrings(raw.getIdentityCanvases(),
                IdentityCanvasModel::getMacro, IdentityCanvasModel::getTech,
                IdentityCanvasModel::getCustomer, IdentityCanvasModel::getCompetitor);

        List<String> internalLimitationData = extractStrings(raw.getIdentityCanvases(),
                IdentityCanvasModel::getCapability, IdentityCanvasModel::getCulture,
                IdentityCanvasModel::getStructure, IdentityCanvasModel::getEtc);

        List<String> visions = extractField(raw.getIdentityCanvases(), IdentityCanvasModel::getNewVision);
        List<String> missions = extractField(raw.getIdentityCanvases(), IdentityCanvasModel::getNewMission);
        List<String> values = extractField(raw.getIdentityCanvases(), IdentityCanvasModel::getNewValue);

        List<String> goalTitles = extractField(raw.getFlowCanvases(), FlowCanvasModel::getGoalTitle);

        // tactical, strategicActivity 원본 리스트 생성
        List<TacticalItem> tacticalItems = buildTacticals(raw.getTacticals());
        List<StrategicActivityItem> strategicActivityItems = buildStrategicActivities(raw.getStrategicActivities());

        // AI에 전달할 문자열 쌍 생성
        List<String> tacticalPairs = tacticalItems.stream()
                .map(t -> t.getMetric() + " | " + t.getGoal())
                .collect(Collectors.toList());
        List<String> strategicActivityPairs = strategicActivityItems.stream()
                .map(s -> s.getActivityMetric() + " | " + s.getInterCriteria())
                .collect(Collectors.toList());

        // AI 통합 분석 (1회 호출)
        FullReportAiResult aiResult = aiService.analyzeFullReport(
                externalThreatData, internalLimitationData,
                visions, missions, values,
                goalTitles, tacticalPairs, strategicActivityPairs
        );

        // AI가 반환한 정렬 순서대로 원본 데이터 재배치
        List<TacticalItem> sortedTacticals = reorder(tacticalItems, aiResult.getTacticalOrder());
        List<StrategicActivityItem> sortedStrategicActivities = reorder(strategicActivityItems, aiResult.getStrategicActivityOrder());

        // 결과 조합
        return ReportAiResult.builder()
                .externalThreats(aiResult.getExternalThreats())
                .internalLimitations(aiResult.getInternalLimitations())
                .visionMissionValue(aiResult.getVisionMissionValue())
                .flowCanvasGoals(buildGoalAnalysis(raw.getFlowCanvases(), aiResult.getGoalKeywords()))
                .tacticals(sortedTacticals)
                .strategicActivities(sortedStrategicActivities)
                .build();
    }

    private <T> List<T> reorder(List<T> items, List<Integer> order) {
        if (items == null || items.isEmpty() || order == null || order.isEmpty()) {
            return items;
        }
        List<T> result = new ArrayList<>(items.size());
        for (Integer idx : order) {
            if (idx >= 0 && idx < items.size()) {
                result.add(items.get(idx));
            }
        }
        // 혹시 누락된 항목이 있으면 추가
        if (result.size() < items.size()) {
            Set<Integer> orderSet = new HashSet<>(order);
            for (int i = 0; i < items.size(); i++) {
                if (!orderSet.contains(i)) {
                    result.add(items.get(i));
                }
            }
        }
        return result;
    }

    private GoalAnalysis buildGoalAnalysis(List<FlowCanvasModel> flowCanvases, List<String> keywords) {
        if (flowCanvases == null || flowCanvases.isEmpty()) {
            return GoalAnalysis.builder()
                    .goals(Collections.emptyList())
                    .keywords(Collections.emptyList())
                    .build();
        }

        List<GoalItem> goals = flowCanvases.stream()
                .filter(fc -> fc.getGoalTitle() != null && !fc.getGoalTitle().isBlank())
                .map(fc -> GoalItem.builder()
                        .title(fc.getGoalTitle())
                        .description(fc.getGoalDescription())
                        .count(1)
                        .build())
                .collect(Collectors.toList());

        return GoalAnalysis.builder()
                .goals(goals)
                .keywords(keywords)
                .build();
    }

    private List<TacticalItem> buildTacticals(List<TacticalModel> tacticals) {
        if (tacticals == null || tacticals.isEmpty()) {
            return Collections.emptyList();
        }

        return tacticals.stream()
                .map(t -> TacticalItem.builder()
                        .metric(t.getTacticalMetric())
                        .goal(t.getTacticalGoal())
                        .build())
                .collect(Collectors.toList());
    }

    private List<StrategicActivityItem> buildStrategicActivities(List<StrategicActivityModel> activities) {
        if (activities == null || activities.isEmpty()) {
            return Collections.emptyList();
        }

        return activities.stream()
                .map(a -> StrategicActivityItem.builder()
                        .activityMetric(a.getActivityMetric())
                        .interCriteria(a.getInterCriteria())
                        .build())
                .collect(Collectors.toList());
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
