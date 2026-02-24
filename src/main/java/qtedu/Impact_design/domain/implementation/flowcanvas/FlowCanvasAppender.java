package qtedu.Impact_design.domain.implementation.flowcanvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.request.flowcanvas.FlowCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.flowcanvas.FlowCanvasResponse;
import qtedu.Impact_design.common.error.ConflictException;
import qtedu.Impact_design.common.error.ErrorCode;
import qtedu.Impact_design.domain.model.flow_canvas.FlowCanvasModel;
import qtedu.Impact_design.domain.model.flow_canvas.StrategicActivityModel;
import qtedu.Impact_design.domain.model.flow_canvas.TacticalModel;
import qtedu.Impact_design.domain.repository.flow_canvas.FlowCanvasRepository;
import qtedu.Impact_design.domain.repository.flow_canvas.StrategicActivityRepository;
import qtedu.Impact_design.domain.repository.flow_canvas.TacticalRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FlowCanvasAppender {

    private final FlowCanvasRepository flowCanvasRepository;
    private final TacticalRepository tacticalRepository;
    private final StrategicActivityRepository strategicActivityRepository;
    private final FlowCanvasReader flowCanvasReader;

    @Transactional
    public FlowCanvasResponse append(Long userId, FlowCanvasSaveRequest request) {
        if (flowCanvasRepository.existsSubmittedByUserId(userId)) {
            throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
        }

        List<FlowCanvasSaveRequest.GoalItem> requestGoals = request.getGoals() != null
                ? request.getGoals() : Collections.emptyList();

        // 기존 goal 전체 조회 후 orderNo 기준 Map
        Map<Integer, FlowCanvasModel> existingGoalMap = flowCanvasRepository.findByUserId(userId)
                .stream().collect(Collectors.toMap(FlowCanvasModel::getOrderNo, g -> g));

        for (FlowCanvasSaveRequest.GoalItem goalItem : requestGoals) {
            FlowCanvasModel existing = existingGoalMap.get(goalItem.getOrderNo());

            FlowCanvasModel goalModel;
            if (existing != null) {
                goalModel = FlowCanvasModel.builder()
                        .goalId(existing.getGoalId())
                        .userId(userId)
                        .goalTitle(goalItem.getGoalTitle())
                        .goalDescription(goalItem.getGoalDescription())
                        .orderNo(goalItem.getOrderNo())
                        .build();
            } else {
                goalModel = FlowCanvasModel.builder()
                        .userId(userId)
                        .goalTitle(goalItem.getGoalTitle())
                        .goalDescription(goalItem.getGoalDescription())
                        .orderNo(goalItem.getOrderNo())
                        .build();
            }

            FlowCanvasModel savedGoal = flowCanvasRepository.save(goalModel);
            Long goalId = savedGoal.getGoalId();

            mergeTacticals(goalId, goalItem.getTacticals());
            mergeActivities(goalId, goalItem.getStrategicActivities());
        }

        return flowCanvasReader.read(userId);
    }

    private void mergeTacticals(Long goalId,
                                List<FlowCanvasSaveRequest.TacticalItem> requestItems) {
        List<FlowCanvasSaveRequest.TacticalItem> items = requestItems != null
                ? requestItems : Collections.emptyList();

        Map<Integer, TacticalModel> existingMap = tacticalRepository.findByGoalIdIn(List.of(goalId))
                .stream().collect(Collectors.toMap(TacticalModel::getOrderNo, t -> t));

        for (FlowCanvasSaveRequest.TacticalItem item : items) {
            TacticalModel existing = existingMap.get(item.getOrderNo());

            TacticalModel model;
            if (existing != null) {
                model = TacticalModel.builder()
                        .metricId(existing.getMetricId())
                        .goalId(goalId)
                        .tacticalMetric(item.getTacticalMetric())
                        .tacticalGoal(item.getTacticalGoal())
                        .orderNo(item.getOrderNo())
                        .build();
            } else {
                model = TacticalModel.builder()
                        .goalId(goalId)
                        .tacticalMetric(item.getTacticalMetric())
                        .tacticalGoal(item.getTacticalGoal())
                        .orderNo(item.getOrderNo())
                        .build();
            }

            tacticalRepository.save(model);
        }
    }

    private void mergeActivities(Long goalId,
                                 List<FlowCanvasSaveRequest.StrategicActivityItem> requestItems) {
        List<FlowCanvasSaveRequest.StrategicActivityItem> items = requestItems != null
                ? requestItems : Collections.emptyList();

        Map<Integer, StrategicActivityModel> existingMap = strategicActivityRepository.findByGoalIdIn(List.of(goalId))
                .stream().collect(Collectors.toMap(StrategicActivityModel::getOrderNo, t -> t));

        for (FlowCanvasSaveRequest.StrategicActivityItem item : items) {
            StrategicActivityModel existing = existingMap.get(item.getOrderNo());

            StrategicActivityModel model;
            if (existing != null) {
                model = StrategicActivityModel.builder()
                        .activityId(existing.getActivityId())
                        .goalId(goalId)
                        .activityMetric(item.getActivityMetric())
                        .interCriteria(item.getInterCriteria())
                        .orderNo(item.getOrderNo())
                        .build();
            } else {
                model = StrategicActivityModel.builder()
                        .goalId(goalId)
                        .activityMetric(item.getActivityMetric())
                        .interCriteria(item.getInterCriteria())
                        .orderNo(item.getOrderNo())
                        .build();
            }

            strategicActivityRepository.save(model);
        }
    }

    @Transactional
    public FlowCanvasResponse submit(Long userId, FlowCanvasSaveRequest request) {
        if (flowCanvasRepository.existsSubmittedByUserId(userId)) {
            throw new ConflictException(ErrorCode.ALREADY_SUBMITTED);
        }
        FlowCanvasResponse response = append(userId, request);
        flowCanvasRepository.submitAllByUserId(userId);
        return response;
    }
}
