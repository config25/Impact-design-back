package qtedu.Impact_design.domain.implementation.buildwin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import qtedu.Impact_design.api.dto.request.buildwin.BuildWinCanvasSaveRequest;
import qtedu.Impact_design.api.dto.response.buildwin.BuildWinCanvasResponse;
import qtedu.Impact_design.domain.model.en.CanvasType;
import qtedu.Impact_design.domain.model.win_canvas.*;
import qtedu.Impact_design.domain.repository.win_canvas.*;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BuildWinCanvasAppender {

    private final WinCanvasRepository winCanvasRepository;
    private final TaskInputRepository taskInputRepository;
    private final TaskActivityRepository taskActivityRepository;
    private final TeamworkRepository teamworkRepository;
    private final TaskOutcomeRepository taskOutcomeRepository;
    private final BuildWinCanvasReader buildWinCanvasReader;

    @Transactional
    public BuildWinCanvasResponse append(Long userId, BuildWinCanvasSaveRequest request) {
        WinCanvasModel canvas = winCanvasRepository.findByUserIdAndCanvasType(userId, CanvasType.BUILD)
                .map(existing -> winCanvasRepository.save(WinCanvasModel.builder()
                        .canvasId(existing.getCanvasId())
                        .userId(userId)
                        .canvasType(CanvasType.BUILD)
                        .strategicGoal(request.getStrategicGoal())
                        .taskName(request.getTaskName())
                        .taskDescription(request.getTaskDescription())
                        .crisisSignal(request.getCrisisSignal())
                        .painTouchPoint(request.getPainTouchPoint())
                        .build()))
                .orElseGet(() -> winCanvasRepository.save(WinCanvasModel.builder()
                        .userId(userId)
                        .canvasType(CanvasType.BUILD)
                        .strategicGoal(request.getStrategicGoal())
                        .taskName(request.getTaskName())
                        .taskDescription(request.getTaskDescription())
                        .crisisSignal(request.getCrisisSignal())
                        .painTouchPoint(request.getPainTouchPoint())
                        .build()));

        Long canvasId = canvas.getCanvasId();

        mergeTaskInputs(canvasId, request);
        mergeTaskActivities(canvasId, request);
        mergeTeamwork(canvasId, request);
        mergeTaskOutcomes(canvasId, request);

        return buildWinCanvasReader.read(userId);
    }

    private void mergeTaskInputs(Long canvasId, BuildWinCanvasSaveRequest request) {
        if (request.getTaskInputs() == null) return;

        Map<Integer, TaskInputModel> existingMap = taskInputRepository.findByCanvasId(canvasId)
                .stream().collect(Collectors.toMap(TaskInputModel::getOrderNo, t -> t));

        for (BuildWinCanvasSaveRequest.TaskInputItem item : request.getTaskInputs()) {
            TaskInputModel existing = existingMap.get(item.getOrderNo());
            if (existing != null) {
                taskInputRepository.save(TaskInputModel.builder()
                        .inputId(existing.getInputId())
                        .canvasId(canvasId)
                        .resourceName(item.getResourceName())
                        .quantity(item.getQuantity())
                        .orderNo(item.getOrderNo())
                        .build());
            } else {
                taskInputRepository.save(TaskInputModel.builder()
                        .canvasId(canvasId)
                        .resourceName(item.getResourceName())
                        .quantity(item.getQuantity())
                        .orderNo(item.getOrderNo())
                        .build());
            }
        }
    }

    private void mergeTaskActivities(Long canvasId, BuildWinCanvasSaveRequest request) {
        if (request.getTaskActivities() == null) return;

        Map<Integer, TaskActivityModel> existingMap = taskActivityRepository.findByCanvasId(canvasId)
                .stream().collect(Collectors.toMap(TaskActivityModel::getOrderNo, t -> t));

        for (BuildWinCanvasSaveRequest.TaskActivityItem item : request.getTaskActivities()) {
            TaskActivityModel existing = existingMap.get(item.getOrderNo());
            if (existing != null) {
                taskActivityRepository.save(TaskActivityModel.builder()
                        .activityId(existing.getActivityId())
                        .canvasId(canvasId)
                        .processStep(item.getProcessStep())
                        .activityContent(item.getActivityContent())
                        .duration(item.getDuration())
                        .orderNo(item.getOrderNo())
                        .build());
            } else {
                taskActivityRepository.save(TaskActivityModel.builder()
                        .canvasId(canvasId)
                        .processStep(item.getProcessStep())
                        .activityContent(item.getActivityContent())
                        .duration(item.getDuration())
                        .orderNo(item.getOrderNo())
                        .build());
            }
        }
    }

    private void mergeTeamwork(Long canvasId, BuildWinCanvasSaveRequest request) {
        if (request.getTeamwork() == null) return;

        teamworkRepository.findByCanvasId(canvasId)
                .ifPresentOrElse(
                        existing -> teamworkRepository.save(TeamworkModel.builder()
                                .teamworkId(existing.getTeamworkId())
                                .canvasId(canvasId)
                                .activityTeamwork(request.getTeamwork().getActivityTeamwork())
                                .workType(request.getTeamwork().getWorkType())
                                .build()),
                        () -> teamworkRepository.save(TeamworkModel.builder()
                                .canvasId(canvasId)
                                .activityTeamwork(request.getTeamwork().getActivityTeamwork())
                                .workType(request.getTeamwork().getWorkType())
                                .build())
                );
    }

    private void mergeTaskOutcomes(Long canvasId, BuildWinCanvasSaveRequest request) {
        if (request.getTaskOutcomes() == null) return;

        Map<Integer, TaskOutcomeModel> existingMap = taskOutcomeRepository.findByCanvasId(canvasId)
                .stream().collect(Collectors.toMap(TaskOutcomeModel::getOrderNo, t -> t));

        for (BuildWinCanvasSaveRequest.TaskOutcomeItem item : request.getTaskOutcomes()) {
            TaskOutcomeModel existing = existingMap.get(item.getOrderNo());
            if (existing != null) {
                taskOutcomeRepository.save(TaskOutcomeModel.builder()
                        .outcomeNo(existing.getOutcomeNo())
                        .canvasId(canvasId)
                        .outcomeType(item.getOutcomeType())
                        .outcomeContent(item.getOutcomeContent())
                        .orderNo(item.getOrderNo())
                        .build());
            } else {
                taskOutcomeRepository.save(TaskOutcomeModel.builder()
                        .canvasId(canvasId)
                        .outcomeType(item.getOutcomeType())
                        .outcomeContent(item.getOutcomeContent())
                        .orderNo(item.getOrderNo())
                        .build());
            }
        }
    }
}
