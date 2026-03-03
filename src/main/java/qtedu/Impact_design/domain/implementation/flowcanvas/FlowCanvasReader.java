package qtedu.Impact_design.domain.implementation.flowcanvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.StrategicActivityModel;
import qtedu.Impact_design.domain.model.flow_canvas.TacticalModel;
import qtedu.Impact_design.domain.model.IdentityCanvasModel;
import qtedu.Impact_design.domain.repository.IdentityCanvasRepository;
import qtedu.Impact_design.domain.repository.flow_canvas.FlowCanvasRepository;
import qtedu.Impact_design.domain.repository.flow_canvas.StrategicActivityRepository;
import qtedu.Impact_design.domain.repository.flow_canvas.TacticalRepository;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FlowCanvasReader {

    private final FlowCanvasRepository flowCanvasRepository;
    private final TacticalRepository tacticalRepository;
    private final StrategicActivityRepository strategicActivityRepository;
    private final IdentityCanvasRepository identityCanvasRepository;

    public List<FlowCanvasModel> readByUserIds(List<Long> userIds) {
        return flowCanvasRepository.findByUserIdIn(userIds);
    }

    public List<TacticalModel> readTacticalsByGoalIds(List<Long> goalIds) {
        return goalIds.isEmpty() ? Collections.emptyList() : tacticalRepository.findByGoalIdIn(goalIds);
    }

    public List<StrategicActivityModel> readStrategicActivitiesByGoalIds(List<Long> goalIds) {
        return goalIds.isEmpty() ? Collections.emptyList() : strategicActivityRepository.findByGoalIdIn(goalIds);
    }

    public FlowCanvasResponse read(Long userId) {
        String newVision = identityCanvasRepository.findByUserId(userId)
                .map(IdentityCanvasModel::getNewVision)
                .orElse(null);
        List<FlowCanvasModel> goals = flowCanvasRepository.findByUserId(userId);
        if (goals.isEmpty()) {
            return FlowCanvasResponse.builder()
                    .newVision(newVision)
                    .goals(Collections.emptyList())
                    .submitted(false)
                    .build();
        }

        List<Long> goalIds = goals.stream().map(FlowCanvasModel::getGoalId).collect(Collectors.toList());

        Map<Long, List<TacticalModel>> tacticalMap = tacticalRepository.findByGoalIdIn(goalIds)
                .stream().collect(Collectors.groupingBy(TacticalModel::getGoalId));

        Map<Long, List<StrategicActivityModel>> activityMap = strategicActivityRepository.findByGoalIdIn(goalIds)
                .stream().collect(Collectors.groupingBy(StrategicActivityModel::getGoalId));

        List<FlowCanvasResponse.GoalItem> goalItems = goals.stream()
                .map(g -> FlowCanvasResponse.GoalItem.from(
                        g,
                        tacticalMap.getOrDefault(g.getGoalId(), Collections.emptyList()),
                        activityMap.getOrDefault(g.getGoalId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());

        boolean submitted = goals.stream()
                .anyMatch(g -> Boolean.TRUE.equals(g.getSubmitted()));

        return FlowCanvasResponse.builder()
                .newVision(newVision)
                .goals(goalItems)
                .submitted(submitted)
                .build();
    }
}
