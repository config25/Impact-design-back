package qtedu.Impact_design.domain.implementation.wincanvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import qtedu.Impact_design.api.dto.request.wincanvas.WinCanvasSaveRequest;
import qtedu.Impact_design.domain.model.win_canvas.*;
import qtedu.Impact_design.domain.repository.win_canvas.*;

import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WinCanvasMerger {

    private final TaskInputRepository taskInputRepository;
    private final TaskActivityRepository taskActivityRepository;
    private final TeamworkRepository teamworkRepository;
    private final TaskOutcomeRepository taskOutcomeRepository;

    public void mergeTaskInputs(Long canvasId, WinCanvasSaveRequest request) {
        if (request.getTaskInputs() == null) return;

        Map<Integer, TaskInputModel> existingMap = taskInputRepository.findByCanvasId(canvasId)
                .stream().collect(Collectors.toMap(TaskInputModel::getOrderNo, t -> t));

        for (WinCanvasSaveRequest.TaskInputItem item : request.getTaskInputs()) {
            TaskInputModel existing = existingMap.get(item.getOrderNo());
            taskInputRepository.save(TaskInputModel.builder()
                    .inputId(existing != null ? existing.getInputId() : null)
                    .canvasId(canvasId)
                    .resourceName(item.getResourceName())
                    .quantity(item.getQuantity())
                    .orderNo(item.getOrderNo())
                    .build());
        }
    }

    public void mergeTaskActivities(Long canvasId, WinCanvasSaveRequest request) {
        if (request.getTaskActivities() == null) return;

        Map<Integer, TaskActivityModel> existingMap = taskActivityRepository.findByCanvasId(canvasId)
                .stream().collect(Collectors.toMap(TaskActivityModel::getOrderNo, t -> t));

        for (WinCanvasSaveRequest.TaskActivityItem item : request.getTaskActivities()) {
            TaskActivityModel existing = existingMap.get(item.getOrderNo());
            taskActivityRepository.save(TaskActivityModel.builder()
                    .activityId(existing != null ? existing.getActivityId() : null)
                    .canvasId(canvasId)
                    .processStep(item.getProcessStep())
                    .activityContent(item.getActivityContent())
                    .duration(item.getDuration())
                    .orderNo(item.getOrderNo())
                    .build());
        }
    }

    public void mergeTeamwork(Long canvasId, WinCanvasSaveRequest request) {
        if (request.getTeamwork() == null) return;

        Long existingId = teamworkRepository.findByCanvasId(canvasId)
                .map(TeamworkModel::getTeamworkId).orElse(null);

        teamworkRepository.save(TeamworkModel.builder()
                .teamworkId(existingId)
                .canvasId(canvasId)
                .activityTeamwork(request.getTeamwork().getActivityTeamwork())
                .workType(request.getTeamwork().getWorkType())
                .build());
    }

    public void mergeTaskOutcomes(Long canvasId, WinCanvasSaveRequest request) {
        if (request.getTaskOutcomes() == null) return;

        Map<Integer, TaskOutcomeModel> existingMap = taskOutcomeRepository.findByCanvasId(canvasId)
                .stream().collect(Collectors.toMap(TaskOutcomeModel::getOrderNo, t -> t));

        for (WinCanvasSaveRequest.TaskOutcomeItem item : request.getTaskOutcomes()) {
            TaskOutcomeModel existing = existingMap.get(item.getOrderNo());
            taskOutcomeRepository.save(TaskOutcomeModel.builder()
                    .outcomeNo(existing != null ? existing.getOutcomeNo() : null)
                    .canvasId(canvasId)
                    .outcomeType(item.getOutcomeType())
                    .outcomeContent(item.getOutcomeContent())
                    .orderNo(item.getOrderNo())
                    .build());
        }
    }
}
