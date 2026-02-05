package qtedu.Impact_design.storage.repository.web.win_canvas;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import qtedu.Impact_design.domain.model.win_canvas.TaskActivityModel;
import qtedu.Impact_design.domain.repository.win_canvas.TaskActivityRepository;
import qtedu.Impact_design.storage.jpaentity.win_canvas.TaskActivity;
import qtedu.Impact_design.storage.jparepository.web.win_canvas.TaskActivityJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TaskActivityRepositoryImpl implements TaskActivityRepository {

    private final TaskActivityJpaRepository taskActivityJpaRepository;

    @Override
    public List<TaskActivityModel> findByCanvasId(Long canvasId) {
        return taskActivityJpaRepository.findByCanvasIdOrderByOrderNoAsc(canvasId)
                .stream().map(this::toModel).collect(Collectors.toList());
    }

    @Override
    public Optional<TaskActivityModel> findByCanvasIdAndOrderNo(Long canvasId, Integer orderNo) {
        return taskActivityJpaRepository.findByCanvasIdAndOrderNo(canvasId, orderNo)
                .map(this::toModel);
    }

    @Override
    public TaskActivityModel save(TaskActivityModel taskActivityModel) {
        TaskActivity entity = taskActivityModel.getActivityId() != null
                ? taskActivityJpaRepository.findById(taskActivityModel.getActivityId())
                    .map(existing -> {
                        existing.update(taskActivityModel.getProcessStep(), taskActivityModel.getActivityContent(),
                                taskActivityModel.getDuration(), taskActivityModel.getOrderNo());
                        return existing;
                    })
                    .orElseGet(() -> TaskActivity.builder()
                            .canvasId(taskActivityModel.getCanvasId())
                            .processStep(taskActivityModel.getProcessStep())
                            .activityContent(taskActivityModel.getActivityContent())
                            .duration(taskActivityModel.getDuration())
                            .orderNo(taskActivityModel.getOrderNo())
                            .build())
                : TaskActivity.builder()
                        .canvasId(taskActivityModel.getCanvasId())
                        .processStep(taskActivityModel.getProcessStep())
                        .activityContent(taskActivityModel.getActivityContent())
                        .duration(taskActivityModel.getDuration())
                        .orderNo(taskActivityModel.getOrderNo())
                        .build();
        return toModel(taskActivityJpaRepository.save(entity));
    }

    private TaskActivityModel toModel(TaskActivity entity) {
        return TaskActivityModel.builder()
                .activityId(entity.getActivityId())
                .processStep(entity.getProcessStep())
                .activityContent(entity.getActivityContent())
                .duration(entity.getDuration())
                .orderNo(entity.getOrderNo())
                .canvasId(entity.getCanvasId())
                .build();
    }
}
